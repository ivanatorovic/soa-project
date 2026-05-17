import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TourResponse } from './tour';

export interface CheckoutResponse {
  purchaseId: string;
  status: string;
  message: string;
}

export interface PurchaseStatusResponse {
  purchaseId: string;
  status: 'PENDING' | 'COMPLETED' | 'FAILED';
  failureReason: string | null;
}

@Injectable({
  providedIn: 'root',
})
export class PurchaseService {
  private apiUrl = 'http://localhost:8000/api/purchase/shopping-cart';

  constructor(private http: HttpClient) {}

  getPurchasedTours(): Observable<TourResponse[]> {
    return this.http.get<TourResponse[]>(`${this.apiUrl}/purchased-tours`);
  }

  checkout(): Observable<CheckoutResponse> {
    return this.http.post<CheckoutResponse>(`${this.apiUrl}/checkout`, {});
  }

  getPurchaseStatus(purchaseId: string): Observable<PurchaseStatusResponse> {
    return this.http.get<PurchaseStatusResponse>(`${this.apiUrl}/purchases/${purchaseId}/status`);
  }
}
