import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TourService, TourResponse, KeyPointResponse } from '../../../core/services/tour';
import * as L from 'leaflet';

@Component({
  selector: 'app-tour-details',
  standalone: true,
  imports: [CommonModule, NgIf, NgFor, FormsModule],
  templateUrl: './tour-details.html',
  styleUrls: ['./tour-details.css'],
})
export class TourDetails implements OnInit, OnDestroy {
  tour: TourResponse | null = null;
  errorMessage = '';
  successMessage = '';

  reviewCount = 0;

  transportForm = {
    transportType: 'WALKING',
    durationMinutes: 120,
  };

  private map: L.Map | null = null;
  private markers: L.Marker[] = [];
  private activeMarker: L.Marker | null = null;
  private routeLine: L.Polyline | null = null;
  showRoute = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private tourService: TourService,
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (!id) {
      this.errorMessage = 'ID ture nije pronađen.';
      return;
    }

    const tourId = Number(id);
    this.loadTour(tourId);
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  loadTour(tourId: number): void {
    this.tourService.getTourById(tourId).subscribe({
      next: (response) => {
        this.tour = response;

        this.tourService.getReviewCount(tourId).subscribe({
          next: (count) => (this.reviewCount = count),
          error: () => (this.reviewCount = 0),
        });

        setTimeout(() => {
          if (!this.map) {
            this.initMap();
          } else {
            this.renderMarkers();
            this.map.invalidateSize();
          }
        }, 100);
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Nije moguće učitati detalje ture.';
      },
    });
  }

  addTransportTime(): void {
    if (!this.tour) return;

    this.errorMessage = '';
    this.successMessage = '';

    if (!this.transportForm.durationMinutes || this.transportForm.durationMinutes <= 0) {
      this.errorMessage = 'Vreme obilaska mora biti veće od 0 minuta.';
      return;
    }

    this.tourService
      .addTransportTime(
        this.tour.id,
        this.transportForm.transportType,
        this.transportForm.durationMinutes
      )
      .subscribe({
        next: (response) => {
          this.tour = response;
          this.successMessage = 'Vreme obilaska je uspešno dodato.';
        },
        error: (error) => {
          console.error(error);
          this.errorMessage = error?.error?.message || 'Nije moguće dodati vreme obilaska.';
        },
      });
  }

  initMap(): void {
    this.map = L.map('tour-details-map').setView([45.2671, 19.8335], 12);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors',
      maxZoom: 19,
    }).addTo(this.map);

    this.renderMarkers();

    setTimeout(() => {
      this.map?.invalidateSize();
    }, 300);
  }

  renderMarkers(): void {
    if (!this.map || !this.tour) return;

    this.markers.forEach((marker) => this.map?.removeLayer(marker));
    this.markers = [];

    if (this.routeLine) {
      this.map.removeLayer(this.routeLine);
      this.routeLine = null;
    }

    const validPoints =
      this.tour.keyPoints?.filter((kp) => kp.latitude !== null && kp.longitude !== null) || [];

    if (validPoints.length === 0) {
      this.map.setView([45.2671, 19.8335], 12);
      return;
    }

    const bounds: L.LatLngTuple[] = [];

    validPoints.forEach((kp) => {
      const marker = L.marker([kp.latitude, kp.longitude])
        .addTo(this.map!)
        .bindPopup(`<b>${kp.name}</b><br>${kp.description ?? ''}`);

      marker.on('click', () => {
        this.activeMarker = marker;
      });

      this.markers.push(marker);
      bounds.push([kp.latitude, kp.longitude]);
    });

    if (this.showRoute && bounds.length >= 2) {
      this.routeLine = L.polyline(bounds, {
        color: '#d62828',
        weight: 5,
        opacity: 0.95,
        dashArray: '12, 8',
        lineCap: 'round',
        lineJoin: 'round',
      }).addTo(this.map);
    }

    if (bounds.length === 1) {
      this.map.setView(bounds[0], 15);
    } else {
      this.map.fitBounds(bounds, { padding: [40, 40] });
    }
  }

  focusKeyPoint(kp: KeyPointResponse): void {
    if (!this.map) return;

    this.map.setView([kp.latitude, kp.longitude], 16);

    const matchingMarker = this.markers.find((marker) => {
      const position = marker.getLatLng();
      return position.lat === kp.latitude && position.lng === kp.longitude;
    });

    if (matchingMarker) {
      matchingMarker.openPopup();
      this.activeMarker = matchingMarker;
    }
  }

  getTourIcon(difficulty: string | undefined): string {
    switch (difficulty) {
      case 'EASY':
        return '🌿';
      case 'MEDIUM':
        return '🧭';
      case 'HARD':
        return '⛰️';
      default:
        return '🌍';
    }
  }

  getTransportLabel(type: string): string {
    switch (type) {
      case 'WALKING':
        return 'Peške';
      case 'BICYCLE':
        return 'Bicikl';
      case 'CAR':
        return 'Automobil';
      default:
        return type;
    }
  }

  isTourist(): boolean {
    return localStorage.getItem('role') === 'TOURIST';
  }

  getImageUrl(imageUrl: string | undefined): string {
    if (!imageUrl) return '';
    if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
      return imageUrl;
    }
    return `http://localhost:8083${imageUrl}`;
  }

  goBack(): void {
    this.router.navigate(['/tours']);
  }

  toggleRoute(): void {
    this.showRoute = !this.showRoute;
    this.renderMarkers();
  }

  goToReviews(): void {
    if (!this.tour) return;

    this.router.navigate(['/tours', this.tour.id, 'reviews']);
  }

  startTour(): void {
  if (!this.tour) return;

  this.errorMessage = '';
  this.successMessage = '';

  this.tourService.getTouristLocation().subscribe({
    next: (location) => {
      const request = {
        latitude: location.latitude,
        longitude: location.longitude
      };

      this.tourService.startTourExecution(this.tour!.id, request).subscribe({
        next: (execution) => {
          this.router.navigate(['/tours/active', execution.id]);
        },
        error: (error) => {
          console.error(error);
          this.errorMessage = error?.error?.message || 'Nije moguće pokrenuti turu.';
        }
      });
    },
    error: () => {
      this.errorMessage = 'Prvo podesite lokaciju u simulatoru pozicije.';
    }
  });
}
}
