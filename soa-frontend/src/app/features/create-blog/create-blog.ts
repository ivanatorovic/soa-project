import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { BlogService } from '../../core/services/blog.service';

@Component({
  selector: 'app-create-blog',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './create-blog.html',
  styleUrls: ['./create-blog.css'],
})
export class CreateBlogComponent {
  createBlogForm: FormGroup;
  submitting = false;
  successMessage = '';
  errorMessage = '';

  selectedFiles: File[] = [];
  imageAddedMessage = '';

  constructor(
    private fb: FormBuilder,
    private blogService: BlogService,
    private router: Router,
  ) {
    this.createBlogForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.createBlogForm.get(fieldName);
    return !!field && field.invalid && field.touched;
  }

  onImagesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const files = input.files ? Array.from(input.files) : [];

    if (files.length === 0) {
      return;
    }

    this.selectedFiles = [...this.selectedFiles, ...files];
    this.imageAddedMessage = 'Slike su uspešno dodate.';

    setTimeout(() => {
      this.imageAddedMessage = '';
    }, 2000);

    input.value = '';
  }

  removeImage(index: number): void {
    this.selectedFiles.splice(index, 1);
    this.selectedFiles = [...this.selectedFiles];
  }

  onSubmit(): void {
    if (this.createBlogForm.invalid) {
      this.createBlogForm.markAllAsTouched();
      return;
    }

    if (this.submitting) {
      return;
    }

    this.submitting = true;
    this.successMessage = '';
    this.errorMessage = '';

    const info = {
      title: this.createBlogForm.value.title.trim(),
      description: this.createBlogForm.value.description.trim(),
    };

    const formData = new FormData();
    formData.append('info', JSON.stringify(info));

    this.selectedFiles.forEach((file) => {
      formData.append('images', file);
    });

    this.blogService.createBlog(formData).subscribe({
      next: () => {
        this.successMessage = 'Blog je uspešno kreiran.';
        this.submitting = false;

        setTimeout(() => {
          this.router.navigate(['/blog']);
        }, 1000);
      },
      error: (error) => {
        this.submitting = false;
        this.errorMessage =
          error?.error?.message || error?.error || 'Došlo je do greške prilikom kreiranja bloga.';
      },
    });
  }
}
