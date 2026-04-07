import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Comment {
  id: number;
  blogId: number;
  authorUsername: string;
  text: string;
}

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  private apiUrl = 'http://localhost:8082/api/comments';

  constructor(private http: HttpClient) {}

  getCommentsByBlogId(blogId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.apiUrl}/blog/${blogId}`);
  }
}
