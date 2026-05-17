import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TourService, CreateTourRequest, TourResponse } from '../../../core/services/tour';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-tour',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-tour.html',
  styleUrl: './create-tour.css',
})
export class CreateTour {
  formData = {
    name: '',
    description: '',
    difficulty: 'EASY',
    tagsInput: '',
    availableSlots: 1,
  };

  successMessage: string = '';
  errorMessage: string = '';
  createdTour: TourResponse | null = null;

  constructor(
    private tourService: TourService,
    private router: Router,
  ) {}

  createTour(): void {
    this.successMessage = '';
    this.errorMessage = '';
    this.createdTour = null;

    const tags = this.formData.tagsInput
      .split(',')
      .map((tag) => tag.trim())
      .filter((tag) => tag.length > 0);

    const request: CreateTourRequest = {
      name: this.formData.name,
      description: this.formData.description,
      difficulty: this.formData.difficulty,
      tags,
      availableSlots: this.formData.availableSlots,
    };

    this.tourService.createTour(request).subscribe({
      next: (response) => {
        this.createdTour = response;
        this.successMessage = 'Tura je uspešno kreirana.';
        setTimeout(() => {
          this.router.navigate(['/tours']);
        }, 1500);
        this.resetForm();
      },
      error: (error) => {
        console.error('Greška pri kreiranju ture:', error);
        this.errorMessage = 'Nije moguće kreirati turu.';
      },
    });
  }

  resetForm(): void {
    this.formData = {
      name: '',
      description: '',
      difficulty: 'EASY',
      tagsInput: '',
      availableSlots: 1,
    };
  }
}
