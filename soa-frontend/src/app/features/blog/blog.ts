import { Component, OnInit } from '@angular/core';
import { DatePipe, NgFor, NgIf } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { Blog, BlogService } from '../../core/services/blog.service';
import { TimeAgoPipe } from '../../core/time-ago.pipe';

@Component({
  selector: 'app-blog',
  standalone: true,
  imports: [NgFor, NgIf, RouterLink, DatePipe, TimeAgoPipe],
  templateUrl: './blog.html',
  styleUrl: './blog.css',
})
export class BlogComponent implements OnInit {
  blogs: Blog[] = [];
  loading = true;
  errorMessage = '';

  constructor(
    private blogService: BlogService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadBlogs();
  }

  loadBlogs(): void {
    this.blogService.getAllBlogs().subscribe({
      next: (response) => {
        this.blogs = response.map((blog) => ({
          ...blog,
          likeLoading: false,
          likeErrorMessage: '',
        }));
        this.loading = false;
      },
      error: (error) => {
        console.error('Greška pri učitavanju blogova:', error);
        this.errorMessage = 'Neuspešno učitavanje blogova.';
        this.loading = false;
      },
    });
  }

  toggleLike(blog: Blog): void {
    if (blog.likeLoading) return;

    blog.likeLoading = true;
    blog.likeErrorMessage = '';

    if (blog.likedByCurrentUser) {
      this.blogService.unlikeBlog(blog.id).subscribe({
        next: () => {
          blog.likedByCurrentUser = false;
          blog.likesCount = Math.max(0, blog.likesCount - 1);
          blog.likeLoading = false;
        },
        error: (error) => {
          console.error('Greška pri uklanjanju lajka:', error);
          blog.likeErrorMessage =
            error?.error?.message || 'Došlo je do greške pri uklanjanju lajka.';
          blog.likeLoading = false;
        },
      });
    } else {
      this.blogService.likeBlog(blog.id).subscribe({
        next: () => {
          blog.likedByCurrentUser = true;
          blog.likesCount += 1;
          blog.likeLoading = false;
        },
        error: (error) => {
          console.error('Greška pri lajkovanju:', error);
          blog.likeErrorMessage = error?.error?.message || 'Došlo je do greške pri lajkovanju.';
          blog.likeLoading = false;
        },
      });
    }
  }

  goToCreateBlog(): void {
    this.router.navigate(['/blog/create']);
  }

  goToBlogDetails(blogId: number): void {
    this.router.navigate(['/blog', blogId]);
  }

  getAuthor(blog: Blog): string {
    return blog.authorUsername ? blog.authorUsername : 'Nepoznati autor';
  }
}
