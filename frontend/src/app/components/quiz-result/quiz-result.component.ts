import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { QuizService } from '../../services/quiz.service';
import { QuizResultDto } from '../../models/quiz.model';

@Component({
  selector: 'app-quiz-result',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-lg-8 col-xl-7">
          <div class="card border-0 shadow-lg rounded-4 overflow-hidden text-center">
            <!-- Header Result Banner -->
            <div
              class="p-5 text-white"
              [ngClass]="result?.passed ? 'bg-gradient-success' : 'bg-gradient-danger'"
            >
              <div class="badge bg-white text-dark p-3 rounded-circle mb-3 shadow-sm" style="width: 72px; height: 72px; display: inline-flex; align-items: center; justify-content: center;">
                <i
                  class="bi display-6"
                  [ngClass]="result?.passed ? 'bi-trophy-fill text-success' : 'bi-arrow-repeat text-danger'"
                ></i>
              </div>

              <h2 class="fw-bold mb-1">
                {{ result?.passed ? 'Congratulations!' : 'Keep Practicing!' }}
              </h2>
              <p class="mb-0 text-white-50">
                {{ result?.passed ? 'You passed the quiz successfully.' : 'You did not meet the passing grade (60%). Try again!' }}
              </p>
            </div>

            <div class="card-body p-4 p-md-5">
              <h4 class="fw-bold text-dark mb-1">{{ result?.quizTitle || 'Quiz Assessment' }}</h4>
              <p class="text-muted small mb-4">Assessment results overview</p>

              <!-- Score Highlight -->
              <div class="d-inline-block bg-light rounded-4 p-4 mb-4 border">
                <div class="display-3 fw-bold" [ngClass]="result?.passed ? 'text-success' : 'text-danger'">
                  {{ result?.score }} <span class="fs-4 text-muted">/ {{ result?.total }}</span>
                </div>
                <div class="fw-semibold text-muted text-uppercase small mt-1">
                  Overall Score ({{ result?.percentage }}%)
                </div>
              </div>

              <!-- Breakdown Grid -->
              <div class="row g-3 mb-5">
                <div class="col-4">
                  <div class="p-3 rounded-3 bg-success bg-opacity-10 border border-success border-opacity-25">
                    <i class="bi bi-check-circle-fill text-success fs-4 mb-1 d-block"></i>
                    <h4 class="fw-bold text-success mb-0">{{ result?.correctCount || 0 }}</h4>
                    <span class="small text-muted">Correct</span>
                  </div>
                </div>

                <div class="col-4">
                  <div class="p-3 rounded-3 bg-danger bg-opacity-10 border border-danger border-opacity-25">
                    <i class="bi bi-x-circle-fill text-danger fs-4 mb-1 d-block"></i>
                    <h4 class="fw-bold text-danger mb-0">{{ result?.incorrectCount || 0 }}</h4>
                    <span class="small text-muted">Incorrect</span>
                  </div>
                </div>

                <div class="col-4">
                  <div class="p-3 rounded-3 bg-warning bg-opacity-10 border border-warning border-opacity-25">
                    <i class="bi bi-dash-circle-fill text-warning fs-4 mb-1 d-block"></i>
                    <h4 class="fw-bold text-warning mb-0">{{ result?.unansweredCount || 0 }}</h4>
                    <span class="small text-muted">Unanswered</span>
                  </div>
                </div>
              </div>

              <!-- Action Buttons -->
              <div class="d-flex flex-wrap justify-content-center gap-3">
                <a
                  [routerLink]="['/quizzes/take', quizId]"
                  class="btn btn-outline-primary rounded-pill px-4 fw-semibold"
                >
                  <i class="bi bi-arrow-clockwise me-1"></i> Retake Quiz
                </a>
                <a routerLink="/quizzes" class="btn btn-primary rounded-pill px-4 fw-bold shadow-sm">
                  <i class="bi bi-collection-play me-1"></i> Browse More Quizzes
                </a>
                <a routerLink="/dashboard" class="btn btn-light rounded-pill px-4 text-muted">
                  Dashboard
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class QuizResultComponent implements OnInit {
  quizId = 0;
  result: QuizResultDto | null = null;

  constructor(
    private route: ActivatedRoute,
    private quizService: QuizService
  ) {}

  ngOnInit(): void {
    this.quizId = Number(this.route.snapshot.paramMap.get('id'));
    this.result = this.quizService.latestResult();
  }
}
