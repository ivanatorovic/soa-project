import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CreateTourRequest {
  name: string;
  description: string;
  difficulty: string;
  tags: string[];
}

export interface CreateKeyPointRequest {
  name: string;
  description: string;
  latitude: number;
  longitude: number;
  imageUrl: string;
}

export interface KeyPointResponse {
  id: number;
  name: string;
  description: string;
  latitude: number;
  longitude: number;
  imageUrl: string;
}

export interface OrderItemResponse {
  id: number;
  tourId: number;
  tourName: string;
  price: number;
}

export interface ShoppingCartResponse {
  id: number;
  touristId: number;
  totalPrice: number;
  items: OrderItemResponse[];
}

export interface TourTransportTimeResponse {
  id: number;
  transportType: string;
  durationMinutes: number;
}

export interface TourResponse {
  id: number;
  name: string;
  description: string;
  difficulty: string;
  price: number;
  status: string;
  inShoppingCart?: boolean;
purchased?: boolean;
  authorId: number;
  tags: string[];
  keyPoints: KeyPointResponse[];

  publishedAt?: string;
  archivedAt?: string | null;
  distanceInKm?: number;
  transportTimes?: TourTransportTimeResponse[];
}

export interface TouristLocationRequest {
  latitude: number;
  longitude: number;
}

export interface TouristLocationResponse {
  latitude: number;
  longitude: number;
}

export interface ReviewResponse {
  id: number;
  tourId: number;
  rating: number;
  comment: string;
  touristId: number;
  touristUsername: string;
  visitedAt: string;
  createdAt: string;
  imageUrls: string[];
}

export interface StartTourExecutionRequest {
  latitude: number;
  longitude: number;
}

export interface CheckKeyPointRequest {
  latitude: number;
  longitude: number;
}

export interface CompletedKeyPointResponse {
  keyPointId: number;
  keyPointName: string;
  reachedAt: string;
}

export interface TourExecutionResponse {
  id: number;
  tourId: number;
  tourName: string;
  status: string;
  startedAt: string;
  completedAt?: string;
  abandonedAt?: string;
  lastActivityAt: string;
  completedKeyPoints: CompletedKeyPointResponse[];
}

@Injectable({
  providedIn: 'root',
})
export class TourService {
  private apiUrl = 'http://localhost:8000/api/tours';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt');

    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }

  createTour(request: CreateTourRequest): Observable<TourResponse> {
    return this.http.post<TourResponse>(this.apiUrl, request, {
      headers: this.getAuthHeaders(),
    });
  }
getShoppingCart(): Observable<ShoppingCartResponse> {
  return this.http.get<ShoppingCartResponse>(`${this.apiUrl}/shopping-cart`, {
    headers: this.getAuthHeaders(),
  });
}

addTourToCart(tourId: number): Observable<ShoppingCartResponse> {
  return this.http.post<ShoppingCartResponse>(
    `${this.apiUrl}/shopping-cart/items/${tourId}`,
    {},
    {
      headers: this.getAuthHeaders(),
    }
  );
}

removeItemFromCart(itemId: number): Observable<ShoppingCartResponse> {
  return this.http.delete<ShoppingCartResponse>(
    `${this.apiUrl}/shopping-cart/items/${itemId}`,
    {
      headers: this.getAuthHeaders(),
    }
  );
}

checkout(): Observable<ShoppingCartResponse> {
  return this.http.post<ShoppingCartResponse>(
    `${this.apiUrl}/shopping-cart/checkout`,
    {},
    {
      headers: this.getAuthHeaders(),
    }
  );
}
  getMyTours(): Observable<TourResponse[]> {
    return this.http.get<TourResponse[]>(`${this.apiUrl}/my`, {
      headers: this.getAuthHeaders(),
    });
  }

  getAllTours(): Observable<TourResponse[]> {
    return this.http.get<TourResponse[]>(this.apiUrl, {
      headers: this.getAuthHeaders(),
    });
  }

  getPublishedTours(): Observable<TourResponse[]> {
    return this.http.get<TourResponse[]>(`${this.apiUrl}/published`, {
      headers: this.getAuthHeaders(),
    });
  }

  getTourById(tourId: number): Observable<TourResponse> {
    return this.http.get<TourResponse>(`${this.apiUrl}/${tourId}`, {
      headers: this.getAuthHeaders(),
    });
  }

  publishTour(tourId: number): Observable<TourResponse> {
    return this.http.post<TourResponse>(
      `${this.apiUrl}/${tourId}/publish`,
      {},
      {
        headers: this.getAuthHeaders(),
      }
    );
  }

  archiveTour(tourId: number): Observable<TourResponse> {
    return this.http.post<TourResponse>(
      `${this.apiUrl}/${tourId}/archive`,
      {},
      {
        headers: this.getAuthHeaders(),
      }
    );
  }

  reactivateTour(tourId: number): Observable<TourResponse> {
    return this.http.post<TourResponse>(
      `${this.apiUrl}/${tourId}/reactivate`,
      {},
      {
        headers: this.getAuthHeaders(),
      }
    );
  }

  addTransportTime(
    tourId: number,
    transportType: string,
    durationMinutes: number
  ): Observable<TourResponse> {
    return this.http.post<TourResponse>(
      `${this.apiUrl}/${tourId}/transport-times?transportType=${transportType}&durationMinutes=${durationMinutes}`,
      {},
      {
        headers: this.getAuthHeaders(),
      }
    );
  }

  addKeyPoint(
    tourId: number,
    request: CreateKeyPointRequest,
    imageFile?: File | null
  ): Observable<void> {
    const formData = new FormData();

    formData.append('name', request.name);
    formData.append('description', request.description);
    formData.append('latitude', request.latitude.toString());
    formData.append('longitude', request.longitude.toString());

    if (imageFile) {
      formData.append('image', imageFile);
    }

    return this.http.post<void>(`${this.apiUrl}/${tourId}/key-points`, formData, {
      headers: this.getAuthHeaders(),
    });
  }

  updateKeyPoint(
    tourId: number,
    keyPointId: number,
    request: CreateKeyPointRequest,
    imageFile?: File | null
  ): Observable<void> {
    const formData = new FormData();

    formData.append('name', request.name);
    formData.append('description', request.description);
    formData.append('latitude', request.latitude.toString());
    formData.append('longitude', request.longitude.toString());

    if (imageFile) {
      formData.append('image', imageFile);
    }

    return this.http.put<void>(`${this.apiUrl}/${tourId}/key-points/${keyPointId}`, formData, {
      headers: this.getAuthHeaders(),
    });
  }

  deleteKeyPoint(tourId: number, keyPointId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${tourId}/key-points/${keyPointId}`, {
      headers: this.getAuthHeaders(),
    });
  }

  getTouristLocation(): Observable<TouristLocationResponse> {
    return this.http.get<TouristLocationResponse>(`${this.apiUrl}/tourist-location`, {
      headers: this.getAuthHeaders(),
    });
  }

  updateTouristLocation(request: TouristLocationRequest): Observable<TouristLocationResponse> {
    return this.http.put<TouristLocationResponse>(`${this.apiUrl}/tourist-location`, request, {
      headers: this.getAuthHeaders(),
    });
  }

  getReviewCount(tourId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/${tourId}/reviews/count`, {
      headers: this.getAuthHeaders(),
    });
  }

  getReviewsForTour(tourId: number): Observable<ReviewResponse[]> {
    return this.http.get<ReviewResponse[]>(`${this.apiUrl}/${tourId}/reviews`, {
      headers: this.getAuthHeaders(),
    });
  }

  createReview(tourId: number, formData: FormData): Observable<ReviewResponse> {
  return this.http.post<ReviewResponse>(`${this.apiUrl}/${tourId}/reviews`, formData, {
    headers: this.getAuthHeaders(),
  });
}

getPurchasedTours(): Observable<TourResponse[]> {
  return this.http.get<TourResponse[]>(
    `${this.apiUrl}/purchases`,
    {
      headers: this.getAuthHeaders(),
    }
  );
}

  startTourExecution(
  tourId: number,
  request: StartTourExecutionRequest
): Observable<TourExecutionResponse> {
  return this.http.post<TourExecutionResponse>(
    `${this.apiUrl}/executions/start/${tourId}`,
    request,
    {
      headers: this.getAuthHeaders(),
    }
  );
}

getActiveTourExecution(): Observable<TourExecutionResponse> {
  return this.http.get<TourExecutionResponse>(
    `${this.apiUrl}/executions/active`,
    {
      headers: this.getAuthHeaders(),
    }
  );
}

checkKeyPoints(
  executionId: number,
  request: CheckKeyPointRequest
): Observable<TourExecutionResponse> {
  return this.http.post<TourExecutionResponse>(
    `${this.apiUrl}/executions/${executionId}/check-key-points`,
    request,
    {
      headers: this.getAuthHeaders(),
    }
  );
}

completeTourExecution(executionId: number): Observable<TourExecutionResponse> {
  return this.http.post<TourExecutionResponse>(
    `${this.apiUrl}/executions/${executionId}/complete`,
    {},
    {
      headers: this.getAuthHeaders(),
    }
  );
}

abandonTourExecution(executionId: number): Observable<TourExecutionResponse> {
  return this.http.post<TourExecutionResponse>(
    `${this.apiUrl}/executions/${executionId}/abandon`,
    {},
    {
      headers: this.getAuthHeaders(),
    }
  );
}
getCompletedExecutions(): Observable<TourExecutionResponse[]> {
  return this.http.get<TourExecutionResponse[]>(
    `${this.apiUrl}/executions/completed`,
    {
      headers: this.getAuthHeaders(),
    }
  );
}



}
