import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProfileResponse {
  id: number;
  username: string;
  email : string;
  role: string;
  blocked: boolean;
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

export interface UpdateProfileInfo {
  username: string;
  email: string;
  currentPassword?: string;
  newPassword?: string;
  firstName: string;
  lastName: string;
  biography: string;
  motto: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8081/api/users';
  private baseUrl = 'http://localhost:8081';

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
getProfileImageUrl(profileImage?: string): string | null {
    if (!profileImage) {
      return null;
    }

    if (profileImage.startsWith('http')) {
      return profileImage;
    }

    return `${this.baseUrl}${profileImage}`;
  }

    updateMyProfile(info: UpdateProfileInfo, imageFile?: File): Observable<ProfileResponse> {
    const formData = new FormData();

    formData.append('info', JSON.stringify({
      username: info.username,
      email: info.email,
      currentPassword: info.currentPassword || '',
      newPassword: info.newPassword || '',
      firstName: info.firstName,
      lastName: info.lastName,
      biography: info.biography,
      motto: info.motto
    }));

    if (imageFile) {
      formData.append('profileImage', imageFile);
    }

    return this.http.patch<ProfileResponse>(`${this.apiUrl}/me/profile`, formData);
  }
}