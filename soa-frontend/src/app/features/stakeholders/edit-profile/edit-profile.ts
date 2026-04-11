import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService, ProfileResponse } from '../../../core/services/user';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-profile.html',
  styleUrl: './edit-profile.css'
})
export class EditProfileComponent implements OnInit {

  profile: ProfileResponse | null = null;
  imagePreview: string | ArrayBuffer | null = null;

  formData = {
    username: '',
    email: '',
    currentPassword: '',
    newPassword: '',
    firstName: '',
    lastName: '',
    biography: '',
    motto: ''
  };

  selectedFile: File | undefined;
  errorMessage = '';
  passwordError = '';

  constructor(
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userService.getMyProfile().subscribe({
      next: (res) => {
        this.profile = res;

        this.formData = {
          username: res.username,
          email: res.email,
          currentPassword: '',
          newPassword: '',
          firstName: res.firstName || '',
          lastName: res.lastName || '',
          biography: res.biography || '',
          motto: res.motto || ''
        };

        if (res.profileImage) {
          this.imagePreview = 'http://localhost:8081' + res.profileImage;
        }
      },
      error: () => {
        this.errorMessage = 'Greška pri učitavanju profila.';
      }
    });
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) {
      this.selectedFile = undefined;
      this.imagePreview = this.profile?.profileImage || null;
      return;
    }

    this.selectedFile = file;

    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result;
    };
    reader.readAsDataURL(file);
  }

  saveChanges() {
    this.errorMessage = '';
    this.passwordError = '';

    this.userService.updateMyProfile(this.formData, this.selectedFile).subscribe({
      next: () => {
        this.router.navigate(['/profile'], { replaceUrl: true });
      },
      error: (err) => {
        console.log(err);

        if (
          err.error?.message === 'Username is already taken.' ||
          err.error?.message === 'Username already exists.' ||
          err.error === 'Username is already taken.'
        ) {
          this.errorMessage = 'Već postoji korisnik sa ovim korisničkim imenom.';
        } else if (
          err.error?.message === 'Email is already taken.' ||
          err.error?.message === 'Email already exists.' ||
          err.error === 'Email is already taken.'
        ) {
          this.errorMessage = 'Već postoji korisnik sa ovim email-om.';
        } else if (err.error?.message === 'Current password is incorrect.') {
          this.passwordError = 'Stara lozinka nije tačna.';
        } else if (err.error?.message === 'Both current password and new password are required.') {
          this.passwordError = 'Morate uneti i staru i novu lozinku.';
        } else {
          this.errorMessage = 'Greška pri čuvanju.';
        }
      }
    });
  }
}