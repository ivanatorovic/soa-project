import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Blog {
  id: number;
  title: string;
  description: string;
  createdAt: string;
  likesCount: number;
  authorUsername: string;
  authorId: number;
  imageUrls: string[];

  likedByCurrentUser: boolean;
  likeLoading?: boolean;
  likeErrorMessage?: string;
}

export interface CreateBlogRequest {
  title: string;
  description: string;
  imageUrls: string[];
}

export interface FeedMessageResponse {
  message: string;
}

@Injectable({
  providedIn: 'root',
})
export class BlogService {
  private apiUrl = 'http://localhost:8000/api/blogs';
  private baseUrl = 'http://localhost:8000';

  constructor(private http: HttpClient) {}

  getAllBlogs(): Observable<Blog[]> {
    return this.http.get<Blog[]>(this.apiUrl);
  }

  getFollowedUsersBlogs(): Observable<Blog[] | FeedMessageResponse> {
    return this.http.get<Blog[] | FeedMessageResponse>(`${this.apiUrl}/feed`);
  }

  getBlogById(id: number): Observable<Blog> {
    return this.http.get<Blog>(`${this.apiUrl}/${id}`);
  }

  likeBlog(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${id}/like`, {});
  }

  unlikeBlog(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}/like`);
  }

  createBlog(formData: FormData): Observable<any> {
    return this.http.post(this.apiUrl, formData);
  }

  resolveImageUrl(imageUrl: string): string {
    if (!imageUrl) {
      return '';
    }

    if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
      return imageUrl;
    }

    return `${this.baseUrl}${imageUrl}`;
  }
}
