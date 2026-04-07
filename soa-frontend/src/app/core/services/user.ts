import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProfileResponse {
  id: number;
  username: string;
  email : string;
  role: string;
  firstName: string;
  lastName: string;
  profileImage: string;
  biography: string;
  motto: string;
}

export interface AdminUserOverviewResponse {
  id: number;
  username: string;
  email : string;
  role: string;
  blocked: boolean;
  firstName?: string;
  lastName?: string;
  profileImage?: string;
  biography?: string;
  motto?: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8081/api/users';

  constructor(private http: HttpClient) {}

  getMyProfile(): Observable<ProfileResponse> {
    return this.http.get<ProfileResponse>(`${this.apiUrl}/me/profile`);
  }

  getAllUsers(): Observable<AdminUserOverviewResponse[]> {
    return this.http.get<AdminUserOverviewResponse[]>(this.apiUrl);
  }
  blockUser(userId: number): Observable<any> {
  return this.http.put(`${this.apiUrl}/${userId}/block`, {});
}
}