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

@Injectable({
  providedIn: 'root'
})
export class TourService {
  private apiUrl = 'http://localhost:8083/api/tours';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt'); // koristi svuda isto ime
    return new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
  }

  createTour(request: CreateTourRequest): Observable<TourResponse> {
    return this.http.post<TourResponse>(this.apiUrl, request, {
      headers: this.getAuthHeaders()
    });
  }

  getMyTours(): Observable<TourResponse[]> {
    return this.http.get<TourResponse[]>(`${this.apiUrl}/my`, {
      headers: this.getAuthHeaders()
    });
  }

  getAllTours(): Observable<TourResponse[]> {
    return this.http.get<TourResponse[]>(this.apiUrl, {
      headers: this.getAuthHeaders()
    });
  }

 getTourById(tourId: number): Observable<TourResponse> {
  const token = localStorage.getItem('jwt');

  const headers = new HttpHeaders({
    Authorization: `Bearer ${token}`
  });

  return this.http.get<TourResponse>(`${this.apiUrl}/${tourId}`, { headers });
}

  addKeyPoint(
  tourId: number,
  request: CreateKeyPointRequest,
  imageFile?: File | null
): Observable<void> {
  const token = localStorage.getItem('jwt');

  const headers = new HttpHeaders({
    Authorization: `Bearer ${token}`
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
  imageFile?: File | null
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

  return this.http.put<void>(
    `${this.apiUrl}/${tourId}/key-points/${keyPointId}`,
    formData,
    { headers }
  );
}

deleteKeyPoint(tourId: number, keyPointId: number): Observable<void> {
  return this.http.delete<void>(
    `${this.apiUrl}/${tourId}/key-points/${keyPointId}`,
    { headers: this.getAuthHeaders() }
  );
}
}