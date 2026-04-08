import { Component, OnInit, OnDestroy } from '@angular/core';
import { DatePipe, NgFor, NgIf, ViewportScroller } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Blog, BlogService } from '../../core/services/blog.service';
import { Comment, CommentService } from '../../core/services/comment.service';
import { FormsModule } from '@angular/forms';
import { TimeAgoPipe } from '../../core/time-ago.pipe';
import { marked } from 'marked';

@Component({
  selector: 'app-blog-details',
  standalone: true,
  imports: [NgIf, NgFor, DatePipe, RouterLink, FormsModule, TimeAgoPipe],
  templateUrl: './blog-details.html',
  styleUrl: './blog-details.css',
})
export class BlogDetails implements OnInit, OnDestroy {
  blog: Blog | null = null;
  comments: Comment[] = [];
  loading = true;
  commentsLoading = true;
  errorMessage = '';
  newCommentText: string = '';
  commentSubmitting = false;

  currentImageIndex = 0;
  private sliderInterval: ReturnType<typeof setInterval> | null = null;

  constructor(
    private route: ActivatedRoute,
    private blogService: BlogService,
    private commentService: CommentService,
    private viewportScroller: ViewportScroller,
  ) {}

  ngOnInit(): void {
    void this.viewportScroller.scrollToPosition([0, 0]);
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

  ngOnDestroy(): void {
    this.stopAutoSlide();
  }

  loadBlog(blogId: number): void {
    this.blogService.getBlogById(blogId).subscribe({
      next: (response) => {
        this.blog = {
          ...response,
          likeLoading: false,
        };
        this.currentImageIndex = 0;
        this.startAutoSlide();
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

  toggleLike(): void {
    if (!this.blog || this.blog.likeLoading) return;

    this.blog.likeLoading = true;

    if (this.blog.likedByCurrentUser) {
      this.blogService.unlikeBlog(this.blog.id).subscribe({
        next: () => {
          if (!this.blog) return;
          this.blog.likedByCurrentUser = false;
          this.blog.likesCount = Math.max(0, this.blog.likesCount - 1);
          this.blog.likeLoading = false;
        },
        error: (error) => {
          console.error('Greška pri uklanjanju lajka:', error);
          if (this.blog) {
            this.blog.likeLoading = false;
          }
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
          console.error('Greška pri lajkovanju:', error);
          if (this.blog) {
            this.blog.likeLoading = false;
          }
        },
      });
    }
  }

  getImageUrl(image: string): string {
    return `http://localhost:8082${image}`;
  }

  submitComment(): void {
    if (!this.blog || !this.newCommentText.trim()) return;

    this.commentSubmitting = true;

    this.commentService
      .createComment({
        blogId: this.blog.id,
        text: this.newCommentText,
      })
      .subscribe({
        next: (response) => {
          this.comments = [response, ...this.comments];
          this.newCommentText = '';
          this.commentSubmitting = false;
        },
        error: (error) => {
          console.error('Greška pri slanju komentara:', error);
          this.commentSubmitting = false;
        },
      });
  }

  startAutoSlide(): void {
    this.stopAutoSlide();

    this.sliderInterval = setInterval(() => {
      if (this.blog?.imageUrls?.length && this.blog.imageUrls.length > 1) {
        this.nextImage();
      }
    }, 6500);
  }

  stopAutoSlide(): void {
    if (this.sliderInterval) {
      clearInterval(this.sliderInterval);
      this.sliderInterval = null;
    }
  }

  nextImage(): void {
    if (!this.blog?.imageUrls?.length) return;

    this.currentImageIndex = (this.currentImageIndex + 1) % this.blog.imageUrls.length;

    this.restartAutoSlide();
  }

  prevImage(): void {
    if (!this.blog?.imageUrls?.length) return;

    this.currentImageIndex =
      (this.currentImageIndex - 1 + this.blog.imageUrls.length) % this.blog.imageUrls.length;

    this.restartAutoSlide();
  }

  goToImage(index: number): void {
    this.currentImageIndex = index;
    this.restartAutoSlide();
  }

  private restartAutoSlide(): void {
    this.startAutoSlide();
  }

  getFormattedDescription(): string {
    return marked.parse(this.blog?.description || '') as string;
  }
}
