import { Routes } from '@angular/router';
import { Login } from './features/stakeholders/login/login';
import { Register } from './features/stakeholders/register/register';
import { Profile } from './features/stakeholders/profile/profile';
import { AdminUsers } from './features/stakeholders/admin-users/admin-users';
import { Home } from './features/home/home';
import { BlogComponent } from './features/blog/blog';
import { TouristLocationSimulator } from './features/tours/tourist-location-simulator/tourist-location-simulator';

import { authGuard } from './core/guards/auth-guard';
import { BlogDetails } from './features/blog-details/blog-details';
import { CreateBlogComponent } from './features/create-blog/create-blog';
import { EditProfileComponent } from './features/stakeholders/edit-profile/edit-profile';
import { CreateTour } from './features/ture/create-tour/create-tour';
import { Tours } from './features/ture/tours/tours';
import { AddKeyPoint } from './features/tours/add-key-point/add-key-point';
import { TourDetails } from './features/tours/tour-details/tour-details';
import { UserList } from './features/stakeholders/user-list/user-list';
import { Reviews } from './features/tours/reviews/reviews';
import { CreateReview } from './features/tours/create-review/create-review';
import { ShoppingCart } from './features/tours/shopping-cart/shopping-cart';
import { PurchasedTours } from './features/tours/purchased-tours/purchased-tours';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'profile', component: Profile, canActivate: [authGuard] },
  { path: 'profile/:id', component: Profile, canActivate: [authGuard] },
  { path: 'admin/users', component: AdminUsers, canActivate: [authGuard] },
  { path: 'blog', component: BlogComponent },
  { path: 'blog/create', component: CreateBlogComponent, canActivate: [authGuard] },
  { path: 'blog/:id', component: BlogDetails },
  {
    path: 'tours/tourist-location',
    component: TouristLocationSimulator,
    canActivate: [authGuard],
  },
  {
  path: 'shopping-cart',
  component: ShoppingCart,
  canActivate: [authGuard],
},
{
  path: 'tours/start/:tourId',
  loadComponent: () =>
    import('./features/tours/active-tour/active-tour')
      .then(m => m.ActiveTour),
  canActivate: [authGuard],
},

{
  path: 'tours/active/:executionId',
  loadComponent: () =>
    import('./features/tours/active-tour/active-tour')
      .then(m => m.ActiveTour),
  canActivate: [authGuard],
},
{
  path: 'tours/purchased',
  component: PurchasedTours,
  canActivate: [authGuard]
},
  { path: 'tours/:id', component: TourDetails },
  { path: 'tours/:id/reviews', component: Reviews },
  { path: 'tours/:id/create-review', component: CreateReview },
  { path: 'tours/:id/key-points', component: AddKeyPoint },
  {
    path: 'create-tour',
    component: CreateTour,
  },
  {
    path: 'tours',
    component: Tours,
  },

  { path: 'edit-profile', component: EditProfileComponent },
  { path: 'users', component: UserList, canActivate: [authGuard] },
  { path: 'users/:type/:id', component: UserList, canActivate: [authGuard] },
];
