import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import * as L from 'leaflet';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import {
  TourService,
  TourExecutionResponse,
  TouristLocationResponse,
  TourResponse,
} from '../../../core/services/tour';

@Component({
  selector: 'app-active-tour',
  standalone: true,
  imports: [CommonModule, NgIf, NgFor, RouterLink],
  templateUrl: './active-tour.html',
  styleUrl: './active-tour.css',
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
    private tourService: TourService,
  ) {}

  ngOnInit(): void {
    const tourId = this.route.snapshot.paramMap.get('tourId');

    console.log('[ActiveTour] ngOnInit route params:', this.route.snapshot.paramMap.keys);
    console.log('[ActiveTour] tourId param:', tourId);
    console.log('[ActiveTour] current url:', this.router.url);

    if (tourId && this.router.url.includes('/tours/start')) {
      this.isStartMode = true;
      this.tourIdToStart = Number(tourId);

      console.log('[ActiveTour] START MODE for tour:', this.tourIdToStart);

      this.tourService.getTourById(this.tourIdToStart).subscribe({
        next: (tour) => {
          console.log('[ActiveTour] loaded tour for start:', tour);

          this.tour = tour;

          setTimeout(() => {
            this.initMap();
            this.drawTourRoute();
          }, 100);
        },
        error: (error) => {
          console.error('[ActiveTour] cannot load tour:', error);
          this.errorMessage = 'Nije moguće učitati turu.';
        },
      });

      return;
    }

    console.log('[ActiveTour] ACTIVE MODE');

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
    console.log('[ActiveTour] loadActiveExecution called');

    this.tourService.getActiveTourExecution().subscribe({
      next: (response) => {
        console.log('[ActiveTour] getActiveTourExecution response:', response);

        if (!response || !response.id) {
          console.error('[ActiveTour] invalid active execution response:', response);
          this.errorMessage = 'Aktivna tura nije pronađena.';
          return;
        }

        this.execution = this.normalizeExecution(response);

        if (this.execution.status !== 'ACTIVE' && this.intervalId) {
          clearInterval(this.intervalId);
        }

        this.tourService.getTourById(this.execution.tourId).subscribe({
          next: (tour) => {
            console.log('[ActiveTour] loaded active tour details:', tour);

            this.tour = tour;
            this.drawTourRoute();
          },
          error: (error) => {
            console.error('[ActiveTour] cannot load active tour details:', error);
            this.errorMessage = 'Nije moguće učitati detalje ture.';
          },
        });
      },
      error: (error) => {
        console.error('[ActiveTour] getActiveTourExecution error:', error);
        this.errorMessage = 'Nije moguće učitati aktivnu turu.';
      },
    });
  }

  startChecking(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }

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

        this.tourService
          .checkKeyPoints(this.execution!.id, {
            latitude: location.latitude,
            longitude: location.longitude,
          })
          .subscribe({
            next: (response) => {
              console.log('[ActiveTour] checkKeyPoints response:', response);

              this.execution = this.normalizeExecution(response);

              if (response.status === 'COMPLETED') {
                this.successMessage = 'Tura je završena jer ste obišli sve ključne tačke.';

                if (this.intervalId) {
                  clearInterval(this.intervalId);
                }
              }
            },
            error: (error) => {
              console.error('[ActiveTour] checkKeyPoints error:', error);
              this.errorMessage = 'Nije moguće proveriti ključne tačke.';
            },
          });
      },
      error: (error) => {
        console.error('[ActiveTour] getTouristLocation error:', error);
        this.errorMessage = 'Nije moguće dobiti lokaciju iz simulatora.';
      },
    });
  }

  completeTour(): void {
    if (!this.execution) return;

    this.tourService.completeTourExecution(this.execution.id).subscribe({
      next: (response) => {
        console.log('[ActiveTour] completeTour response:', response);

        this.execution = this.normalizeExecution(response);
        this.successMessage = 'Tura je uspešno završena.';

        if (this.intervalId) {
          clearInterval(this.intervalId);
        }
      },
      error: (error) => {
        console.error('[ActiveTour] completeTour error:', error);
        this.errorMessage = 'Nije moguće završiti turu.';
      },
    });
  }

  abandonTour(): void {
    if (!this.execution) return;

    this.tourService.abandonTourExecution(this.execution.id).subscribe({
      next: (response) => {
        console.log('[ActiveTour] abandonTour response:', response);

        this.execution = this.normalizeExecution(response);
        this.successMessage = 'Napustili ste turu.';

        if (this.intervalId) {
          clearInterval(this.intervalId);
        }
      },
      error: (error) => {
        console.error('[ActiveTour] abandonTour error:', error);
        this.errorMessage = 'Nije moguće napustiti turu.';
      },
    });
  }

  private initMap(): void {
    if (this.map) {
      this.map.remove();
      this.map = null;
    }

    this.map = L.map('active-tour-map').setView([45.2671, 19.8335], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors',
      maxZoom: 19,
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
      },
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

    console.log('[ActiveTour] saveLocation:', lat, lng);

    this.tourService
      .updateTouristLocation({
        latitude: lat,
        longitude: lng,
      })
      .subscribe({
        next: (response) => {
          console.log('[ActiveTour] updateTouristLocation response:', response);

          this.latitude = response.latitude;
          this.longitude = response.longitude;

          if (this.isStartMode && this.tourIdToStart) {
            console.log('[ActiveTour] starting saga for tour:', this.tourIdToStart);

            this.tourService
              .startTourExecution(this.tourIdToStart, {
                latitude: response.latitude,
                longitude: response.longitude,
              })
              .subscribe({
                next: (startResponse) => {
                  console.log('[ActiveTour] startTourExecution response:', startResponse);

                  this.successMessage = 'Pokretanje ture je započeto...';

                  this.waitForActiveExecution(1);
                },
                error: (error) => {
                  console.error('[ActiveTour] startTourExecution error:', error);
                  this.errorMessage = error?.error?.message || 'Nije moguće pokrenuti turu.';
                },
              });

            return;
          }

          this.successMessage = 'Lokacija je ažurirana.';
          this.checkKeyPoints();
        },
        error: (error) => {
          console.error('[ActiveTour] updateTouristLocation error:', error);
          this.errorMessage = 'Nije moguće sačuvati lokaciju.';
        },
      });
  }

  private waitForActiveExecution(attempt: number): void {
    console.log(`[ActiveTour] waitForActiveExecution attempt ${attempt}`);

    setTimeout(() => {
      this.tourService.getActiveTourExecution().subscribe({
        next: (execution) => {
          console.log('[ActiveTour] active execution after saga:', execution);

          if (!execution || !execution.id) {
            if (attempt < 5) {
              this.waitForActiveExecution(attempt + 1);
              return;
            }

            this.errorMessage = 'Tura još nije spremna. Pokušajte ponovo za trenutak.';
            return;
          }

          this.execution = this.normalizeExecution(execution);
          this.isStartMode = false;
          this.successMessage = 'Tura je uspešno pokrenuta.';

          console.log('[ActiveTour] navigating to active execution id:', this.execution.id);

          this.router.navigate(['/tours/active', this.execution.id]);
          this.startChecking();
        },
        error: (error) => {
          console.error('[ActiveTour] getActiveTourExecution after saga error:', error);

          if (attempt < 5) {
            this.waitForActiveExecution(attempt + 1);
            return;
          }

          this.errorMessage = 'Tura još nije pokrenuta ili je došlo do greške.';
        },
      });
    }, 700);
  }

  private normalizeExecution(execution: TourExecutionResponse): TourExecutionResponse {
    return {
      ...execution,
      completedKeyPoints: execution.completedKeyPoints || [],
    };
  }

  private drawTourRoute(): void {
    if (!this.map || !this.tour || !this.tour.keyPoints || this.tour.keyPoints.length === 0) {
      return;
    }

    this.keyPointMarkers.forEach((marker) => this.map?.removeLayer(marker));
    this.keyPointMarkers = [];

    if (this.routeLine) {
      this.map.removeLayer(this.routeLine);
    }

    const routeCoordinates: L.LatLngExpression[] = this.tour.keyPoints.map((kp) => [
      kp.latitude,
      kp.longitude,
    ]);

    this.routeLine = L.polyline(routeCoordinates).addTo(this.map);

    this.tour.keyPoints.forEach((kp, index) => {
      const marker = L.marker([kp.latitude, kp.longitude])
        .addTo(this.map!)
        .bindPopup(`${index + 1}. ${kp.name}`);

      this.keyPointMarkers.push(marker);
    });

    this.map.fitBounds(this.routeLine.getBounds(), {
      padding: [30, 30],
    });
  }
}
