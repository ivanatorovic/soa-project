import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UserService, ProfileResponse } from '../../../core/services/user';
import { CommonModule } from '@angular/common';




@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile implements OnInit {
  profile: ProfileResponse | null = null;
  profileImageUrl: string | null = null;
  errorMessage: string = '';
  usernameError: string = '';

  isEditing: boolean = false;
  selectedFile: File | undefined;
  imagePreview: string | ArrayBuffer | null = null;
  passwordError: string = '';

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

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.userService.getMyProfile().subscribe({
      next: (response) => {
        this.profile = response;
        this.profileImageUrl = this.userService.getProfileImageUrl(response.profileImage);

        this.formData = {
          username: response.username || '',
          email: response.email || '',
          currentPassword: '',
          newPassword: '',
          firstName: response.firstName || '',
          lastName: response.lastName || '',
          biography: response.biography || '',
          motto: response.motto || ''
        };

        this.imagePreview = this.profileImageUrl;
      },
      error: (error) => {
        console.error('Greška pri učitavanju profila:', error);
        this.errorMessage = 'Profil nije moguće učitati.';
      }
    });
  }

  canEditProfile(): boolean {
    return this.profile?.role === 'TOURIST' || this.profile?.role === 'GUIDE';
  }

  goToEditProfile(): void {
    if (!this.profile) return;

    this.isEditing = true;
    this.errorMessage = '';
    this.passwordError = '';

    this.formData = {
      username: this.profile.username || '',
      email: this.profile.email || '',
      currentPassword: '',
      newPassword: '',
      firstName: this.profile.firstName || '',
      lastName: this.profile.lastName || '',
      biography: this.profile.biography || '',
      motto: this.profile.motto || ''
    };

    this.imagePreview = this.profileImageUrl;
    this.selectedFile = undefined;
  }

  cancelEdit(): void {
    this.isEditing = false;
    this.errorMessage = '';
    this.passwordError = '';
    this.selectedFile = undefined;
    this.imagePreview = this.profileImageUrl;
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) {
      this.selectedFile = undefined;
      this.imagePreview = this.profileImageUrl;
      return;
    }

    this.selectedFile = file;

    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result;
    };
    reader.readAsDataURL(file);
  }

  saveChanges(): void {
    this.errorMessage = '';
    this.passwordError = '';
    this.usernameError = '';

    this.userService.updateMyProfile(this.formData, this.selectedFile).subscribe({
      next: (updatedProfile) => {
        this.profile = updatedProfile;
        this.profileImageUrl = this.userService.getProfileImageUrl(updatedProfile.profileImage);
        this.imagePreview = this.profileImageUrl;
        this.isEditing = false;
        this.selectedFile = undefined;

        this.formData.currentPassword = '';
        this.formData.newPassword = '';
      },
      error: (err) => {
        console.log(err);

        if (
          err.error?.message === 'Username is already taken.' ||
          err.error?.message === 'Username already exists.' ||
          err.error === 'Username is already taken.'
        ) {
          this.usernameError = 'Već postoji korisnik sa ovim korisničkim imenom.';
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