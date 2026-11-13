import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm sticky-top">
      <div class="container">
        <a class="navbar-brand fw-bold d-flex align-items-center gap-2" routerLink="/dashboard">
          <span class="badge bg-primary p-2 rounded-3">
            <i class="bi bi-patch-question-fill fs-5"></i>
          </span>
          <span class="fs-4 tracking-wide">Quiz<span class="text-primary">Base</span></span>
        </a>

        <button class="navbar-toggler border-0" type="button" data-bs-toggle="collapse" data-bs-target="#navContent">
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navContent">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-3 gap-1">
            <li class="nav-item">
              <a class="nav-link px-3 rounded-pill" routerLink="/dashboard" routerLinkActive="active bg-secondary bg-opacity-25 text-white">
                <i class="bi bi-speedometer2 me-1"></i> Dashboard
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link px-3 rounded-pill" routerLink="/quizzes" routerLinkActive="active bg-secondary bg-opacity-25 text-white">
                <i class="bi bi-collection-play me-1"></i> Browse Quizzes
              </a>
            </li>
            <li class="nav-item" *ngIf="authService.isInstructorOrAdmin()">
              <a class="nav-link px-3 rounded-pill" routerLink="/my-quizzes" routerLinkActive="active bg-secondary bg-opacity-25 text-white">
                <i class="bi bi-folder-check me-1"></i> My Quizzes
              </a>
            </li>
            <li class="nav-item" *ngIf="authService.isInstructorOrAdmin()">
              <a class="nav-link px-3 rounded-pill" routerLink="/quizzes/create" routerLinkActive="active bg-secondary bg-opacity-25 text-white">
                <i class="bi bi-plus-circle me-1"></i> Create Quiz
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link px-3 rounded-pill" routerLink="/categories" routerLinkActive="active bg-secondary bg-opacity-25 text-white">
                <i class="bi bi-tags me-1"></i> Categories
              </a>
            </li>
          </ul>

          <div class="d-flex align-items-center gap-3">
            <div class="text-light d-flex align-items-center gap-2" *ngIf="authService.currentUser() as user">
              <div class="text-end d-none d-sm-block">
                <div class="fw-semibold small">{{ user.fullName || user.username }}</div>
                <span class="badge" [ngClass]="{
                  'bg-success': user.role === 'ROLE_INSTRUCTOR',
                  'bg-danger': user.role === 'ROLE_ADMIN',
                  'bg-info text-dark': user.role === 'ROLE_STUDENT'
                }">
                  {{ getRoleBadge(user.role) }}
                </span>
              </div>
              <div class="rounded-circle bg-secondary bg-opacity-50 text-white p-2 d-flex align-items-center justify-content-center" style="width: 38px; height: 38px;">
                <i class="bi bi-person-fill"></i>
              </div>
            </div>

            <button class="btn btn-outline-light btn-sm rounded-pill px-3" (click)="logout()">
              <i class="bi bi-box-arrow-right me-1"></i> Logout
            </button>
          </div>
        </div>
      </div>
    </nav>
  `
})
export class NavbarComponent {
  constructor(public authService: AuthService, private router: Router) {}

  getRoleBadge(role: string): string {
    if (role === 'ROLE_INSTRUCTOR') return 'Instructor';
    if (role === 'ROLE_ADMIN') return 'Admin';
    return 'Student';
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
