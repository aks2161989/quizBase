import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { QuizService } from '../../services/quiz.service';
import { QuizSummaryDto } from '../../models/quiz.model';

@Component({
  selector: 'app-my-quizzes',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="container py-4">
      <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-4">
        <div>
          <h2 class="fw-bold mb-1"><i class="bi bi-folder-check text-success me-2"></i>My Created Quizzes</h2>
          <p class="text-muted mb-0">Manage, edit, or remove the quizzes you have authored.</p>
        </div>

        <a routerLink="/quizzes/create" class="btn btn-primary rounded-pill px-4 shadow-sm">
          <i class="bi bi-plus-lg me-1"></i> Create New Quiz
        </a>
      </div>

      <div *ngIf="isLoading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status"></div>
        <p class="text-muted mt-2">Loading your quizzes...</p>
      </div>

      <div *ngIf="!isLoading && quizzes.length === 0" class="text-center py-5">
        <div class="card border-0 shadow-sm rounded-4 p-5 max-w-md mx-auto">
          <i class="bi bi-folder-x display-3 text-muted mb-3"></i>
          <h4 class="fw-bold">No Quizzes Created Yet</h4>
          <p class="text-muted">Start creating interactive quizzes for your students.</p>
          <div class="mt-2">
            <a routerLink="/quizzes/create" class="btn btn-primary rounded-pill px-4">
              <i class="bi bi-plus-lg me-1"></i> Create Your First Quiz
            </a>
          </div>
        </div>
      </div>

      <div class="card border-0 shadow-sm rounded-4 overflow-hidden" *ngIf="!isLoading && quizzes.length > 0">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light text-muted small text-uppercase">
              <tr>
                <th class="ps-4">Quiz Title</th>
                <th>Category</th>
                <th>Questions</th>
                <th class="text-end pe-4">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let quiz of quizzes">
                <td class="ps-4 py-3">
                  <div class="fw-bold text-dark">{{ quiz.title }}</div>
                  <small class="text-muted text-truncate d-inline-block" style="max-width: 320px;">
                    {{ quiz.description || 'No description' }}
                  </small>
                </td>
                <td>
                  <span class="badge bg-primary bg-opacity-10 text-primary border border-primary border-opacity-25 rounded-pill px-3 py-1">
                    {{ quiz.categoryName }}
                  </span>
                </td>
                <td>
                  <span class="badge bg-light text-muted border rounded-pill">
                    {{ quiz.questionCount }} Questions
                  </span>
                </td>
                <td class="text-end pe-4">
                  <div class="d-inline-flex gap-2">
                    <a [routerLink]="['/quizzes/take', quiz.id]" class="btn btn-sm btn-outline-primary rounded-pill px-3">
                      <i class="bi bi-play-circle me-1"></i> Test
                    </a>
                    <a [routerLink]="['/quizzes/edit', quiz.id]" class="btn btn-sm btn-outline-secondary rounded-pill px-3">
                      <i class="bi bi-pencil me-1"></i> Edit
                    </a>
                    <button
                      type="button"
                      class="btn btn-sm btn-outline-danger rounded-pill px-3"
                      (click)="deleteQuiz(quiz)"
                    >
                      <i class="bi bi-trash3 me-1"></i> Delete
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `
})
export class MyQuizzesComponent implements OnInit {
  quizzes: QuizSummaryDto[] = [];
  isLoading = true;

  constructor(private quizService: QuizService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;
    this.quizService.getMyQuizzes().subscribe({
      next: (data) => {
        this.quizzes = data || [];
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  deleteQuiz(quiz: QuizSummaryDto): void {
    const confirmed = confirm(`Are you sure you want to delete the quiz "${quiz.title}"? This cannot be undone.`);
    if (!confirmed) return;

    this.quizService.deleteQuiz(quiz.id).subscribe({
      next: () => {
        this.quizzes = this.quizzes.filter(q => q.id !== quiz.id);
      },
      error: (err) => {
        alert(err.error?.error || 'Failed to delete quiz.');
      }
    });
  }
}
