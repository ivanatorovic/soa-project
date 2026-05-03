import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {
  TourService,
  TourResponse,
  CreateKeyPointRequest,
  KeyPointResponse
} from '../../../core/services/tour';
import * as L from 'leaflet';

delete (L.Icon.Default.prototype as any)._getIconUrl;

L.Icon.Default.mergeOptions({
  iconRetinaUrl: '/marker-icon-2x.png',
  iconUrl: '/marker-icon.png',
  shadowUrl: '/marker-shadow.png'
});

@Component({
  selector: 'app-add-key-point',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIf, NgFor],
  templateUrl: './add-key-point.html',
  styleUrls: ['./add-key-point.css']
})
export class AddKeyPoint implements OnInit, OnDestroy {
  tourId!: number;
  tour: TourResponse | null = null;

  selectedImage: File | null = null;
  previewImageUrl: string | null = null;

  editMode = false;
  selectedKeyPointId: number | null = null;

  formData: CreateKeyPointRequest = {
    name: '',
    description: '',
    latitude: 0,
    longitude: 0,
    imageUrl: ''
  };

  successMessage = '';
  errorMessage = '';

  private map: L.Map | null = null;
  private marker: L.Marker | null = null;
  private existingMarkers: L.Marker[] = [];

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

    this.tourId = Number(id);
    this.loadTour();
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
      this.selectedImage = input.files[0];

      const reader = new FileReader();
      reader.onload = () => {
        this.previewImageUrl = reader.result as string;
      };
      reader.readAsDataURL(this.selectedImage);
    }
  }

  loadTour(): void {
    this.tourService.getTourById(this.tourId).subscribe({
      next: (response) => {
        this.tour = response;

        setTimeout(() => {
          if (!this.map) {
            this.initMap();
          } else {
            this.renderExistingMarkers();
            this.map.invalidateSize();
          }
        }, 100);
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Nije moguće učitati turu.';
      }
    });
  }

  initMap(): void {
    if (this.map) return;

    this.map = L.map('map').setView([45.2671, 19.8335], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors',
      maxZoom: 19
    }).addTo(this.map);

    this.map.on('click', (e: L.LeafletMouseEvent) => {
      const lat = Number(e.latlng.lat.toFixed(6));
      const lng = Number(e.latlng.lng.toFixed(6));

      this.formData.latitude = lat;
      this.formData.longitude = lng;

      this.setMarker(lat, lng);
    });

    this.renderExistingMarkers();

    setTimeout(() => {
      this.map?.invalidateSize();
    }, 300);
  }

  renderExistingMarkers(): void {
    if (!this.map || !this.tour) return;

    this.existingMarkers.forEach(marker => this.map?.removeLayer(marker));
    this.existingMarkers = [];

    this.tour.keyPoints.forEach(kp => {
      const existingMarker = L.marker([kp.latitude, kp.longitude]).addTo(this.map!);

      existingMarker.bindPopup(`<b>${kp.name}</b><br>${kp.description ?? ''}`);

      existingMarker.on('click', () => {
        this.startEdit(kp);
      });

      this.existingMarkers.push(existingMarker);
    });
  }

  setMarker(lat: number, lng: number): void {
    if (!this.map) return;

    if (this.marker) {
      this.map.removeLayer(this.marker);
    }

    this.marker = L.marker([lat, lng]).addTo(this.map);
    this.map.setView([lat, lng], 15);
  }

  updateMarkerFromInputs(): void {
    if (!isNaN(this.formData.latitude) && !isNaN(this.formData.longitude)) {
      this.setMarker(this.formData.latitude, this.formData.longitude);
    }
  }

  fillDemoCoordinates(): void {
    this.formData.latitude = 45.2671;
    this.formData.longitude = 19.8335;
    this.setMarker(this.formData.latitude, this.formData.longitude);
  }

  startEdit(kp: KeyPointResponse): void {
    this.editMode = true;
    this.selectedKeyPointId = kp.id;

    this.formData = {
      name: kp.name,
      description: kp.description,
      latitude: kp.latitude,
      longitude: kp.longitude,
      imageUrl: kp.imageUrl ?? ''
    };

    this.previewImageUrl = kp.imageUrl ? this.getImageUrl(kp.imageUrl) : null;
    this.selectedImage = null;

    this.setMarker(kp.latitude, kp.longitude);
  }

  saveKeyPoint(): void {
    this.successMessage = '';
    this.errorMessage = '';

    if (!this.formData.name.trim()) {
      this.errorMessage = 'Naziv je obavezan.';
      return;
    }

    if (!this.formData.description.trim()) {
      this.errorMessage = 'Opis je obavezan.';
      return;
    }

    if (this.editMode && this.selectedKeyPointId !== null) {
      this.tourService.updateKeyPoint(
        this.tourId,
        this.selectedKeyPointId,
        this.formData,
        this.selectedImage
      ).subscribe({
        next: () => {
          this.successMessage = 'Ključna tačka je uspešno izmenjena.';
          this.resetForm();
          this.loadTour();
        },
        error: (error) => {
          console.error(error);
          this.errorMessage = 'Nije moguće izmeniti ključnu tačku.';
        }
      });
    } else {
      this.tourService.addKeyPoint(this.tourId, this.formData, this.selectedImage).subscribe({
        next: () => {
          this.successMessage = 'Ključna tačka je uspešno dodata.';
          this.resetForm();
          this.loadTour();
        },
        error: (error) => {
          console.error(error);
          this.errorMessage = 'Nije moguće sačuvati ključnu tačku.';
        }
      });
    }
  }

  deleteKeyPoint(kp: KeyPointResponse): void {
    this.successMessage = '';
    this.errorMessage = '';

    const confirmed = confirm(`Da li sigurno želiš da obrišeš ključnu tačku "${kp.name}"?`);
    if (!confirmed) return;

    this.tourService.deleteKeyPoint(this.tourId, kp.id).subscribe({
      next: () => {
        this.successMessage = 'Ključna tačka je uspešno obrisana.';

        if (this.selectedKeyPointId === kp.id) {
          this.resetForm();
        }

        this.loadTour();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Nije moguće obrisati ključnu tačku.';
      }
    });
  }

  resetForm(): void {
    this.editMode = false;
    this.selectedKeyPointId = null;
    this.selectedImage = null;
    this.previewImageUrl = null;

    this.formData = {
      name: '',
      description: '',
      latitude: 0,
      longitude: 0,
      imageUrl: ''
    };

    if (this.marker && this.map) {
      this.map.removeLayer(this.marker);
      this.marker = null;
    }
  }

  goBack(): void {
    this.router.navigate(['/tours']);
  }

  focusKeyPoint(kp: { latitude: number; longitude: number }): void {
    this.setMarker(kp.latitude, kp.longitude);

    if (this.map) {
      this.map.setView([kp.latitude, kp.longitude], 16);
    }
  }

  getImageUrl(imageUrl: string | undefined): string {
    if (!imageUrl) {
      return '';
    }

    if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
      return imageUrl;
    }

    return `http://localhost:8083${imageUrl}`;
  }
}