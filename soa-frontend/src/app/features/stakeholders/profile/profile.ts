import { Component, OnInit } from '@angular/core';
import { NgIf } from '@angular/common';
import { UserService, ProfileResponse } from '../../../core/services/user';

@Component({
  selector: 'app-profile',
  imports: [NgIf],
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