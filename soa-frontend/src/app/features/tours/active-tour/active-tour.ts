import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import * as L from 'leaflet';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import {
  TourService,
  TourExecutionResponse,
  TouristLocationResponse,
  TourResponse
} from '../../../core/services/tour';

@Component({
  selector: 'app-active-tour',
  standalone: true,
  imports: [CommonModule, NgIf, NgFor, RouterLink],
  templateUrl: './active-tour.html',
  styleUrl: './active-tour.css'
})
export class ActiveTour implements OnInit, OnDestroy {

  execution: TourExecutionResponse | null = null;
  tour: TourResponse | null = null;

  latitude: number | null = null;
  longitude: number | null = null;

  errorMessage = '';
  successMessage = '';

  private intervalId: any;

  private map: L.Map | null = null;
  private currentMarker: L.Marker | null = null;
  private routeLine: L.Polyline | null = null;
  private keyPointMarkers: L.Marker[] = [];

  tourIdToStart: number | null = null;
  isStartMode = false;

 constructor(
  private route: ActivatedRoute,
  private router: Router,
  private tourService: TourService
) {}

 ngOnInit(): void {
  const tourId = this.route.snapshot.paramMap.get('tourId');

  if (tourId) {
    this.isStartMode = true;
    this.tourIdToStart = Number(tourId);

    this.tourService.getTourById(this.tourIdToStart).subscribe({
      next: (tour) => {
        this.tour = tour;

        setTimeout(() => {
          this.initMap();
          this.drawTourRoute();
        }, 100);
      },
      error: () => {
        this.errorMessage = 'Nije moguće učitati turu.';
      }
    });

    return;
  }

  this.loadActiveExecution();

  setTimeout(() => {
    this.initMap();
    this.loadSavedLocation();
  }, 100);

  this.startChecking();
}

  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }

    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  loadActiveExecution(): void {
    this.tourService.getActiveTourExecution().subscribe({
      next: (response) => {
        this.execution = response;

        if (response.status !== 'ACTIVE' && this.intervalId) {
          clearInterval(this.intervalId);
        }

        this.tourService.getTourById(response.tourId).subscribe({
          next: (tour) => {
            this.tour = tour;
            this.drawTourRoute();
          },
          error: (error) => {
            console.error(error);
            this.errorMessage = 'Nije moguće učitati detalje ture.';
          }
        });
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Nije moguće učitati aktivnu turu.';
      }
    });
  }

  startChecking(): void {
    this.intervalId = setInterval(() => {
      this.checkKeyPoints();
    }, 10000);
  }

  checkKeyPoints(): void {
    if (!this.execution || this.execution.status !== 'ACTIVE') return;

    this.tourService.getTouristLocation().subscribe({
      next: (location: TouristLocationResponse | null) => {
        if (!location || location.latitude == null || location.longitude == null) {
          this.errorMessage = 'Kliknite na mapu da postavite trenutnu lokaciju.';
          return;
        }

        this.tourService.checkKeyPoints(this.execution!.id, {
          latitude: location.latitude,
          longitude: location.longitude
        }).subscribe({
          next: (response) => {
            this.execution = response;

            if (response.status === 'COMPLETED') {
              this.successMessage = 'Tura je završena jer ste obišli sve ključne tačke.';

              if (this.intervalId) {
                clearInterval(this.intervalId);
              }
            }
          },
          error: (error) => {
            console.error(error);
            this.errorMessage = 'Nije moguće proveriti ključne tačke.';
          }
        });
      },
      error: () => {
        this.errorMessage = 'Nije moguće dobiti lokaciju iz simulatora.';
      }
    });
  }

  completeTour(): void {
    if (!this.execution) return;

    this.tourService.completeTourExecution(this.execution.id).subscribe({
      next: (response) => {
        this.execution = response;
        this.successMessage = 'Tura je uspešno završena.';

        if (this.intervalId) {
          clearInterval(this.intervalId);
        }
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Nije moguće završiti turu.';
      }
    });
  }

  abandonTour(): void {
    if (!this.execution) return;

    this.tourService.abandonTourExecution(this.execution.id).subscribe({
      next: (response) => {
        this.execution = response;
        this.successMessage = 'Napustili ste turu.';

        if (this.intervalId) {
          clearInterval(this.intervalId);
        }
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Nije moguće napustiti turu.';
      }
    });
  }

  private initMap(): void {
    this.map = L.map('active-tour-map').setView([45.2671, 19.8335], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors',
      maxZoom: 19
    }).addTo(this.map);

    this.map.on('click', (event: L.LeafletMouseEvent) => {
      const lat = event.latlng.lat;
      const lng = event.latlng.lng;

      this.setCurrentLocationMarker(lat, lng);
      this.saveLocation(lat, lng);
    });

    setTimeout(() => {
      this.map?.invalidateSize();
      this.drawTourRoute();
    }, 300);
  }

  private loadSavedLocation(): void {
    this.tourService.getTouristLocation().subscribe({
      next: (location: TouristLocationResponse | null) => {
        if (!location || location.latitude == null || location.longitude == null) {
          return;
        }

        this.setCurrentLocationMarker(location.latitude, location.longitude);
        this.map?.setView([location.latitude, location.longitude], 15);
      },
      error: () => {
        this.errorMessage = 'Kliknite na mapu da postavite trenutnu lokaciju.';
      }
    });
  }

  private setCurrentLocationMarker(lat: number, lng: number): void {
    if (!this.map) return;

    this.latitude = lat;
    this.longitude = lng;

    if (this.currentMarker) {
      this.map.removeLayer(this.currentMarker);
    }

    this.currentMarker = L.marker([lat, lng])
      .addTo(this.map)
      .bindPopup('Vaša trenutna lokacija')
      .openPopup();
  }

  private saveLocation(lat: number, lng: number): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.tourService.updateTouristLocation({
      latitude: lat,
      longitude: lng
    }).subscribe({
     next: (response) => {
  this.latitude = response.latitude;
  this.longitude = response.longitude;

  if (this.isStartMode && this.tourIdToStart) {
    this.tourService.startTourExecution(this.tourIdToStart, {
      latitude: response.latitude,
      longitude: response.longitude
    }).subscribe({
      next: (execution) => {
        this.execution = execution;
        this.isStartMode = false;
        this.successMessage = 'Tura je uspešno pokrenuta.';

        this.router.navigate(['/tours/active', execution.id]);
        this.startChecking();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = error?.error?.message || 'Nije moguće pokrenuti turu.';
      }
    });

    return;
  }

  this.successMessage = 'Lokacija je ažurirana.';
  this.checkKeyPoints();
},
      error: () => {
        this.errorMessage = 'Nije moguće sačuvati lokaciju.';
      }
    });
  }

  private drawTourRoute(): void {
    if (!this.map || !this.tour || !this.tour.keyPoints || this.tour.keyPoints.length === 0) {
      return;
    }

    this.keyPointMarkers.forEach(marker => this.map?.removeLayer(marker));
    this.keyPointMarkers = [];

    if (this.routeLine) {
      this.map.removeLayer(this.routeLine);
    }

    const routeCoordinates: L.LatLngExpression[] = this.tour.keyPoints.map(kp => [
      kp.latitude,
      kp.longitude
    ]);

    this.routeLine = L.polyline(routeCoordinates).addTo(this.map);

    this.tour.keyPoints.forEach((kp, index) => {
      const marker = L.marker([kp.latitude, kp.longitude])
        .addTo(this.map!)
        .bindPopup(`${index + 1}. ${kp.name}`);

      this.keyPointMarkers.push(marker);
    });

    this.map.fitBounds(this.routeLine.getBounds(), {
      padding: [30, 30]
    });
  }
}