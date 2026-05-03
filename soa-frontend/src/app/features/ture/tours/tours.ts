import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { Router } from '@angular/router';
import { TourService, TourResponse } from '../../../core/services/tour';
import {  RouterModule } from '@angular/router';

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
    if (this.isTourist()) {
      this.tourService.getAllTours().subscribe({
        next: (response) => {
          this.tours = response;
        },
        error: (error) => {
          console.error(error);
          this.errorMessage = 'Nije moguće učitati ture.';
        }
      });
    } else {
      this.tourService.getMyTours().subscribe({
        next: (response) => {
          this.tours = response;
        },
        error: (error) => {
          console.error(error);
          this.errorMessage = 'Nije moguće učitati ture.';
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

  goToLocationSimulator(): void {
  this.router.navigate(['/tours/tourist-location']);
}
}