import { Component, OnInit } from '@angular/core';
import { DatePipe, NgFor, NgIf } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Blog, BlogService } from '../../core/services/blog.service';
import { TimeAgoPipe } from '../../core/time-ago.pipe';
import { UserService } from '../../core/services/user';

@Component({
  selector: 'app-blog',
  standalone: true,
  imports: [NgFor, NgIf, RouterLink, DatePipe, TimeAgoPipe],
  templateUrl: './blog.html',
  styleUrl: './blog.css',
})
export class BlogComponent implements OnInit {
  blogs: Blog[] = [];
  followedBlogIds = new Set<string>();

  loading = true;
  errorMessage = '';
  accessErrorMessage = '';

  currentUserId: number | null = null;

  constructor(
    private blogService: BlogService,
    private userService: UserService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.userService.getMyProfile().subscribe({
      next: (profile) => {
        this.currentUserId = profile.id;
        this.loadBlogs();
      },
      error: () => {
        this.loadBlogs();
      },
    });
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

  goToBlogDetails(blog: Blog): void {
    this.accessErrorMessage = '';

    const isMyBlog = blog.authorId === this.currentUserId;

    if (!isMyBlog && !this.followedBlogIds.has(blog.id)) {
      this.accessErrorMessage = 'Morate zapratiti korisnika da biste mogli da čitate blog.';
      return;
    }

    this.router.navigate(['/blog', blog.id]);
  }

  goToAuthorProfile(authorId: number): void {
    if (authorId === this.currentUserId) {
      this.router.navigate(['/profile']);
      return;
    }

    this.router.navigate(['/profile', authorId]);
  }

  getAuthor(blog: Blog): string {
    return blog.authorUsername ? blog.authorUsername : 'Nepoznati autor';
  }
}
