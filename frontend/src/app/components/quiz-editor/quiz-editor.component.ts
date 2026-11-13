import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { QuizService } from '../../services/quiz.service';
import { CategoryService } from '../../services/category.service';
import { CategoryDto } from '../../models/category.model';
import { QuizCreateRequest } from '../../models/quiz.model';

interface FormOption {
  id?: number;
  text: string;
  isCorrect: boolean;
}

interface FormQuestion {
  id?: number;
  text: string;
  options: FormOption[];
}

@Component({
  selector: 'app-quiz-editor',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="container py-4 pb-5">
      <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-4">
        <div>
          <h2 class="fw-bold mb-1">
            <i class="bi" [ngClass]="isEditMode ? 'bi-pencil-square text-info' : 'bi-plus-circle text-primary'"></i>
            {{ isEditMode ? 'Edit Quiz' : 'Create New Quiz' }}
          </h2>
          <p class="text-muted mb-0">Define quiz metadata, questions, and mark correct answers.</p>
        </div>

        <a routerLink="/my-quizzes" class="btn btn-outline-secondary rounded-pill px-3">
          <i class="bi bi-arrow-left me-1"></i> Back to My Quizzes
        </a>
      </div>

      <div *ngIf="errorMessage" class="alert alert-danger rounded-4 p-3 mb-4 d-flex align-items-center">
        <i class="bi bi-exclamation-triangle-fill me-2 fs-5"></i>
        <div>{{ errorMessage }}</div>
      </div>

      <form (ngSubmit)="saveQuiz()">
        <!-- Quiz Details Card -->
        <div class="card border-0 shadow-sm rounded-4 mb-4">
          <div class="card-header bg-white border-0 pt-4 px-4">
            <h5 class="fw-bold mb-0 text-dark">
              <i class="bi bi-info-circle text-primary me-2"></i>1. Quiz Information
            </h5>
          </div>
          <div class="card-body p-4">
            <div class="row g-3">
              <div class="col-md-8">
                <label class="form-label fw-semibold small text-muted">Quiz Title *</label>
                <input
                  type="text"
                  class="form-control rounded-3"
                  placeholder="e.g. Java Concurrency & Multi-Threading"
                  [(ngModel)]="title"
                  name="quizTitle"
                  required
                />
              </div>

              <div class="col-md-4">
                <label class="form-label fw-semibold small text-muted">Category *</label>
                <select
                  class="form-select rounded-3"
                  [(ngModel)]="categoryId"
                  name="quizCategory"
                  required
                >
                  <option [ngValue]="null" disabled>Select category...</option>
                  <option *ngFor="let cat of categories" [ngValue]="cat.id">
                    {{ cat.name }}
                  </option>
                </select>
              </div>

              <div class="col-12">
                <label class="form-label fw-semibold small text-muted">Description</label>
                <textarea
                  class="form-control rounded-3"
                  rows="2"
                  placeholder="Brief summary of what this quiz tests..."
                  [(ngModel)]="description"
                  name="quizDescription"
                ></textarea>
              </div>
            </div>
          </div>
        </div>

        <!-- Questions Card -->
        <div class="card border-0 shadow-sm rounded-4 mb-4">
          <div class="card-header bg-white border-0 pt-4 px-4 d-flex justify-content-between align-items-center">
            <h5 class="fw-bold mb-0 text-dark">
              <i class="bi bi-question-diamond text-primary me-2"></i>2. Questions & Options ({{ questions.length }})
            </h5>
            <button
              type="button"
              class="btn btn-sm btn-outline-primary rounded-pill px-3"
              (click)="addQuestion()"
            >
              <i class="bi bi-plus-lg me-1"></i> Add Question
            </button>
          </div>

          <div class="card-body p-4 d-flex flex-column gap-4">
            <div
              *ngFor="let q of questions; let qIndex = index"
              class="border rounded-4 p-4 bg-light bg-opacity-50"
            >
              <div class="d-flex justify-content-between align-items-center mb-3">
                <div class="d-flex align-items-center gap-2">
                  <span class="badge bg-primary rounded-circle p-2" style="width: 26px; height: 26px; display: inline-flex; align-items: center; justify-content: center;">
                    {{ qIndex + 1 }}
                  </span>
                  <span class="fw-bold text-dark">Question #{{ qIndex + 1 }}</span>
                </div>

                <button
                  type="button"
                  class="btn btn-sm btn-outline-danger rounded-pill px-2"
                  *ngIf="questions.length > 1"
                  (click)="removeQuestion(qIndex)"
                  title="Remove Question"
                >
                  <i class="bi bi-trash3"></i> Delete
                </button>
              </div>

              <!-- Question text -->
              <div class="mb-3">
                <input
                  type="text"
                  class="form-control rounded-3"
                  placeholder="Enter the question text..."
                  [(ngModel)]="q.text"
                  [name]="'qText_' + qIndex"
                  required
                />
              </div>

              <!-- Options -->
              <div class="ps-md-3">
                <label class="form-label fw-semibold small text-muted mb-2">
                  Options (select the radio button for the correct answer):
                </label>

                <div class="d-flex flex-column gap-2">
                  <div
                    *ngFor="let opt of q.options; let optIndex = index"
                    class="input-group"
                  >
                    <div class="input-group-text bg-white">
                      <input
                        class="form-check-input mt-0 cursor-pointer"
                        type="radio"
                        [name]="'correctOpt_' + qIndex"
                        [checked]="opt.isCorrect"
                        (change)="setCorrectOption(q, optIndex)"
                        title="Mark as correct answer"
                      />
                    </div>
                    <input
                      type="text"
                      class="form-control"
                      placeholder="Option text..."
                      [(ngModel)]="opt.text"
                      [name]="'optText_' + qIndex + '_' + optIndex"
                      required
                    />
                    <button
                      type="button"
                      class="btn btn-outline-secondary"
                      *ngIf="q.options.length > 2"
                      (click)="removeOption(q, optIndex)"
                      title="Remove Option"
                    >
                      <i class="bi bi-x-lg"></i>
                    </button>
                  </div>
                </div>

                <div class="mt-2">
                  <button
                    type="button"
                    class="btn btn-sm btn-link text-primary text-decoration-none"
                    (click)="addOption(q)"
                  >
                    <i class="bi bi-plus-circle me-1"></i> Add Another Option
                  </button>
                </div>
              </div>
            </div>

            <div class="text-center pt-2">
              <button
                type="button"
                class="btn btn-outline-primary rounded-pill px-4"
                (click)="addQuestion()"
              >
                <i class="bi bi-plus-lg me-1"></i> Add Another Question
              </button>
            </div>
          </div>
        </div>

        <!-- Submit Bar -->
        <div class="d-flex justify-content-end gap-2">
          <a routerLink="/my-quizzes" class="btn btn-light rounded-pill px-4 text-muted">
            Cancel
          </a>
          <button
            type="submit"
            class="btn btn-primary rounded-pill px-5 fw-bold shadow-sm"
            [disabled]="isSubmitting"
          >
            <span *ngIf="isSubmitting" class="spinner-border spinner-border-sm me-1" role="status"></span>
            <i *ngIf="!isSubmitting" class="bi bi-check2-circle me-1"></i>
            {{ isEditMode ? 'Update Quiz' : 'Publish Quiz' }}
          </button>
        </div>
      </form>
    </div>
  `
})
export class QuizEditorComponent implements OnInit {
  isEditMode = false;
  quizId: number | null = null;
  categories: CategoryDto[] = [];

  title = '';
  description = '';
  categoryId: number | null = null;
  questions: FormQuestion[] = [];

  isLoading = true;
  isSubmitting = false;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private quizService: QuizService,
    private categoryService: CategoryService
  ) {}

  ngOnInit(): void {
    this.categoryService.getAllCategories().subscribe({
      next: (cats) => {
        this.categories = cats || [];
        if (this.categories.length > 0 && !this.categoryId) {
          this.categoryId = this.categories[0].id || null;
        }
      }
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.quizId = Number(idParam);
      this.loadExistingQuiz(this.quizId);
    } else {
      this.isEditMode = false;
      this.initDefaultQuestion();
      this.isLoading = false;
    }
  }

  initDefaultQuestion(): void {
    this.questions = [
      {
        text: '',
        options: [
          { text: '', isCorrect: true },
          { text: '', isCorrect: false }
        ]
      }
    ];
  }

  loadExistingQuiz(id: number): void {
    this.quizService.getQuizById(id).subscribe({
      next: (quiz) => {
        this.title = quiz.title;
        this.description = quiz.description || '';
        this.categoryId = quiz.categoryId;
        this.questions = quiz.questions.map((q) => ({
          id: q.id,
          text: q.text,
          options: q.options.map((opt) => ({
            id: opt.id,
            text: opt.text,
            isCorrect: !!opt.isCorrect
          }))
        }));
        if (this.questions.length === 0) {
          this.initDefaultQuestion();
        }
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.error || 'Failed to load quiz for editing.';
      }
    });
  }

  addQuestion(): void {
    this.questions.push({
      text: '',
      options: [
        { text: '', isCorrect: true },
        { text: '', isCorrect: false }
      ]
    });
  }

  removeQuestion(index: number): void {
    if (this.questions.length > 1) {
      this.questions.splice(index, 1);
    }
  }

  addOption(q: FormQuestion): void {
    q.options.push({ text: '', isCorrect: false });
  }

  removeOption(q: FormQuestion, optIndex: number): void {
    if (q.options.length > 2) {
      const removedWasCorrect = q.options[optIndex].isCorrect;
      q.options.splice(optIndex, 1);
      if (removedWasCorrect && q.options.length > 0) {
        q.options[0].isCorrect = true;
      }
    }
  }

  setCorrectOption(q: FormQuestion, correctIndex: number): void {
    q.options.forEach((opt, idx) => {
      opt.isCorrect = idx === correctIndex;
    });
  }

  validate(): boolean {
    this.errorMessage = '';
    if (!this.title.trim()) {
      this.errorMessage = 'Quiz title is required.';
      return false;
    }
    if (!this.categoryId) {
      this.errorMessage = 'Please select a category.';
      return false;
    }
    if (this.questions.length === 0) {
      this.errorMessage = 'At least one question is required.';
      return false;
    }

    for (let i = 0; i < this.questions.length; i++) {
      const q = this.questions[i];
      if (!q.text.trim()) {
        this.errorMessage = `Question #${i + 1} text cannot be empty.`;
        return false;
      }
      if (q.options.length < 2) {
        this.errorMessage = `Question #${i + 1} must have at least 2 options.`;
        return false;
      }
      const hasCorrect = q.options.some((o) => o.isCorrect);
      if (!hasCorrect) {
        this.errorMessage = `Question #${i + 1} must have one option marked as correct.`;
        return false;
      }
      for (let j = 0; j < q.options.length; j++) {
        if (!q.options[j].text.trim()) {
          this.errorMessage = `Question #${i + 1}, Option #${j + 1} cannot be empty.`;
          return false;
        }
      }
    }

    return true;
  }

  saveQuiz(): void {
    if (!this.validate()) return;

    this.isSubmitting = true;
    const payload: QuizCreateRequest = {
      title: this.title.trim(),
      description: this.description.trim(),
      categoryId: this.categoryId!,
      questions: this.questions.map((q) => ({
        id: q.id,
        text: q.text.trim(),
        options: q.options.map((o) => ({
          id: o.id,
          text: o.text.trim(),
          isCorrect: o.isCorrect
        }))
      }))
    };

    const action$ = this.isEditMode
      ? this.quizService.updateQuiz(this.quizId!, payload)
      : this.quizService.createQuiz(payload);

    action$.subscribe({
      next: () => {
        this.isSubmitting = false;
        this.router.navigate(['/my-quizzes']);
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = err.error?.error || 'Failed to save quiz. Please try again.';
      }
    });
  }
}
