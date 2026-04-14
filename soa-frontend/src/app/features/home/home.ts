import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { NgIf } from '@angular/common';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [NgIf, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  getUsername(): string {
    return this.authService.getUsername() || 'korisniče';
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }

  goToRegister(): void {
    this.router.navigate(['/register']);
  }

  goToProfile(): void {
    this.router.navigate(['/profile']);
  }

  goToAdminUsers(): void {
    this.router.navigate(['/admin/users']);
  }

  goToBlog(): void {
    this.router.navigate(['/blog']);
  }

  goToCreateTour(): void {
  this.router.navigate(['/create-tour']);
}

goToTours(): void {
  this.router.navigate(['/tours']);
}
}
