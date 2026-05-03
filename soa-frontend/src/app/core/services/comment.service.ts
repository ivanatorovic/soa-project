import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Comment {
  id: string;
  blogId: string;
  authorUsername: string;
  text: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  private apiUrl = 'http://localhost:8082/api/comments';

  constructor(private http: HttpClient) {}

  getCommentsByBlogId(blogId: string): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.apiUrl}/blog/${blogId}`);
  }

  createComment(request: { blogId: string; text: string }) {
    return this.http.post<Comment>('http://localhost:8082/api/comments', request);
  }
}
