import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { QuizService } from '../../services/quiz.service';
import { CategoryService } from '../../services/category.service';
import { AuthService } from '../../services/auth.service';
import { QuizSummaryDto } from '../../models/quiz.model';
import { CategoryDto } from '../../models/category.model';

@Component({
  selector: 'app-quiz-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="container py-4">
      <!-- Header -->
      <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-4">
        <div>
          <h2 class="fw-bold mb-1"><i class="bi bi-collection-play text-primary me-2"></i>Browse Quizzes</h2>
          <p class="text-muted mb-0">Select a quiz to test your mastery or filter by subject category.</p>
        </div>

        <a
          *ngIf="authService.isInstructorOrAdmin()"
          routerLink="/quizzes/create"
          class="btn btn-primary rounded-pill px-4 shadow-sm"
        >
          <i class="bi bi-plus-lg me-1"></i> Create Quiz
        </a>
      </div>

      <!-- Search & Filters -->
      <div class="card border-0 shadow-sm rounded-4 mb-4">
        <div class="card-body p-3 p-md-4">
          <div class="row g-3 align-items-center">
            <div class="col-md-6 col-lg-5">
              <div class="input-group">
                <span class="input-group-text bg-light border-end-0"><i class="bi bi-search text-muted"></i></span>
                <input
                  type="text"
                  class="form-control border-start-0 ps-0"
                  placeholder="Search by title or topic..."
                  [(ngModel)]="searchTerm"
                />
                <button *ngIf="searchTerm" class="btn btn-outline-secondary" type="button" (click)="searchTerm = ''">
                  <i class="bi bi-x"></i>
                </button>
              </div>
            </div>

            <!-- Category Pills -->
            <div class="col-md-6 col-lg-7">
              <div class="d-flex flex-wrap gap-2 align-items-center">
                <span class="text-muted small fw-semibold me-1 d-none d-lg-inline">Category:</span>
                <button
                  type="button"
                  class="btn btn-sm rounded-pill px-3"
                  [ngClass]="selectedCategoryId === null ? 'btn-primary' : 'btn-outline-secondary'"
                  (click)="selectedCategoryId = null"
                >
                  All ({{ quizzes.length }})
                </button>
                <button
                  *ngFor="let cat of categories"
                  type="button"
                  class="btn btn-sm rounded-pill px-3"
                  [ngClass]="selectedCategoryId === cat.id ? 'btn-primary' : 'btn-outline-secondary'"
                  (click)="selectedCategoryId = cat.id || null"
                >
                  {{ cat.name }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Loading / Empty / Content -->
      <div *ngIf="isLoading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status"></div>
        <p class="text-muted mt-2">Loading available quizzes...</p>
      </div>

      <div *ngIf="!isLoading && filteredQuizzes.length === 0" class="text-center py-5">
        <div class="card border-0 shadow-sm rounded-4 p-5 max-w-md mx-auto">
          <i class="bi bi-search display-3 text-muted mb-3"></i>
          <h4 class="fw-bold">No Quizzes Found</h4>
          <p class="text-muted">
            {{ searchTerm || selectedCategoryId ? 'Try clearing your filters or search keyword.' : 'No quizzes have been created yet.' }}
          </p>
          <div class="mt-2">
            <button *ngIf="searchTerm || selectedCategoryId" class="btn btn-outline-secondary rounded-pill px-4" (click)="resetFilters()">
              Reset Filters
            </button>
          </div>
        </div>
      </div>

      <!-- Quiz Grid -->
      <div class="row g-4" *ngIf="!isLoading && filteredQuizzes.length > 0">
        <div class="col-md-6 col-lg-4" *ngFor="let quiz of filteredQuizzes">
          <div class="card border-0 shadow-sm rounded-4 h-100 card-hover overflow-hidden d-flex flex-column">
            <div class="card-body p-4 flex-grow-1">
              <div class="d-flex justify-content-between align-items-start mb-2">
                <span class="badge bg-primary bg-opacity-10 text-primary border border-primary border-opacity-25 rounded-pill px-3 py-1">
                  {{ quiz.categoryName }}
                </span>
                <span class="badge bg-light text-muted border rounded-pill">
                  <i class="bi bi-question-circle me-1"></i>{{ quiz.questionCount }} Questions
                </span>
              </div>

              <h5 class="fw-bold text-dark mb-2 mt-2">{{ quiz.title }}</h5>
              <p class="text-muted small mb-3 text-truncate-3" style="min-height: 40px;">
                {{ quiz.description || 'No description provided for this quiz.' }}
              </p>

              <div class="text-muted small d-flex align-items-center gap-1 border-top pt-3">
                <i class="bi bi-person text-secondary"></i>
                <span>Created by <strong class="text-dark">{{ quiz.creatorUsername }}</strong></span>
              </div>
            </div>

            <div class="card-footer bg-light border-0 p-3 px-4 d-flex justify-content-between align-items-center">
              <a [routerLink]="['/quizzes/take', quiz.id]" class="btn btn-primary rounded-pill px-4 btn-sm fw-semibold shadow-sm">
                <i class="bi bi-play-circle me-1"></i> Take Quiz
              </a>

              <a
                *ngIf="canEdit(quiz)"
                [routerLink]="['/quizzes/edit', quiz.id]"
                class="btn btn-outline-secondary btn-sm rounded-pill px-3"
                title="Edit Quiz"
              >
                <i class="bi bi-pencil me-1"></i> Edit
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class QuizListComponent implements OnInit {
  quizzes: QuizSummaryDto[] = [];
  categories: CategoryDto[] = [];
  selectedCategoryId: number | null = null;
  searchTerm = '';
  isLoading = true;

  constructor(
    public authService: AuthService,
    private quizService: QuizService,
    private categoryService: CategoryService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;
    this.categoryService.getAllCategories().subscribe({
      next: (cats) => (this.categories = cats || []),
      error: () => {}
    });

    this.quizService.getAllQuizzes().subscribe({
      next: (quizzes) => {
        this.quizzes = quizzes || [];
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  get filteredQuizzes(): QuizSummaryDto[] {
    return this.quizzes.filter((q) => {
      const matchesCategory =
        this.selectedCategoryId === null || q.categoryId === this.selectedCategoryId;
      const term = this.searchTerm.trim().toLowerCase();
      const matchesSearch =
        !term ||
        q.title.toLowerCase().includes(term) ||
        (q.description && q.description.toLowerCase().includes(term));
      return matchesCategory && matchesSearch;
    });
  }

  canEdit(quiz: QuizSummaryDto): boolean {
    const user = this.authService.currentUser();
    if (!user) return false;
    return user.role === 'ROLE_ADMIN' || user.username === quiz.creatorUsername;
  }

  resetFilters(): void {
    this.searchTerm = '';
    this.selectedCategoryId = null;
  }
}
