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
  imageUrls: string[];

  likedByCurrentUser: boolean;
  likeLoading?: boolean;
}

export interface CreateBlogRequest {
  title: string;
  description: string;
  imageUrls: string[];
}

@Injectable({
  providedIn: 'root',
})
export class BlogService {
  private apiUrl = 'http://localhost:8082/api/blogs';

  constructor(private http: HttpClient) {}

  getAllBlogs(): Observable<Blog[]> {
    return this.http.get<Blog[]>(this.apiUrl);
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
}
