import { Component, OnInit } from '@angular/core';
import { DatePipe, NgFor, NgIf } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Blog, BlogService } from '../../core/services/blog.service';
import { Comment, CommentService } from '../../core/services/comment.service';

@Component({
  selector: 'app-blog-details',
  standalone: true,
  imports: [NgIf, NgFor, DatePipe, RouterLink],
  templateUrl: './blog-details.html',
  styleUrl: './blog-details.css',
})
export class BlogDetails implements OnInit {
  blog: Blog | null = null;
  comments: Comment[] = [];
  loading = true;
  commentsLoading = true;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private blogService: BlogService,
    private commentService: CommentService,
  ) {}

  ngOnInit(): void {
    const blogId = Number(this.route.snapshot.paramMap.get('id'));

    if (!blogId) {
      this.errorMessage = 'Neispravan ID bloga.';
      this.loading = false;
      this.commentsLoading = false;
      return;
    }

    this.loadBlog(blogId);
    this.loadComments(blogId);
  }

  loadBlog(blogId: number): void {
    this.blogService.getBlogById(blogId).subscribe({
      next: (response) => {
        this.blog = response;
        this.loading = false;
      },
      error: (error) => {
        console.error('Greška pri učitavanju bloga:', error);
        this.errorMessage = 'Neuspešno učitavanje detalja bloga.';
        this.loading = false;
      },
    });
  }

  loadComments(blogId: number): void {
    this.commentService.getCommentsByBlogId(blogId).subscribe({
      next: (response) => {
        this.comments = response;
        this.commentsLoading = false;
      },
      error: (error) => {
        console.error('Greška pri učitavanju komentara:', error);
        this.comments = [];
        this.commentsLoading = false;
      },
    });
  }

  getImageUrl(image: string): string {
    return `http://localhost:8082${image}`;
  }
}
