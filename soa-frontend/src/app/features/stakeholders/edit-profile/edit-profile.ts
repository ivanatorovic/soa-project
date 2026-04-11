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
      },
      error: () => {
        this.errorMessage = 'Greška pri učitavanju profila.';
      }
    });
  }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

saveChanges() {
  this.errorMessage = '';
  this.passwordError = '';

  this.userService.updateMyProfile(this.formData, this.selectedFile).subscribe({
    next: () => {
      this.router.navigate(['/profile']);
    },
    error: (err) => {
      console.log(err);

      if (
        err.error?.message === 'Username is already taken.' ||
        err.error?.message === 'Username already exists.' ||
        err.error === 'Username is already taken.'
      ) {
        this.errorMessage = 'Već postoji korisnik ovim username-om.';
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