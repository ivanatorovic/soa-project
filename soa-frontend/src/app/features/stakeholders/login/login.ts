import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [FormsModule, NgIf],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  username: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  onSubmit() {
    this.errorMessage = '';

    const request = {
      username: this.username,
      password: this.password,
    };

    this.authService.login(request).subscribe({
      next: (response) => {
        this.authService.saveAuthData(response);

        console.log('Uspešan login:', response);

        this.router.navigate(['/']);
      },
      error: (error) => {
        console.error('Greška pri logovanju:', error);
        this.errorMessage = 'Neuspešna prijava. Proveri korisničko ime i lozinku.';
      },
    });
  }
}
