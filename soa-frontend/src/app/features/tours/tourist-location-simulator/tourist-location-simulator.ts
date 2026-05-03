import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { Router } from '@angular/router';
import * as L from 'leaflet';
import {
  TourService,
  TouristLocationResponse,
  TouristLocationRequest
} from '../../../core/services/tour';

@Component({
  selector: 'app-tourist-location-simulator',
  standalone: true,
  imports: [CommonModule, NgIf],
  templateUrl: './tourist-location-simulator.html',
  styleUrl: './tourist-location-simulator.css'
})
export class TouristLocationSimulator implements OnInit, OnDestroy {
  private map: L.Map | null = null;
  private currentMarker: L.Marker | null = null;

  latitude: number | null = null;
  longitude: number | null = null;

  successMessage = '';
  errorMessage = '';
  hasSavedLocation = false;

  constructor(
    private tourService: TourService,
    private router: Router
  ) {}

  ngOnInit(): void {
    setTimeout(() => {
      this.initMap();
      this.loadSavedLocation();
    }, 100);
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  private initMap(): void {
    this.map = L.map('tourist-location-map').setView([45.2671, 19.8335], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors',
      maxZoom: 19
    }).addTo(this.map);

    this.map.on('click', (event: L.LeafletMouseEvent) => {
      const lat = event.latlng.lat;
      const lng = event.latlng.lng;

      this.setMarker(lat, lng);
      this.saveLocation(lat, lng);
    });

    setTimeout(() => {
      this.map?.invalidateSize();
    }, 300);
  }

  private loadSavedLocation(): void {
    this.tourService.getTouristLocation().subscribe({
      next: (location: TouristLocationResponse) => {
        if (!location) {
          return;
        }

        this.hasSavedLocation = true;
        this.setMarker(location.latitude, location.longitude);

        this.map?.setView([location.latitude, location.longitude], 15);
      },
      error: (error) => {
        if (error.status === 204) {
          this.hasSavedLocation = false;
          return;
        }

        console.error('Greška pri učitavanju lokacije:', error);
        this.errorMessage = 'Nije moguće učitati prethodnu lokaciju.';
      }
    });
  }

  private setMarker(lat: number, lng: number): void {
    if (!this.map) {
      return;
    }

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
    this.successMessage = '';
    this.errorMessage = '';

    const request: TouristLocationRequest = {
      latitude: lat,
      longitude: lng
    };

    this.tourService.updateTouristLocation(request).subscribe({
      next: (response) => {
        this.latitude = response.latitude;
        this.longitude = response.longitude;
        this.hasSavedLocation = true;
        this.successMessage = 'Trenutna lokacija je uspešno sačuvana.';
      },
      error: (error) => {
        console.error('Greška pri čuvanju lokacije:', error);
        this.errorMessage = 'Nije moguće sačuvati trenutnu lokaciju.';
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/tours']);
  }
}