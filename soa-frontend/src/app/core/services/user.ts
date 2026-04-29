import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProfileResponse {
  id: number;
  username: string;
  email: string;
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
  email: string;
  role: string;
  blocked: boolean;
  firstName?: string;
  lastName?: string;
  profileImage?: string;
  biography?: string;
  motto?: string;
}

export interface FollowUserResponse {
  userId: number;
  username: string;
}

export interface UserListItem {
  id: number;
  username: string;
  profileImage?: string;
  role?: string;
  isFollowedByMe: boolean;
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
  providedIn: 'root',
})
export class UserService {
  private apiUrl = 'http://localhost:8000/api/users';
  private baseUrl = 'http://localhost:8000';
  private followerUrl = 'http://localhost:8000/api/follows';

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

  getUserProfile(userId: number): Observable<ProfileResponse> {
    return this.http.get<ProfileResponse>(`${this.apiUrl}/${userId}`);
  }

  updateMyProfile(info: UpdateProfileInfo, imageFile?: File): Observable<ProfileResponse> {
    const formData = new FormData();

    formData.append(
      'info',
      JSON.stringify({
        username: info.username,
        email: info.email,
        currentPassword: info.currentPassword || '',
        newPassword: info.newPassword || '',
        firstName: info.firstName,
        lastName: info.lastName,
        biography: info.biography,
        motto: info.motto,
      }),
    );

    if (imageFile) {
      formData.append('profileImage', imageFile);
    }

    return this.http.patch<ProfileResponse>(`${this.apiUrl}/me/profile`, formData);
  }

  getFollowersCount(userId: number) {
    return this.http.get<{ count: number }>(`${this.followerUrl}/${userId}/followers/count`);
  }

  getFollowingCount(userId: number) {
    return this.http.get<{ count: number }>(`${this.followerUrl}/${userId}/following/count`);
  }

  getFollowers(userId: number): Observable<FollowUserResponse[]> {
    return this.http.get<FollowUserResponse[]>(`${this.followerUrl}/${userId}/followers`);
  }

  getFollowing(userId: number): Observable<FollowUserResponse[]> {
    return this.http.get<FollowUserResponse[]>(`${this.followerUrl}/${userId}/following`);
  }

  followUser(userId: number): Observable<any> {
    return this.http.post(`${this.followerUrl}/${userId}`, {});
  }

  unfollowUser(userId: number): Observable<any> {
    return this.http.delete(`${this.followerUrl}/${userId}`);
  }

  getRecommendations(userId: number): Observable<FollowUserResponse[]> {
    return this.http.get<FollowUserResponse[]>(`${this.followerUrl}/recommendations/${userId}`);
  }
}
