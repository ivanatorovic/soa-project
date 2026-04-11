import { Component, OnInit } from '@angular/core';
import { NgIf } from '@angular/common';
import { UserService, ProfileResponse } from '../../../core/services/user';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  imports: [NgIf,CommonModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile implements OnInit {
  profile: ProfileResponse | null = null;
  profileImageUrl: string | null = null;
  errorMessage: string = '';

  constructor(private userService: UserService, private router: Router) {}

  ngOnInit(): void {
    this.userService.getMyProfile().subscribe({
      next: (response) => {
        this.profile = response;
        this.profileImageUrl = this.userService.getProfileImageUrl(response.profileImage);
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
    this.router.navigate(['/edit-profile']);
  }
}