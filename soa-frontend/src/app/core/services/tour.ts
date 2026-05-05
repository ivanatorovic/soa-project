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

export interface TourResponse {
  id: number;
  name: string;
  description: string;
  difficulty: string;
  price: number;
  status: string;
  authorId: number;
  tags: string[];
  keyPoints: KeyPointResponse[];
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

@Injectable({
  providedIn: 'root',
})
export class TourService {
  private apiUrl = 'http://localhost:8000/api/tours';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt'); // koristi svuda isto ime
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }

  createTour(request: CreateTourRequest): Observable<TourResponse> {
    return this.http.post<TourResponse>(this.apiUrl, request, {
      headers: this.getAuthHeaders(),
    });
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

  getTourById(tourId: number): Observable<TourResponse> {
    const token = localStorage.getItem('jwt');

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<TourResponse>(`${this.apiUrl}/${tourId}`, { headers });
  }

  addKeyPoint(
    tourId: number,
    request: CreateKeyPointRequest,
    imageFile?: File | null,
  ): Observable<void> {
    const token = localStorage.getItem('jwt');

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    const formData = new FormData();
    formData.append('name', request.name);
    formData.append('description', request.description);
    formData.append('latitude', request.latitude.toString());
    formData.append('longitude', request.longitude.toString());

    if (imageFile) {
      formData.append('image', imageFile);
    }

    return this.http.post<void>(`${this.apiUrl}/${tourId}/key-points`, formData, { headers });
  }
  updateKeyPoint(
    tourId: number,
    keyPointId: number,
    request: CreateKeyPointRequest,
    imageFile?: File | null,
  ): Observable<void> {
    const headers = this.getAuthHeaders();

    const formData = new FormData();
    formData.append('name', request.name);
    formData.append('description', request.description);
    formData.append('latitude', request.latitude.toString());
    formData.append('longitude', request.longitude.toString());

    if (imageFile) {
      formData.append('image', imageFile);
    }

    return this.http.put<void>(`${this.apiUrl}/${tourId}/key-points/${keyPointId}`, formData, {
      headers,
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

  getReviewCount(tourId: number) {
    return this.http.get<number>(`${this.apiUrl}/${tourId}/reviews/count`);
  }

  getReviewsForTour(tourId: number) {
    return this.http.get<ReviewResponse[]>(`${this.apiUrl}/${tourId}/reviews`);
  }

  createReview(tourId: number, formData: FormData) {
    return this.http.post<ReviewResponse>(`${this.apiUrl}/${tourId}/reviews`, formData, {
      headers: this.getAuthHeaders(),
    });
  }
}
