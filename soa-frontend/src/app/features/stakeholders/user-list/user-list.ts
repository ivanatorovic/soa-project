import { Component, OnInit } from '@angular/core';
import { NgFor, NgIf, UpperCasePipe, NgClass } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { forkJoin, from } from 'rxjs';
import {
  UserService,
  FollowUserResponse,
  AdminUserOverviewResponse,
  UserListItem,
} from '../../../core/services/user';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [NgFor, NgIf, UpperCasePipe, NgClass, RouterModule],
  templateUrl: './user-list.html',
  styleUrl: './user-list.css',
})
export class UserList implements OnInit {
  users: UserListItem[] = [];
  recommendedUsers: UserListItem[] = [];
  errorMessage = '';
  loading = true;
  type: 'followers' | 'following' | 'all' = 'all';
  pageTitle = 'Pratioci';
  pageDescription = 'Pregled korisnika koji prate izabranog korisnika.';

  currentUserId?: number;

  constructor(
    public userService: UserService,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    const type = this.route.snapshot.paramMap.get('type');
    const idParam = this.route.snapshot.paramMap.get('id');
    const userId = idParam ? Number(idParam) : null;

    this.type = type ? (type as 'followers' | 'following' | 'all') : 'all';
    let usersRequest;

    if (type === 'followers' && userId) {
      usersRequest = this.userService.getFollowers(userId);
      this.pageTitle = 'Pratioci';
      this.pageDescription = 'Korisnici koji prate izabranog korisnika.';
    } else if (type === 'following' && userId) {
      usersRequest = this.userService.getFollowing(userId);
      this.pageTitle = 'Korisnici koje pratiš';
      this.pageDescription = 'Lista korisnika koje trenutno pratiš.';
    } else {
      usersRequest = null;
      this.pageTitle = 'Svi korisnici';
      this.pageDescription = 'Pregled svih korisnika aplikacije.';
    }

    forkJoin({
      myProfile: this.userService.getMyProfile(),
      allUsers: this.userService.getAllUsers(),
    }).subscribe({
      next: ({ myProfile, allUsers }) => {
        this.currentUserId = myProfile.id;

        forkJoin({
          following: this.userService.getFollowing(myProfile.id),
          recommendations: this.userService.getRecommendations(myProfile.id),
          selectedUsers: usersRequest ? usersRequest : this.userService.getFollowers(myProfile.id),
        }).subscribe({
          next: ({ following, recommendations, selectedUsers }) => {
            if (type === 'followers' || type === 'following') {
              this.users = this.mapUsers(selectedUsers, allUsers, following);
            } else {
              this.users = this.mapAllUsersWithRecommendationsFirst(
                allUsers,
                recommendations,
                following,
                myProfile.id,
              );
            }

            const existingIds = new Set(this.users.map((u) => u.id));

            const filteredRecommendations = recommendations.filter(
              (r) => !existingIds.has(r.userId),
            );

            this.recommendedUsers = this.mapUsers(filteredRecommendations, allUsers, following);

            this.loading = false;
          },
          error: (error) => {
            console.error('Greška pri učitavanju korisnika:', error);
            this.errorMessage = 'Greška pri učitavanju korisnika.';
            this.loading = false;
          },
        });
      },
      error: (error) => {
        console.error('Greška pri učitavanju profila:', error);
        this.errorMessage = 'Greška pri učitavanju profila.';
        this.loading = false;
      },
    });
  }

  private mapUsers(
    users: FollowUserResponse[],
    allUsers: AdminUserOverviewResponse[],
    following: FollowUserResponse[],
  ): UserListItem[] {
    return users.map((user) => {
      const fullUser = allUsers.find((u) => u.id === user.userId);
      const isFollowedByMe = following.some((u) => u.userId === user.userId);

      return {
        id: user.userId,
        username: user.username,
        profileImage: fullUser?.profileImage,
        role: fullUser?.role,
        isFollowedByMe,
      };
    });
  }

  follow(user: UserListItem): void {
    this.userService.followUser(user.id).subscribe({
      next: () => {
        user.isFollowedByMe = true;
      },
      error: (error) => {
        console.error('Greška pri praćenju:', error);
        this.errorMessage = 'Praćenje korisnika nije uspelo.';
      },
    });
  }

  unfollow(user: UserListItem): void {
    this.userService.unfollowUser(user.id).subscribe({
      next: () => {
        user.isFollowedByMe = false;
      },
      error: (error) => {
        console.error('Greška pri otpraćivanju:', error);
        this.errorMessage = 'Otpraćivanje korisnika nije uspelo.';
      },
    });
  }

  private mapAllUsersWithRecommendationsFirst(
    allUsers: AdminUserOverviewResponse[],
    recommendations: FollowUserResponse[],
    following: FollowUserResponse[],
    currentUserId: number,
  ): UserListItem[] {
    const recommendationIds = new Set(recommendations.map((r) => r.userId));
    const followingIds = new Set(following.map((f) => f.userId));

    const mappedUsers = allUsers
      .filter((user) => user.id !== currentUserId && user.role?.toLowerCase() !== 'admin')
      .map((user) => ({
        id: user.id,
        username: user.username,
        profileImage: user.profileImage,
        role: user.role,
        isFollowedByMe: followingIds.has(user.id),
        isRecommended: recommendationIds.has(user.id),
      }));

    return mappedUsers.sort((a, b) => {
      if (a.isRecommended && !b.isRecommended) return -1;
      if (!a.isRecommended && b.isRecommended) return 1;
      return 0;
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
