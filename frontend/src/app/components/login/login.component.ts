import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
          <div class="card shadow-lg border-0 rounded-4 overflow-hidden">
            <div class="card-header bg-gradient-primary text-white p-4 text-center">
              <div class="badge bg-white text-primary p-2 rounded-circle mb-2">
                <i class="bi bi-shield-lock-fill fs-3"></i>
              </div>
              <h3 class="fw-bold mb-1">Welcome Back</h3>
              <p class="mb-0 text-white-50 small">Sign in to your QuizBase account</p>
            </div>

            <div class="card-body p-4 p-md-5">
              <div *ngIf="errorMessage" class="alert alert-danger d-flex align-items-center rounded-3 mb-4" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2 fs-5"></i>
                <div>{{ errorMessage }}</div>
              </div>

              <form (ngSubmit)="onSubmit()">
                <div class="mb-3">
                  <label for="username" class="form-label fw-semibold small text-muted">Username</label>
                  <div class="input-group">
                    <span class="input-group-text bg-light border-end-0"><i class="bi bi-person text-muted"></i></span>
                    <input
                      type="text"
                      id="username"
                      name="username"
                      [(ngModel)]="username"
                      required
                      class="form-control border-start-0 ps-0"
                      placeholder="e.g. john_doe"
                    />
                  </div>
                </div>

                <div class="mb-4">
                  <label for="password" class="form-label fw-semibold small text-muted">Password</label>
                  <div class="input-group">
                    <span class="input-group-text bg-light border-end-0"><i class="bi bi-key text-muted"></i></span>
                    <input
                      type="password"
                      id="password"
                      name="password"
                      [(ngModel)]="password"
                      required
                      class="form-control border-start-0 ps-0"
                      placeholder="••••••••"
                    />
                  </div>
                </div>

                <button
                  type="submit"
                  class="btn btn-primary w-100 py-2 rounded-3 fw-bold shadow-sm"
                  [disabled]="isLoading || !username || !password"
                >
                  <span *ngIf="isLoading" class="spinner-border spinner-border-sm me-2" role="status"></span>
                  {{ isLoading ? 'Signing in...' : 'Sign In' }}
                </button>
              </form>

              <!-- Demo Accounts Helper -->
              <div class="mt-4 pt-3 border-top text-center">
                <p class="text-muted small mb-2 fw-semibold">Quick-fill Demo Credentials:</p>
                <div class="d-flex justify-content-center gap-2">
                  <button type="button" class="btn btn-sm btn-outline-secondary rounded-pill" (click)="fillDemo('john_doe', 'password123')">
                    <i class="bi bi-mortarboard me-1"></i> Instructor (john_doe)
                  </button>
                  <button type="button" class="btn btn-sm btn-outline-secondary rounded-pill" (click)="fillDemo('jane_smith', 'password456')">
                    <i class="bi bi-person me-1"></i> Student (jane_smith)
                  </button>
                </div>
              </div>

              <div class="text-center mt-4 pt-2">
                <span class="text-muted small">Don't have an account? </span>
                <a routerLink="/register" class="fw-semibold text-primary text-decoration-none">Sign Up</a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class LoginComponent {
  username = '';
  password = '';
  errorMessage = '';
  isLoading = false;

  constructor(private authService: AuthService, private router: Router) {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/dashboard']);
    }
  }

  fillDemo(u: string, p: string): void {
    this.username = u;
    this.password = p;
    this.errorMessage = '';
  }

  onSubmit(): void {
    if (!this.username || !this.password) return;
    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login({ username: this.username, password: this.password }).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.error || 'Invalid credentials or connection failed.';
      }
    });
  }
}
