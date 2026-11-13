import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CategoryService } from '../../services/category.service';
import { AuthService } from '../../services/auth.service';
import { CategoryDto } from '../../models/category.model';

@Component({
  selector: 'app-category-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="container py-4">
      <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-4">
        <div>
          <h2 class="fw-bold mb-1"><i class="bi bi-tags-fill text-warning me-2"></i>Subject Categories</h2>
          <p class="text-muted mb-0">Browse subject categories or add a new category to organize quizzes.</p>
        </div>

        <button
          type="button"
          class="btn btn-primary rounded-pill px-4 shadow-sm"
          (click)="showCreateForm = !showCreateForm"
        >
          <i class="bi" [ngClass]="showCreateForm ? 'bi-x-lg' : 'bi-plus-lg'"></i>
          {{ showCreateForm ? 'Close Form' : 'New Category' }}
        </button>
      </div>

      <!-- Create Category Form (Collapsible) -->
      <div class="card border-0 shadow-sm rounded-4 mb-4 bg-light" *ngIf="showCreateForm">
        <div class="card-body p-4">
          <h5 class="fw-bold mb-3"><i class="bi bi-folder-plus text-primary me-2"></i>Create New Category</h5>
          <div *ngIf="formError" class="alert alert-danger rounded-3 py-2 px-3 small">{{ formError }}</div>

          <form (ngSubmit)="createCategory()" class="row g-2 align-items-center">
            <div class="col-md-8">
              <input
                type="text"
                class="form-control rounded-3"
                placeholder="Enter category name (e.g. Computer Science, Literature, Biology)..."
                [(ngModel)]="newCategoryName"
                name="categoryName"
                required
              />
            </div>
            <div class="col-md-4 d-flex gap-2">
              <button
                type="submit"
                class="btn btn-primary rounded-pill px-4 fw-semibold"
                [disabled]="isSubmitting || !newCategoryName.trim()"
              >
                <span *ngIf="isSubmitting" class="spinner-border spinner-border-sm me-1" role="status"></span>
                Save Category
              </button>
              <button
                type="button"
                class="btn btn-outline-secondary rounded-pill px-3"
                (click)="showCreateForm = false; newCategoryName = ''"
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Categories Grid -->
      <div *ngIf="isLoading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status"></div>
        <p class="text-muted mt-2">Loading categories...</p>
      </div>

      <div class="row g-3" *ngIf="!isLoading">
        <div class="col-md-6 col-lg-4" *ngFor="let cat of categories">
          <div class="card border-0 shadow-sm rounded-4 h-100 card-hover p-3 d-flex flex-row align-items-center justify-content-between">
            <div class="d-flex align-items-center gap-3">
              <div class="rounded-4 p-3 bg-warning bg-opacity-10 text-warning">
                <i class="bi bi-tag-fill fs-4"></i>
              </div>
              <div>
                <h5 class="fw-bold mb-0 text-dark">{{ cat.name }}</h5>
                <small class="text-muted" *ngIf="cat.creatorUsername">
                  Added by {{ cat.creatorUsername }}
                </small>
              </div>
            </div>

            <div class="d-flex align-items-center gap-2">
              <a [routerLink]="['/quizzes']" class="btn btn-sm btn-outline-primary rounded-pill px-3">
                Quizzes
              </a>
              <button
                *ngIf="canDelete(cat)"
                type="button"
                class="btn btn-sm btn-outline-danger rounded-circle p-1"
                style="width: 32px; height: 32px;"
                (click)="deleteCategory(cat)"
                title="Delete Category"
              >
                <i class="bi bi-trash"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class CategoryListComponent implements OnInit {
  categories: CategoryDto[] = [];
  showCreateForm = false;
  newCategoryName = '';
  formError = '';
  isLoading = true;
  isSubmitting = false;

  constructor(
    private categoryService: CategoryService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.isLoading = true;
    this.categoryService.getAllCategories().subscribe({
      next: (data) => {
        this.categories = data || [];
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  createCategory(): void {
    if (!this.newCategoryName.trim()) return;
    this.isSubmitting = true;
    this.formError = '';

    this.categoryService.createCategory(this.newCategoryName.trim()).subscribe({
      next: (cat) => {
        this.categories.push(cat);
        this.newCategoryName = '';
        this.showCreateForm = false;
        this.isSubmitting = false;
      },
      error: (err) => {
        this.isSubmitting = false;
        this.formError = err.error?.error || 'Failed to create category. It may already exist.';
      }
    });
  }

  canDelete(cat: CategoryDto): boolean {
    const user = this.authService.currentUser();
    if (!user) return false;
    return user.role === 'ROLE_ADMIN' || user.username === cat.creatorUsername;
  }

  deleteCategory(cat: CategoryDto): void {
    if (!cat.id) return;
    const ok = confirm(`Are you sure you want to delete category "${cat.name}"?`);
    if (!ok) return;

    this.categoryService.deleteCategory(cat.id).subscribe({
      next: () => {
        this.categories = this.categories.filter(c => c.id !== cat.id);
      },
      error: (err) => {
        alert(err.error?.error || 'Failed to delete category.');
      }
    });
  }
}
