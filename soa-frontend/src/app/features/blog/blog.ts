import { Component, OnInit } from '@angular/core';
import { DatePipe, NgFor, NgIf } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
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
  followedBlogIds = new Set<number>();

  loading = true;
  errorMessage = '';
  accessErrorMessage = '';

  constructor(
    private blogService: BlogService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadBlogs();
  }

  loadBlogs(): void {
    forkJoin({
      allBlogs: this.blogService.getAllBlogs(),
      followedBlogs: this.blogService.getFollowedUsersBlogs(),
    }).subscribe({
      next: ({ allBlogs, followedBlogs }) => {
        this.blogs = allBlogs.map((blog) => ({
          ...blog,
          likeLoading: false,
          likeErrorMessage: '',
        }));

        this.followedBlogIds.clear();

        if (Array.isArray(followedBlogs)) {
          followedBlogs.forEach((blog) => this.followedBlogIds.add(blog.id));
        }

        this.errorMessage = '';
        this.accessErrorMessage = '';
        this.loading = false;
      },
      error: (error) => {
        console.error('Greška pri učitavanju blogova:', error);
        this.errorMessage = error?.error?.message || 'Neuspešno učitavanje blogova.';
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
    this.accessErrorMessage = '';

    if (!this.followedBlogIds.has(blogId)) {
      this.accessErrorMessage = 'Morate zapratiti korisnika da biste mogli da čitate blog.';
      return;
    }

    this.router.navigate(['/blog', blogId]);
  }

  getAuthor(blog: Blog): string {
    return blog.authorUsername ? blog.authorUsername : 'Nepoznati autor';
  }
}
