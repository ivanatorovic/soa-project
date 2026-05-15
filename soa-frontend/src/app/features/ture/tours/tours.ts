import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { Router } from '@angular/router';
import { TourService, TourResponse } from '../../../core/services/tour';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-tours',
  standalone: true,
  imports: [CommonModule, NgFor, NgIf, RouterModule],
  templateUrl: './tours.html',
  styleUrl: './tours.css'
})
export class Tours implements OnInit, OnDestroy {
  tours: TourResponse[] = [];
  errorMessage: string = '';
  successMessage: string = '';

  heroImages: string[] = [
    '/slika.jpg',
    '/slika1.jpg',
    '/slika2.jpeg'
  ];

  currentSlide: number = 0;
  private sliderInterval: any;

  constructor(
    private tourService: TourService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadTours();
    this.startSlider();
  }

  ngOnDestroy(): void {
    if (this.sliderInterval) {
      clearInterval(this.sliderInterval);
    }
  }

  loadTours(): void {
    this.errorMessage = '';

    if (this.isTourist()) {
      this.tourService.getPublishedTours().subscribe({
        next: (response) => {
          this.tours = response;
        },
        error: (error) => {
          console.error(error);
          this.errorMessage = 'Nije moguće učitati objavljene ture.';
        }
      });
    } else {
      this.tourService.getMyTours().subscribe({
        next: (response) => {
          this.tours = response;
        },
        error: (error) => {
          console.error(error);
          this.errorMessage = 'Nije moguće učitati vaše ture.';
        }
      });
    }
  }

  startSlider(): void {
    this.sliderInterval = setInterval(() => {
      this.currentSlide = (this.currentSlide + 1) % this.heroImages.length;
    }, 4000);
  }

  goToCreateTour(): void {
    this.router.navigate(['/create-tour']);
  }

  isTourist(): boolean {
    return localStorage.getItem('role') === 'TOURIST';
  }

  isGuideOrAdmin(): boolean {
    const role = localStorage.getItem('role');
    return role === 'GUIDE' || role === 'ADMIN';
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

  getStatusLabel(status: string | undefined): string {
    switch (status) {
      case 'DRAFT':
        return 'Draft';
      case 'PUBLISHED':
        return 'Objavljena';
      case 'ARCHIVED':
        return 'Arhivirana';
      default:
        return status || '';
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

  publishTour(tourId: number, event: Event): void {
    event.stopPropagation();
    this.errorMessage = '';
    this.successMessage = '';

    this.tourService.publishTour(tourId).subscribe({
      next: () => {
        this.successMessage = 'Tura je uspešno objavljena.';
        this.loadTours();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = error?.error?.message || 'Nije moguće objaviti turu.';
      }
    });
  }

  archiveTour(tourId: number, event: Event): void {
    event.stopPropagation();
    this.errorMessage = '';
    this.successMessage = '';

    this.tourService.archiveTour(tourId).subscribe({
      next: () => {
        this.successMessage = 'Tura je uspešno arhivirana.';
        this.loadTours();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = error?.error?.message || 'Nije moguće arhivirati turu.';
      }
    });
  }

  reactivateTour(tourId: number, event: Event): void {
    event.stopPropagation();
    this.errorMessage = '';
    this.successMessage = '';

    this.tourService.reactivateTour(tourId).subscribe({
      next: () => {
        this.successMessage = 'Tura je ponovo aktivirana.';
        this.loadTours();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = error?.error?.message || 'Nije moguće ponovo aktivirati turu.';
      }
    });
  }
  addToCart(tourId: number, event: Event): void {
  event.stopPropagation();
  this.errorMessage = '';
  this.successMessage = '';

  this.tourService.addTourToCart(tourId).subscribe({
    next: () => {
      this.successMessage = 'Tura je dodata u korpu.';
    },
    error: (error) => {
      console.error(error);
      this.errorMessage = error?.error?.message || 'Nije moguće dodati turu u korpu.';
    }
  });
}

  goToLocationSimulator(): void {
    this.router.navigate(['/tours/tourist-location']);
  }
}
