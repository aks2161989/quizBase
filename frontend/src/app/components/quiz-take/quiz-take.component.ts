import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { QuizService } from '../../services/quiz.service';
import { QuizDetailDto } from '../../models/quiz.model';

@Component({
  selector: 'app-quiz-take',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="container py-4 pb-5 mb-5">
      <!-- Loading State -->
      <div *ngIf="isLoading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status"></div>
        <p class="text-muted mt-2">Loading quiz questions...</p>
      </div>

      <!-- Error State -->
      <div *ngIf="errorMessage" class="alert alert-danger rounded-4 p-4 text-center">
        <i class="bi bi-exclamation-triangle-fill display-5 mb-3 d-block"></i>
        <h5 class="fw-bold">{{ errorMessage }}</h5>
        <a routerLink="/quizzes" class="btn btn-outline-danger rounded-pill px-4 mt-2">Back to Quizzes</a>
      </div>

      <!-- Quiz Player -->
      <div *ngIf="quiz && !isLoading">
        <!-- Header Banner -->
        <div class="card border-0 shadow-sm rounded-4 mb-4 bg-white">
          <div class="card-body p-4">
            <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-2">
              <span class="badge bg-primary bg-opacity-10 text-primary border border-primary border-opacity-25 rounded-pill px-3 py-1">
                {{ quiz.categoryName }}
              </span>
              <span class="badge bg-light text-muted border rounded-pill">
                {{ answeredCount }} / {{ quiz.questions.length }} Answered
              </span>
            </div>

            <h3 class="fw-bold mb-1">{{ quiz.title }}</h3>
            <p class="text-muted mb-3" *ngIf="quiz.description">{{ quiz.description }}</p>

            <!-- Progress Bar -->
            <div class="progress rounded-pill" style="height: 10px;">
              <div
                class="progress-bar bg-gradient-primary rounded-pill transition-all"
                role="progressbar"
                [style.width.%]="progressPercentage"
                [attr.aria-valuenow]="progressPercentage"
                aria-valuemin="0"
                aria-valuemax="100"
              ></div>
            </div>
          </div>
        </div>

        <!-- Question Cards -->
        <div class="d-flex flex-column gap-4">
          <div
            *ngFor="let question of quiz.questions; let i = index"
            class="card border-0 shadow-sm rounded-4"
            [class.border-primary]="selectedAnswers[question.id!]"
          >
            <div class="card-body p-4">
              <div class="d-flex align-items-center gap-2 mb-3">
                <span class="badge bg-primary rounded-circle p-2" style="width: 28px; height: 28px; display: inline-flex; align-items: center; justify-content: center;">
                  {{ i + 1 }}
                </span>
                <span class="text-muted small fw-semibold text-uppercase">Question {{ i + 1 }} of {{ quiz.questions.length }}</span>
                <span
                  *ngIf="selectedAnswers[question.id!]"
                  class="badge bg-success bg-opacity-10 text-success ms-auto rounded-pill px-2"
                >
                  <i class="bi bi-check2"></i> Answered
                </span>
              </div>

              <h5 class="fw-bold mb-4 text-dark">{{ question.text }}</h5>

              <!-- Options -->
              <div class="d-flex flex-column gap-2">
                <div
                  *ngFor="let opt of question.options"
                  class="p-3 rounded-3 border cursor-pointer transition-all d-flex align-items-center gap-3"
                  [ngClass]="{
                    'bg-primary bg-opacity-10 border-primary text-primary fw-semibold': selectedAnswers[question.id!] === opt.id,
                    'bg-light border-light-subtle hover-bg-light': selectedAnswers[question.id!] !== opt.id
                  }"
                  (click)="selectOption(question.id!, opt.id!)"
                >
                  <div
                    class="rounded-circle border d-flex align-items-center justify-content-center"
                    style="width: 22px; height: 22px;"
                    [ngClass]="{
                      'border-primary bg-primary text-white': selectedAnswers[question.id!] === opt.id,
                      'border-secondary': selectedAnswers[question.id!] !== opt.id
                    }"
                  >
                    <i class="bi bi-check-lg small" *ngIf="selectedAnswers[question.id!] === opt.id"></i>
                  </div>
                  <span class="flex-grow-1">{{ opt.text }}</span>
                </div>
              </div>

              <div *ngIf="selectedAnswers[question.id!]" class="text-end mt-2">
                <button
                  type="button"
                  class="btn btn-sm btn-link text-muted text-decoration-none small"
                  (click)="clearOption(question.id!)"
                >
                  <i class="bi bi-x-circle me-1"></i> Clear choice
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Sticky Bottom Bar -->
        <nav class="navbar fixed-bottom bg-white border-top shadow-lg py-3">
          <div class="container d-flex justify-content-between align-items-center">
            <div class="d-flex align-items-center gap-3">
              <span class="fw-bold text-dark d-none d-sm-inline">{{ answeredCount }} of {{ quiz.questions.length }} answered</span>
              <span *ngIf="unansweredCount > 0" class="badge bg-warning bg-opacity-25 text-warning border border-warning">
                {{ unansweredCount }} unanswered
              </span>
            </div>

            <div class="d-flex gap-2">
              <a routerLink="/quizzes" class="btn btn-outline-secondary rounded-pill px-3">
                Cancel
              </a>
              <button
                type="button"
                class="btn btn-primary rounded-pill px-4 fw-bold shadow-sm"
                [disabled]="isSubmitting"
                (click)="submitQuiz()"
              >
                <span *ngIf="isSubmitting" class="spinner-border spinner-border-sm me-1" role="status"></span>
                <i *ngIf="!isSubmitting" class="bi bi-send-check-fill me-1"></i>
                Submit Answers
              </button>
            </div>
          </div>
        </nav>
      </div>
    </div>
  `
})
export class QuizTakeComponent implements OnInit {
  quiz: QuizDetailDto | null = null;
  selectedAnswers: { [questionId: number]: number } = {};
  isLoading = true;
  isSubmitting = false;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private quizService: QuizService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.errorMessage = 'Invalid quiz ID';
      this.isLoading = false;
      return;
    }
    this.loadQuiz(id);
  }

  loadQuiz(id: number): void {
    this.quizService.getQuizForPlay(id).subscribe({
      next: (q) => {
        this.quiz = q;
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.error || 'Failed to load quiz. Please try again.';
      }
    });
  }

  selectOption(questionId: number, optionId: number): void {
    this.selectedAnswers[questionId] = optionId;
  }

  clearOption(questionId: number): void {
    delete this.selectedAnswers[questionId];
  }

  get answeredCount(): number {
    return Object.keys(this.selectedAnswers).length;
  }

  get unansweredCount(): number {
    if (!this.quiz) return 0;
    return Math.max(0, this.quiz.questions.length - this.answeredCount);
  }

  get progressPercentage(): number {
    if (!this.quiz || this.quiz.questions.length === 0) return 0;
    return Math.round((this.answeredCount / this.quiz.questions.length) * 100);
  }

  submitQuiz(): void {
    if (!this.quiz) return;

    if (this.unansweredCount > 0) {
      const confirmSubmit = confirm(
        `You have left ${this.unansweredCount} question(s) unanswered. Are you sure you want to submit?`
      );
      if (!confirmSubmit) return;
    }

    this.isSubmitting = true;
    this.quizService.submitQuiz(this.quiz.id, this.selectedAnswers).subscribe({
      next: (result) => {
        this.isSubmitting = false;
        this.router.navigate(['/quizzes/result', result.quizId]);
      },
      error: (err) => {
        this.isSubmitting = false;
        alert(err.error?.error || 'Submission failed. Please try again.');
      }
    });
  }
}
