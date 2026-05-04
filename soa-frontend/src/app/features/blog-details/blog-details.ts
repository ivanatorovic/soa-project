import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule, DatePipe, NgFor, NgIf } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ViewportScroller } from '@angular/common';
import { Subscription } from 'rxjs';

import { Blog, BlogService } from '../../core/services/blog.service';
import { Comment, CommentService } from '../../core/services/comment.service';
import { UserService } from '../../core/services/user';
import { TimeAgoPipe } from '../../core/time-ago.pipe';

@Component({
  selector: 'app-blog-details',
  standalone: true,
  imports: [CommonModule, NgIf, NgFor, FormsModule, RouterLink, DatePipe, TimeAgoPipe],
  templateUrl: './blog-details.html',
  styleUrls: ['./blog-details.css'],
})
export class BlogDetails implements OnInit, OnDestroy {
  blog: Blog | null = null;
  comments: Comment[] = [];

  loading = true;
  commentsLoading = true;

  errorMessage = '';
  commentErrorMessage = '';

  newCommentText = '';
  commentSubmitting = false;

  currentImageIndex = 0;
  currentUserId: number | null = null;

  private subscription = new Subscription();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private blogService: BlogService,
    private commentService: CommentService,
    private userService: UserService,
    private viewportScroller: ViewportScroller,
  ) {}

  ngOnInit(): void {
    void this.viewportScroller.scrollToPosition([0, 0]);
    const blogId = this.route.snapshot.paramMap.get('id');
    this.viewportScroller.scrollToPosition([0, 0]);

    this.userService.getMyProfile().subscribe({
      next: (profile) => {
        this.currentUserId = profile.id;
      },
    });



    if (!blogId) {
      this.errorMessage = 'Neispravan ID bloga.';
      this.loading = false;
      this.commentsLoading = false;
      return;
    }

    this.loadBlog(blogId);
    this.loadComments(blogId);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  loadBlog(blogId: string): void {
    this.blogService.getBlogById(blogId).subscribe({
      next: (blog) => {
        this.blog = {
          ...blog,
          likeLoading: false,
          likeErrorMessage: '',
        };

        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = error?.error?.message || 'Greška pri učitavanju bloga.';
        this.loading = false;
      },
    });
  }

  loadComments(blogId: string): void {
    this.commentService.getCommentsByBlogId(blogId).subscribe({
      next: (comments) => {
        this.comments = comments;
        this.commentsLoading = false;
      },
      error: () => {
        this.comments = [];
        this.commentsLoading = false;
      },
    });
  }

  submitComment(): void {
    if (!this.blog) return;

    this.commentErrorMessage = '';

    if (!this.newCommentText.trim()) {
      this.commentErrorMessage = 'Komentar ne sme biti prazan.';
      return;
    }

    this.commentSubmitting = true;

    this.commentService
      .createComment({
        blogId: this.blog.id,
        text: this.newCommentText,
      })
      .subscribe({
        next: (comment) => {
          this.comments = [comment, ...this.comments];
          this.newCommentText = '';
          this.commentSubmitting = false;
        },
        error: (error) => {
          this.commentErrorMessage = error?.error?.message || 'Greška pri slanju komentara.';
          this.commentSubmitting = false;
        },
      });
  }

  toggleLike(): void {
    if (!this.blog || this.blog.likeLoading) return;

    this.blog.likeLoading = true;
    this.blog.likeErrorMessage = '';

    if (this.blog.likedByCurrentUser) {
      this.blogService.unlikeBlog(this.blog.id).subscribe({
        next: () => {
          if (!this.blog) return;

          this.blog.likedByCurrentUser = false;
          this.blog.likesCount = Math.max(0, this.blog.likesCount - 1);
          this.blog.likeLoading = false;
        },
        error: (error) => {
          if (!this.blog) return;

          this.blog.likeErrorMessage = error?.error?.message || 'Greška pri uklanjanju lajka.';
          this.blog.likeLoading = false;
        },
      });
    } else {
      this.blogService.likeBlog(this.blog.id).subscribe({
        next: () => {
          if (!this.blog) return;

          this.blog.likedByCurrentUser = true;
          this.blog.likesCount += 1;
          this.blog.likeLoading = false;
        },
        error: (error) => {
          if (!this.blog) return;

          this.blog.likeErrorMessage = error?.error?.message || 'Greška pri lajkovanju.';
          this.blog.likeLoading = false;
        },
      });
    }
  }

  goToAuthorProfile(authorId: number): void {
    if (authorId === this.currentUserId) {
      this.router.navigate(['/profile']);
      return;
    }

    this.router.navigate(['/profile', authorId]);
  }

  goToCommentAuthor(authorId: number): void {
    if (authorId === this.currentUserId) {
      this.router.navigate(['/profile']);
      return;
    }

    this.router.navigate(['/profile', authorId]);
  }

  getImageUrl(imageUrl: string): string {
    return this.blogService.resolveImageUrl(imageUrl);
  }

  nextImage(): void {
    if (!this.blog?.imageUrls?.length) return;

    this.currentImageIndex = (this.currentImageIndex + 1) % this.blog.imageUrls.length;
  }

  prevImage(): void {
    if (!this.blog?.imageUrls?.length) return;

    this.currentImageIndex =
      (this.currentImageIndex - 1 + this.blog.imageUrls.length) % this.blog.imageUrls.length;
  }

  goToImage(index: number): void {
    this.currentImageIndex = index;
  }




  getFormattedDescription(): string {
    if (!this.blog?.description) return '';

    return this.blog.description.replace(/\n/g, '<br>');
  }
}
