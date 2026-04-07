import { Routes } from '@angular/router';
import { Login } from './features/stakeholders/login/login';
import { Register } from './features/stakeholders/register/register';
import { Profile } from './features/stakeholders/profile/profile';
import { AdminUsers } from './features/stakeholders/admin-users/admin-users';
import { Home } from './features/home/home';
import { BlogComponent } from './features/blog/blog';

import { authGuard } from './core/guards/auth-guard';
import { CreateBlog } from './features/create-blog/create-blog';
import { BlogDetails } from './features/blog-details/blog-details';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'profile', component: Profile, canActivate: [authGuard] },
  { path: 'admin/users', component: AdminUsers, canActivate: [authGuard] },
  { path: 'blog', component: BlogComponent },
  { path: 'blog/create', component: CreateBlog, canActivate: [authGuard] },
  { path: 'blog/:id', component: BlogDetails },
];
