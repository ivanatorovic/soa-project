import { Component, OnInit } from '@angular/core';
import { DatePipe, NgFor, NgIf } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { Blog, BlogService } from '../../core/services/blog.service';

@Component({
  selector: 'app-blog',
  standalone: true,
  imports: [NgFor, NgIf, RouterLink, DatePipe],
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
        this.blogs = response;
        this.loading = false;
      },
      error: (error) => {
        console.error('Greska pri ucitavanju blogova:', error);
        this.errorMessage = 'Neuspesno ucitavanje blogova.';
        this.loading = false;
      },
    });
  }

  goToCreateBlog(): void {
    this.router.navigate(['/blog/create']);
  }

  goToBlogDetails(blogId: number): void {
    this.router.navigate(['/blog', blogId]);
  }

  getAuthor(blog: any): string {
    return blog.authorUsername ? blog.authorUsername : `#${blog.authorId}`;
  }
}
