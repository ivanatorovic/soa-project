import { Component, OnInit } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { Router } from '@angular/router';
import {
  TourService,
  TourResponse,
  TourExecutionResponse,
  TouristLocationResponse
} from '../../../core/services/tour';

@Component({
  selector: 'app-purchased-tours',
  standalone: true,
  imports: [CommonModule, NgIf, NgFor],
  templateUrl: './purchased-tours.html',
  styleUrl: './purchased-tours.css'
})
export class PurchasedTours implements OnInit {

  tours: TourResponse[] = [];
  activeExecution: TourExecutionResponse | null = null;

  errorMessage = '';
  successMessage = '';

  completedTourIds: number[] = [];
  constructor(
    private tourService: TourService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPurchasedTours();
    this.loadActiveTour();
    this.loadCompletedTours();
  }

  loadPurchasedTours(): void {
    this.tourService.getPurchasedTours().subscribe({
      next: (response) => {
        this.tours = response;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Nije moguće učitati kupljene ture.';
      }
    });
  }

  loadActiveTour(): void {
    this.tourService.getActiveTourExecution().subscribe({
      next: (response) => {
        this.activeExecution = response;
      },
      error: (error) => {
        if (error.status === 204) {
          this.activeExecution = null;
          return;
        }

        console.error(error);
      }
    });
  }

  startTour(tourId: number): void {
  this.errorMessage = '';
  this.successMessage = '';

  if (this.activeExecution) {
    this.errorMessage = 'Već imate aktivnu turu.';
    return;
  }

  this.router.navigate(['/tours/start', tourId]);
}

  goToActiveTour(): void {
    if (this.activeExecution) {
      this.router.navigate(['/tours/active', this.activeExecution.id]);
    }
  }

  isTourCompleted(tourId: number): boolean {
  return this.completedTourIds.includes(tourId);
}

loadCompletedTours(): void {
  this.tourService.getCompletedExecutions().subscribe({
    next: (executions) => {
      this.completedTourIds = executions.map(e => e.tourId);
    },
    error: (error) => {
      console.error(error);
    }
  });
}

}