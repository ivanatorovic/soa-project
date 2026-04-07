import { Component, OnInit } from '@angular/core';
import { NgIf } from '@angular/common';
import { UserService, ProfileResponse } from '../../../core/services/user';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile',
  imports: [NgIf,CommonModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile implements OnInit {
  profile: ProfileResponse | null = null;
  errorMessage: string = '';

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.userService.getMyProfile().subscribe({
      next: (response) => {
        this.profile = response;
      },
      error: (error) => {
        console.error('Greška pri učitavanju profila:', error);
        this.errorMessage = 'Profil nije moguće učitati.';
      }
    });
  }
}