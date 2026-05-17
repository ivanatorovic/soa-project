import { Component, OnInit } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { Router } from '@angular/router';
import { TourService, ShoppingCartResponse } from '../../../core/services/tour';
import { PurchaseService } from '../../../core/services/purchase';
import { interval, Subscription, switchMap, takeWhile } from 'rxjs';

@Component({
  selector: 'app-shopping-cart',
  standalone: true,
  imports: [CommonModule, NgFor, NgIf],
  templateUrl: './shopping-cart.html',
  styleUrl: './shopping-cart.css',
})
export class ShoppingCart implements OnInit {
  cart: ShoppingCartResponse | null = null;

  errorMessage = '';
  successMessage = '';
  processingCheckout = false;

  private statusSubscription?: Subscription;

  constructor(
    private tourService: TourService,
    private purchaseService: PurchaseService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadCart();
  }

  ngOnDestroy(): void {
    this.statusSubscription?.unsubscribe();
  }

  loadCart(): void {
    this.tourService.getShoppingCart().subscribe({
      next: (response) => {
        this.cart = response;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Nije moguće učitati korpu.';
      },
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
      },
    });
  }

  checkout(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.processingCheckout = true;

    this.purchaseService.checkout().subscribe({
      next: (response) => {
        this.successMessage = 'Kupovina se obrađuje...';
        this.watchPurchaseStatus(response.purchaseId);
      },
      error: (error) => {
        console.error(error);
        this.processingCheckout = false;
        this.errorMessage = error?.error?.message || 'Checkout nije uspeo.';
      },
    });
  }

  private watchPurchaseStatus(purchaseId: string): void {
    this.statusSubscription?.unsubscribe();

    this.statusSubscription = interval(1000)
      .pipe(
        switchMap(() => this.purchaseService.getPurchaseStatus(purchaseId)),
        takeWhile((status) => status.status === 'PENDING', true),
      )
      .subscribe({
        next: (status) => {
          if (status.status === 'COMPLETED') {
            this.processingCheckout = false;
            this.successMessage = 'Kupovina je uspešno završena.';
            this.loadCart();

            setTimeout(() => {
              this.router.navigate(['/tours']);
            }, 1200);
          }

          if (status.status === 'FAILED') {
            this.processingCheckout = false;
            this.successMessage = '';
            this.errorMessage = status.failureReason || 'Kupovina nije uspela.';
            this.loadCart();
          }
        },
        error: (error) => {
          console.error(error);
          this.processingCheckout = false;
          this.errorMessage = 'Nije moguće proveriti status kupovine.';
        },
      });
  }
}
