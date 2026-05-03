import { Component, OnInit } from '@angular/core';
import { CommonModule, UpperCasePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { ProfileResponse, UpdateProfileInfo, UserService } from '../../../core/services/user';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, UpperCasePipe],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  profile: ProfileResponse | null = null;

  profileImageUrl: string | null = null;
  imagePreview: string | null = null;
  selectedFile: File | undefined;

  followingCount = 0;
  followersCount = 0;

  isFollowedByMe = false;
  currentUserId: number | null = null;
  isMyProfile = true;
  isEditing = false;
  errorMessage = '';
  usernameError = '';
  passwordError = '';

  formData: UpdateProfileInfo = {
    username: '',
    email: '',
    currentPassword: '',
    newPassword: '',
    firstName: '',
    lastName: '',
    biography: '',
    motto: '',
  };

  constructor(
    private userService: UserService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const userId = params.get('id');

      if (userId) {
        this.isMyProfile = false;
        this.loadUserProfile(+userId);
      } else {
        this.isMyProfile = true;
        this.loadProfile();
      }
    });
  }

  loadUserProfile(userId: number): void {
    this.userService.getUserProfile(userId).subscribe({
      next: (profile) => {
        this.profile = profile as ProfileResponse;
        this.profileImageUrl = this.userService.getProfileImageUrl(profile.profileImage);
        this.loadFollowCounts(profile.id);
        this.checkIfFollowed(profile.id);
      },
      error: () => {
        this.errorMessage = 'Greska prilikom ucitavanja profila korisnika.';
      },
    });
  }

  loadProfile(): void {
    this.userService.getMyProfile().subscribe({
      next: (profile) => {
        this.profile = profile;
        this.profileImageUrl = this.userService.getProfileImageUrl(profile.profileImage);
        this.currentUserId = profile.id;

        this.formData = {
          username: profile.username,
          email: profile.email,
          currentPassword: '',
          newPassword: '',
          firstName: profile.firstName || '',
          lastName: profile.lastName || '',
          biography: profile.biography || '',
          motto: profile.motto || '',
        };

        this.loadFollowCounts(profile.id);
      },
      error: () => {
        this.errorMessage = 'Greska prilikom ucitavanja profila.';
      },
    });
  }

  loadFollowCounts(userId: number): void {
    this.userService.getFollowingCount(userId).subscribe({
      next: (response) => {
        this.followingCount = response.count;
      },
      error: () => {
        this.followingCount = 0;
      },
    });

    this.userService.getFollowersCount(userId).subscribe({
      next: (response) => {
        this.followersCount = response.count;
      },
      error: () => {
        this.followersCount = 0;
      },
    });
  }

  checkIfFollowed(userId: number): void {
    if (!this.currentUserId) {
      this.userService.getMyProfile().subscribe({
        next: (myProfile) => {
          this.currentUserId = myProfile.id;
          this.loadFollowingAndCheck(userId);
        },
      });

      return;
    }

    this.loadFollowingAndCheck(userId);
  }

  loadFollowingAndCheck(userId: number): void {
    if (!this.currentUserId) return;

    this.userService.getFollowing(this.currentUserId).subscribe({
      next: (following) => {
        this.isFollowedByMe = following.some((user) => user.userId === userId);
      },
      error: () => {
        this.isFollowedByMe = false;
      },
    });
  }

  followProfile(): void {
    if (!this.profile) return;

    this.userService.followUser(this.profile.id).subscribe({
      next: () => {
        this.isFollowedByMe = true;
        this.loadFollowCounts(this.profile!.id);
      },
    });
  }

  unfollowProfile(): void {
    if (!this.profile) return;

    this.userService.unfollowUser(this.profile.id).subscribe({
      next: () => {
        this.isFollowedByMe = false;
        this.loadFollowCounts(this.profile!.id);
      },
    });
  }

  goToFollowing(): void {
    if (!this.profile?.id) return;

    this.router.navigate(['/users', 'following', this.profile.id]);
  }

  goToFollowers(): void {
    if (!this.profile?.id) return;

    this.router.navigate(['/users', 'followers', this.profile.id]);
  }

  goToEditProfile(): void {
    this.router.navigate(['/edit-profile']);
  }

  canEditProfile(): boolean {
    return !!this.profile && this.isMyProfile;
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) {
      return;
    }

    this.selectedFile = input.files[0];

    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result as string;
    };

    reader.readAsDataURL(this.selectedFile);
  }

  saveChanges(): void {
    this.errorMessage = '';
    this.usernameError = '';
    this.passwordError = '';

    if (!this.formData.username.trim()) {
      this.usernameError = 'Korisnicko ime je obavezno.';
      return;
    }

    if (this.formData.newPassword && !this.formData.currentPassword) {
      this.passwordError = 'Morate uneti staru lozinku.';
      return;
    }

    this.userService.updateMyProfile(this.formData, this.selectedFile).subscribe({
      next: (updatedProfile) => {
        this.profile = updatedProfile;
        this.profileImageUrl = this.userService.getProfileImageUrl(updatedProfile.profileImage);

        this.isEditing = false;
        this.selectedFile = undefined;
        this.imagePreview = null;

        this.loadFollowCounts(updatedProfile.id);
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Greska prilikom cuvanja izmena profila.';
      },
    });
  }

  cancelEdit(): void {
    if (!this.profile) return;

    this.isEditing = false;
    this.selectedFile = undefined;
    this.imagePreview = null;
    this.errorMessage = '';
    this.usernameError = '';
    this.passwordError = '';

    this.formData = {
      username: this.profile.username,
      email: this.profile.email,
      currentPassword: '',
      newPassword: '',
      firstName: this.profile.firstName || '',
      lastName: this.profile.lastName || '',
      biography: this.profile.biography || '',
      motto: this.profile.motto || '',
    };
  }
}
