import { Component, OnInit } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TourService } from '../../../core/services/tour';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-create-review',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIf, NgFor],
  templateUrl: './create-review.html',
  styleUrls: ['./create-review.css'],
})
export class CreateReview implements OnInit {
  tourId!: number;

  rating = 0;
  comment = '';
  images: File[] = [];
  imagePreviews: string[] = [];

  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private tourService: TourService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (!id) {
      this.errorMessage = 'ID ture nije pronađen.';
      return;
    }

    if (this.authService.getRole() !== 'TOURIST') {
      this.router.navigate(['/tours', Number(id), 'reviews']);
      return;
    }

    this.tourId = Number(id);
  }

  setRating(value: number): void {
    this.rating = value;
  }

  onImagesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) return;

    const selectedFiles = Array.from(input.files);

    selectedFiles.forEach((file) => {
      this.images.push(file);

      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreviews.push(reader.result as string);
      };
      reader.readAsDataURL(file);
    });

    input.value = '';
  }

  removeImage(index: number): void {
    this.images.splice(index, 1);
    this.imagePreviews.splice(index, 1);
  }

  submitReview(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.rating < 1 || this.rating > 5) {
      this.errorMessage = 'Moraš izabrati ocenu od 1 do 5.';
      return;
    }

    if (!this.comment.trim()) {
      this.errorMessage = 'Komentar je obavezan.';
      return;
    }

    const formData = new FormData();

    const info = {
      rating: this.rating,
      comment: this.comment,
      visitedAt: new Date().toISOString().split('T')[0],
    };

    formData.append('info', JSON.stringify(info));

    this.images.forEach((image) => {
      formData.append('images', image, image.name);
    });

    this.loading = true;

    this.tourService.createReview(this.tourId, formData).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Recenzija je uspešno dodata.';

        setTimeout(() => {
          this.router.navigate(['/tours', this.tourId, 'reviews']);
        }, 700);
      },
      error: (error) => {
        console.error(error);
        this.loading = false;
        this.errorMessage = 'Nije moguće dodati recenziju.';
      },
    });
  }

  goBack(): void {
    this.router.navigate(['/tours', this.tourId, 'reviews']);
  }
}
