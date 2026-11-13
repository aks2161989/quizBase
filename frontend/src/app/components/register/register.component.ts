import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-7 col-lg-6">
          <div class="card shadow-lg border-0 rounded-4 overflow-hidden">
            <div class="card-header bg-gradient-primary text-white p-4 text-center">
              <div class="badge bg-white text-primary p-2 rounded-circle mb-2">
                <i class="bi bi-person-plus-fill fs-3"></i>
              </div>
              <h3 class="fw-bold mb-1">Create an Account</h3>
              <p class="mb-0 text-white-50 small">Join QuizBase to create or take interactive quizzes</p>
            </div>

            <div class="card-body p-4 p-md-5">
              <div *ngIf="errorMessage" class="alert alert-danger d-flex align-items-center rounded-3 mb-4" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2 fs-5"></i>
                <div>{{ errorMessage }}</div>
              </div>

              <div *ngIf="successMessage" class="alert alert-success d-flex align-items-center rounded-3 mb-4" role="alert">
                <i class="bi bi-check-circle-fill me-2 fs-5"></i>
                <div>{{ successMessage }}</div>
              </div>

              <form (ngSubmit)="onSubmit()">
                <div class="mb-3">
                  <label for="fullname" class="form-label fw-semibold small text-muted">Full Name</label>
                  <div class="input-group">
                    <span class="input-group-text bg-light border-end-0"><i class="bi bi-card-text text-muted"></i></span>
                    <input
                      type="text"
                      id="fullname"
                      name="fullname"
                      [(ngModel)]="fullname"
                      required
                      class="form-control border-start-0 ps-0"
                      placeholder="e.g. John Doe"
                    />
                  </div>
                </div>

                <div class="row">
                  <div class="col-md-6 mb-3">
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
                        placeholder="e.g. john123"
                      />
                    </div>
                  </div>

                  <div class="col-md-6 mb-3">
                    <label for="email" class="form-label fw-semibold small text-muted">Email</label>
                    <div class="input-group">
                      <span class="input-group-text bg-light border-end-0"><i class="bi bi-envelope text-muted"></i></span>
                      <input
                        type="email"
                        id="email"
                        name="email"
                        [(ngModel)]="email"
                        required
                        class="form-control border-start-0 ps-0"
                        placeholder="john@example.com"
                      />
                    </div>
                  </div>
                </div>

                <div class="mb-3">
                  <label for="password" class="form-label fw-semibold small text-muted">Password</label>
                  <div class="input-group">
                    <span class="input-group-text bg-light border-end-0"><i class="bi bi-lock text-muted"></i></span>
                    <input
                      type="password"
                      id="password"
                      name="password"
                      [(ngModel)]="password"
                      required
                      minlength="6"
                      class="form-control border-start-0 ps-0"
                      placeholder="At least 6 characters"
                    />
                  </div>
                </div>

                <div class="mb-4">
                  <label class="form-label fw-semibold small text-muted">Select Your Role</label>
                  <div class="row g-2">
                    <div class="col-6">
                      <input
                        type="radio"
                        class="btn-check"
                        name="role"
                        id="roleStudent"
                        value="ROLE_STUDENT"
                        [(ngModel)]="role"
                      />
                      <label class="btn btn-outline-primary w-100 p-2 text-start rounded-3" for="roleStudent">
                        <div class="fw-bold"><i class="bi bi-mortarboard me-1"></i> Student</div>
                        <small class="text-muted d-none d-sm-block">Browse and take quizzes</small>
                      </label>
                    </div>

                    <div class="col-6">
                      <input
                        type="radio"
                        class="btn-check"
                        name="role"
                        id="roleInstructor"
                        value="ROLE_INSTRUCTOR"
                        [(ngModel)]="role"
                      />
                      <label class="btn btn-outline-success w-100 p-2 text-start rounded-3" for="roleInstructor">
                        <div class="fw-bold"><i class="bi bi-person-video3 me-1"></i> Instructor</div>
                        <small class="text-muted d-none d-sm-block">Create and manage quizzes</small>
                      </label>
                    </div>
                  </div>
                </div>

                <button
                  type="submit"
                  class="btn btn-primary w-100 py-2 rounded-3 fw-bold shadow-sm"
                  [disabled]="isLoading || !username || !password || !email || !fullname"
                >
                  <span *ngIf="isLoading" class="spinner-border spinner-border-sm me-2" role="status"></span>
                  {{ isLoading ? 'Creating Account...' : 'Complete Registration' }}
                </button>
              </form>

              <div class="text-center mt-4 pt-2">
                <span class="text-muted small">Already have an account? </span>
                <a routerLink="/login" class="fw-semibold text-primary text-decoration-none">Sign In</a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class RegisterComponent {
  fullname = '';
  username = '';
  email = '';
  password = '';
  role = 'ROLE_STUDENT';
  errorMessage = '';
  successMessage = '';
  isLoading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    if (!this.username || !this.password || !this.email || !this.fullname) return;

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.register({
      username: this.username,
      email: this.email,
      password: this.password,
      fullname: this.fullname,
      role: this.role
    }).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMessage = 'Registration successful! Redirecting to dashboard...';
        setTimeout(() => this.router.navigate(['/dashboard']), 1000);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.error || 'Registration failed. Username or email may already be taken.';
      }
    });
  }
}
