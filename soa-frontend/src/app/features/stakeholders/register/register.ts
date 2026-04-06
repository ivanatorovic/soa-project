import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-register',
  imports: [FormsModule, NgIf],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  username: string = '';
  password: string = '';
  confirmPassword: string = '';
  email: string = '';
  role: string = 'TOURIST';

  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.password !== this.confirmPassword) {
      this.errorMessage = 'Lozinke se ne poklapaju.';
      return;
    }

    const request = {
      username: this.username,
      password: this.password,
      email: this.email,
      role: this.role
    };

    this.authService.register(request).subscribe({
      next: () => {
        this.successMessage = 'Registracija je uspešna. Sada možeš da se prijaviš.';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1500);
      },
      error: (error) => {
        console.error('Greška pri registraciji:', error);

        if (error.status === 400) {
          this.errorMessage = 'Podaci nisu ispravni ili korisnik već postoji.';
        } else {
          this.errorMessage = 'Registracija nije uspela.';
        }
      }
    });
  }
}