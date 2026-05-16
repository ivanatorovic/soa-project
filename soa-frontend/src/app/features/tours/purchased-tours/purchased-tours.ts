import { Component, OnInit } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { Router } from '@angular/router';
import {
  TourService,
  TourResponse,
  TourExecutionResponse
} from '../../../core/services/tour';

import { PurchaseService } from '../../../core/services/purchase';

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
  completedTourIds: number[] = [];

  errorMessage = '';
  successMessage = '';
  completedExecutions: TourExecutionResponse[] = [];

  constructor(
    private tourService: TourService,
    private purchaseService: PurchaseService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPurchasedTours();
    this.loadActiveTour();
    this.loadCompletedTours();
  }

  loadPurchasedTours(): void {
    this.purchaseService.getPurchasedTours().subscribe({
      next: (response: TourResponse[]) => {
        this.tours = response;
      },
      error: (error: any) => {
        console.error(error);
        this.errorMessage = 'Nije moguće učitati kupljene ture.';
      }
    });
  }

  loadActiveTour(): void {
    this.tourService.getActiveTourExecution().subscribe({
      next: (response: TourExecutionResponse) => {
        this.activeExecution = response;
      },
      error: (error: any) => {
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
    next: (executions: TourExecutionResponse[]) => {
      this.completedExecutions = executions;
      this.completedTourIds = executions.map(e => e.tourId);
    },
    error: (error: any) => {
      console.error(error);
    }
  });
}

  getCompletedExecution(tourId: number): TourExecutionResponse | undefined {
  return this.completedExecutions.find(e => e.tourId === tourId);
}
}