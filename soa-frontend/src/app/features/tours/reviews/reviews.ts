import { Component, OnInit } from '@angular/core';
import { CommonModule, NgFor, NgIf, DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TourService, ReviewResponse } from '../../../core/services/tour';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-reviews',
  standalone: true,
  imports: [CommonModule, NgIf, NgFor, DatePipe],
  templateUrl: './reviews.html',
  styleUrls: ['./reviews.css'],
})
export class Reviews implements OnInit {
  reviews: ReviewResponse[] = [];
  loading = false;
  errorMessage = '';
  tourId!: number;
  isTourist = false;
  currentImageIndex: { [key: number]: number } = {};

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private tourService: TourService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    this.isTourist = this.authService.getRole() === 'TOURIST';

    if (!id) {
      this.errorMessage = 'ID ture nije pronađen.';
      return;
    }

    this.tourId = Number(id);
    this.loadReviews();
  }

  loadReviews(): void {
    this.loading = true;

    this.tourService.getReviewsForTour(this.tourId).subscribe({
      next: (response) => {
        this.reviews = response;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Nije moguće učitati recenzije.';
        this.loading = false;
      },
    });
  }

  goBack(): void {
    this.router.navigate(['/tours', this.tourId]);
  }

  getStars(rating: number): string {
    return '★'.repeat(rating) + '☆'.repeat(5 - rating);
  }

  getImageUrl(imageUrl: string): string {
    if (!imageUrl) return '';
    if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
      return imageUrl;
    }
    return `http://localhost:8083${imageUrl}`;
  }

  goToCreateReview(): void {
    this.router.navigate(['/tours', this.tourId, 'create-review']);
  }

  getCurrentImage(review: any): string {
    const index = this.currentImageIndex[review.id] ?? 0;
    return review.imageUrls[index];
  }

  nextImage(review: any): void {
    const index = this.currentImageIndex[review.id] ?? 0;
    this.currentImageIndex[review.id] = (index + 1) % review.imageUrls.length;
  }

  prevImage(review: any): void {
    const index = this.currentImageIndex[review.id] ?? 0;
    this.currentImageIndex[review.id] =
      (index - 1 + review.imageUrls.length) % review.imageUrls.length;
  }
}
