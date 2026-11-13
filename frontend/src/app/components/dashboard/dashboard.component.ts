import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { DashboardService } from '../../services/dashboard.service';
import { QuizService } from '../../services/quiz.service';
import { AuthService } from '../../services/auth.service';
import { DashboardStatsDto } from '../../models/dashboard.model';
import { QuizSummaryDto } from '../../models/quiz.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="container py-4">
      <!-- Welcome Hero -->
      <div class="card border-0 rounded-4 bg-gradient-primary text-white shadow-sm mb-4 overflow-hidden">
        <div class="card-body p-4 p-md-5 position-relative">
          <div class="row align-items-center">
            <div class="col-lg-8">
              <span class="badge bg-white bg-opacity-25 text-white px-3 py-2 rounded-pill mb-2">
                <i class="bi bi-stars me-1"></i> CSYE 6220 Final Project
              </span>
              <h2 class="display-6 fw-bold mb-2">
                Welcome back, {{ stats?.fullName || stats?.username || 'Learner' }}!
              </h2>
              <p class="lead text-white-50 mb-4">
                Explore interactive quizzes, test your knowledge across multiple categories, and track your learning progress.
              </p>
              <div class="d-flex flex-wrap gap-2">
                <a routerLink="/quizzes" class="btn btn-light text-primary fw-bold rounded-pill px-4 shadow-sm">
                  <i class="bi bi-play-circle-fill me-1"></i> Browse Quizzes
                </a>
                <a
                  *ngIf="authService.isInstructorOrAdmin()"
                  routerLink="/quizzes/create"
                  class="btn btn-outline-light rounded-pill px-4"
                >
                  <i class="bi bi-plus-lg me-1"></i> Create New Quiz
                </a>
              </div>
            </div>
            <div class="col-lg-4 d-none d-lg-flex justify-content-center">
              <i class="bi bi-journal-check display-1 text-white opacity-25"></i>
            </div>
          </div>
        </div>
      </div>

      <!-- Metric Stats Cards -->
      <div class="row g-3 mb-4">
        <div class="col-md-4">
          <div class="card border-0 shadow-sm rounded-4 h-100 card-hover">
            <div class="card-body p-4 d-flex align-items-center">
              <div class="rounded-4 p-3 bg-primary bg-opacity-10 text-primary me-3">
                <i class="bi bi-collection display-6"></i>
              </div>
              <div>
                <h6 class="text-muted text-uppercase fw-semibold small mb-1">Available Quizzes</h6>
                <h3 class="fw-bold mb-0 text-dark">{{ stats?.totalQuizzes || 0 }}</h3>
              </div>
            </div>
          </div>
        </div>

        <div class="col-md-4">
          <div class="card border-0 shadow-sm rounded-4 h-100 card-hover">
            <div class="card-body p-4 d-flex align-items-center">
              <div class="rounded-4 p-3 bg-success bg-opacity-10 text-success me-3">
                <i class="bi bi-folder-check display-6"></i>
              </div>
              <div>
                <h6 class="text-muted text-uppercase fw-semibold small mb-1">My Created Quizzes</h6>
                <h3 class="fw-bold mb-0 text-dark">{{ stats?.myQuizzesCount || 0 }}</h3>
              </div>
            </div>
          </div>
        </div>

        <div class="col-md-4">
          <div class="card border-0 shadow-sm rounded-4 h-100 card-hover">
            <div class="card-body p-4 d-flex align-items-center">
              <div class="rounded-4 p-3 bg-warning bg-opacity-10 text-warning me-3">
                <i class="bi bi-tags display-6"></i>
              </div>
              <div>
                <h6 class="text-muted text-uppercase fw-semibold small mb-1">Subject Categories</h6>
                <h3 class="fw-bold mb-0 text-dark">{{ stats?.totalCategories || 0 }}</h3>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Actions & Featured Quizzes -->
      <div class="row g-4">
        <div class="col-lg-8">
          <div class="card border-0 shadow-sm rounded-4">
            <div class="card-header bg-white border-0 pt-4 pb-2 px-4 d-flex justify-content-between align-items-center">
              <h5 class="fw-bold mb-0"><i class="bi bi-lightning-charge-fill text-warning me-2"></i>Recent Quizzes</h5>
              <a routerLink="/quizzes" class="btn btn-sm btn-link text-primary text-decoration-none fw-semibold">
                View all ({{ recentQuizzes.length }}) <i class="bi bi-arrow-right"></i>
              </a>
            </div>
            <div class="card-body px-4 pb-4">
              <div *ngIf="isLoading" class="text-center py-4">
                <div class="spinner-border text-primary" role="status"></div>
                <p class="text-muted small mt-2">Loading quizzes...</p>
              </div>

              <div *ngIf="!isLoading && recentQuizzes.length === 0" class="text-center py-4 text-muted">
                <i class="bi bi-journal-x display-6 mb-2 d-block"></i>
                No quizzes available yet. Be the first to create one!
              </div>

              <div class="list-group list-group-flush" *ngIf="!isLoading && recentQuizzes.length > 0">
                <div
                  *ngFor="let quiz of recentQuizzes.slice(0, 4)"
                  class="list-group-item px-0 py-3 d-flex justify-content-between align-items-center border-bottom border-light"
                >
                  <div class="d-flex align-items-center gap-3">
                    <div class="rounded-3 p-2 bg-light text-primary">
                      <i class="bi bi-file-earmark-text fs-4"></i>
                    </div>
                    <div>
                      <h6 class="fw-bold mb-1">{{ quiz.title }}</h6>
                      <div class="d-flex align-items-center gap-2 small text-muted">
                        <span class="badge bg-light text-dark border">{{ quiz.categoryName }}</span>
                        <span>•</span>
                        <span>{{ quiz.questionCount }} questions</span>
                        <span>•</span>
                        <span>By {{ quiz.creatorUsername }}</span>
                      </div>
                    </div>
                  </div>

                  <a [routerLink]="['/quizzes/take', quiz.id]" class="btn btn-sm btn-outline-primary rounded-pill px-3">
                    Take Quiz <i class="bi bi-chevron-right ms-1"></i>
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Quick Shortcuts Sidebar -->
        <div class="col-lg-4">
          <div class="card border-0 shadow-sm rounded-4 mb-4">
            <div class="card-header bg-white border-0 pt-4 pb-0 px-4">
              <h5 class="fw-bold mb-0"><i class="bi bi-grid-fill text-primary me-2"></i>Quick Actions</h5>
            </div>
            <div class="card-body p-4 d-flex flex-column gap-2">
              <a routerLink="/quizzes" class="btn btn-light text-start p-3 rounded-3 d-flex align-items-center gap-3">
                <i class="bi bi-search text-primary fs-5"></i>
                <div>
                  <div class="fw-semibold">Browse All Quizzes</div>
                  <small class="text-muted">Filter by subject and start taking quizzes</small>
                </div>
              </a>

              <a
                *ngIf="authService.isInstructorOrAdmin()"
                routerLink="/quizzes/create"
                class="btn btn-light text-start p-3 rounded-3 d-flex align-items-center gap-3"
              >
                <i class="bi bi-plus-square-fill text-success fs-5"></i>
                <div>
                  <div class="fw-semibold">Create New Quiz</div>
                  <small class="text-muted">Design questions with multiple options</small>
                </div>
              </a>

              <a
                *ngIf="authService.isInstructorOrAdmin()"
                routerLink="/my-quizzes"
                class="btn btn-light text-start p-3 rounded-3 d-flex align-items-center gap-3"
              >
                <i class="bi bi-pencil-square text-info fs-5"></i>
                <div>
                  <div class="fw-semibold">Manage My Quizzes</div>
                  <small class="text-muted">Edit questions, options, or remove quizzes</small>
                </div>
              </a>

              <a routerLink="/categories" class="btn btn-light text-start p-3 rounded-3 d-flex align-items-center gap-3">
                <i class="bi bi-tags-fill text-warning fs-5"></i>
                <div>
                  <div class="fw-semibold">Explore Categories</div>
                  <small class="text-muted">Browse Arts, Science, Sports, Tech and more</small>
                </div>
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class DashboardComponent implements OnInit {
  stats: DashboardStatsDto | null = null;
  recentQuizzes: QuizSummaryDto[] = [];
  isLoading = true;

  constructor(
    public authService: AuthService,
    private dashboardService: DashboardService,
    private quizService: QuizService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;
    this.dashboardService.getStats().subscribe({
      next: (s) => (this.stats = s),
      error: () => {}
    });

    this.quizService.getAllQuizzes().subscribe({
      next: (quizzes) => {
        this.recentQuizzes = quizzes || [];
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }
}
