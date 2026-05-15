import { Component, OnInit } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import {
  TourService,
  ShoppingCartResponse
} from '../../../core/services/tour';

@Component({
  selector: 'app-shopping-cart',
  standalone: true,
  imports: [CommonModule, NgFor, NgIf],
  templateUrl: './shopping-cart.html',
  styleUrl: './shopping-cart.css'
})
export class ShoppingCart implements OnInit {

  cart: ShoppingCartResponse | null = null;

  errorMessage = '';
  successMessage = '';

  constructor(private tourService: TourService) {}

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    this.tourService.getShoppingCart().subscribe({
      next: (response) => {
        this.cart = response;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Nije moguće učitati korpu.';
      }
    });
  }

  removeItem(itemId: number): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.tourService.removeItemFromCart(itemId).subscribe({
      next: (response) => {
        this.cart = response;
        this.successMessage = 'Stavka je uklonjena iz korpe.';
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Nije moguće ukloniti stavku.';
      }
    });
  }

  checkout(): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.tourService.checkout().subscribe({
      next: (response) => {
        this.cart = response;
        this.successMessage = 'Kupovina uspešno završena.';
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = error?.error?.message || 'Checkout nije uspeo.';
      }
    });
  }
}