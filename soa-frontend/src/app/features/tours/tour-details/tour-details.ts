import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TourService, TourResponse, KeyPointResponse } from '../../../core/services/tour';
import * as L from 'leaflet';

@Component({
  selector: 'app-tour-details',
  standalone: true,
  imports: [CommonModule, NgIf, NgFor],
  templateUrl: './tour-details.html',
  styleUrls: ['./tour-details.css']
})
export class TourDetails implements OnInit, OnDestroy {
  tour: TourResponse | null = null;
  errorMessage = '';

  private map: L.Map | null = null;
  private markers: L.Marker[] = [];
  private activeMarker: L.Marker | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private tourService: TourService
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
      }
    });
  }

  initMap(): void {
    this.map = L.map('tour-details-map').setView([45.2671, 19.8335], 12);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors',
      maxZoom: 19
    }).addTo(this.map);

    this.renderMarkers();

    setTimeout(() => {
      this.map?.invalidateSize();
    }, 300);
  }

  renderMarkers(): void {
  if (!this.map || !this.tour) return;

  this.markers.forEach(marker => this.map?.removeLayer(marker));
  this.markers = [];

  const validPoints = this.tour.keyPoints?.filter(
    kp => kp.latitude !== null && kp.longitude !== null
  ) || [];

  if (validPoints.length === 0) {
    this.map.setView([45.2671, 19.8335], 12);
    return;
  }

  const bounds: L.LatLngTuple[] = [];

  validPoints.forEach(kp => {
    const marker = L.marker([kp.latitude, kp.longitude])
      .addTo(this.map!)
      .bindPopup(`<b>${kp.name}</b><br>${kp.description ?? ''}`);

    marker.on('click', () => {
      this.activeMarker = marker;
    });

    this.markers.push(marker);
    bounds.push([kp.latitude, kp.longitude]);
  });

  if (bounds.length === 1) {
    this.map.setView(bounds[0], 15);
  } else {
    this.map.fitBounds(bounds, { padding: [40, 40] });
  }
}

  focusKeyPoint(kp: KeyPointResponse): void {
    if (!this.map) return;

    this.map.setView([kp.latitude, kp.longitude], 16);

    const matchingMarker = this.markers.find(marker => {
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
}