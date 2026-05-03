import { Component, OnInit } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { UserService, AdminUserOverviewResponse } from '../../../core/services/user';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';


@Component({
  selector: 'app-admin-users',
  imports: [NgFor, NgIf,CommonModule,RouterModule],
  templateUrl: './admin-users.html',
  styleUrl: './admin-users.css'
})
export class AdminUsers implements OnInit {
  users: AdminUserOverviewResponse[] = [];
  errorMessage: string = '';

  constructor(public userService: UserService) {}

  ngOnInit(): void {
    this.userService.getAllUsers().subscribe({
      next: (response) => {
        this.users = response;
      },
      error: (error) => {
        console.error('Greška pri učitavanju korisnika:', error);
        this.errorMessage = 'Nije moguće učitati korisnike.';
      }
    });
  }
  blockUser(userId: number) {
  this.userService.blockUser(userId).subscribe({
    next: () => {
      // odmah update UI (bez refresh)
      this.users = this.users.map(u =>
        u.id === userId ? { ...u, blocked: true } : u
      );
    },
    error: (error) => {
      console.error('Greška pri blokiranju:', error);
      this.errorMessage = 'Blokiranje nije uspelo.';
    }
  });
}

showModal: boolean = false;
selectedUser: any = null;

openModal(user: any): void {
  this.selectedUser = user;
  this.showModal = true;
}

closeModal(): void {
  this.showModal = false;
  this.selectedUser = null;
}

confirmBlock(): void {
  if (this.selectedUser) {
    this.blockUser(this.selectedUser.id);
  }
  this.closeModal();
}
}