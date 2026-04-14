import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CreateTourRequest {
  name: string;
  description: string;
  difficulty: string;
  tags: string[];
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

  createTour(request: CreateTourRequest): Observable<TourResponse> {
    const token = localStorage.getItem('jwt');

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    return this.http.post<TourResponse>(this.apiUrl, request, { headers });
  }

  getMyTours(): Observable<TourResponse[]> {
    const token = localStorage.getItem('jwt');

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    return this.http.get<TourResponse[]>(`${this.apiUrl}/my`, { headers });
  }
  getAllTours(): Observable<TourResponse[]> {
  const token = localStorage.getItem('token');

  const headers = new HttpHeaders({
    Authorization: `Bearer ${token}`
  });

  return this.http.get<TourResponse[]>(this.apiUrl, { headers });
}
}