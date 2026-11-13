import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { QuizListComponent } from './components/quiz-list/quiz-list.component';
import { QuizTakeComponent } from './components/quiz-take/quiz-take.component';
import { QuizResultComponent } from './components/quiz-result/quiz-result.component';
import { MyQuizzesComponent } from './components/my-quizzes/my-quizzes.component';
import { QuizEditorComponent } from './components/quiz-editor/quiz-editor.component';
import { CategoryListComponent } from './components/category-list/category-list.component';
import { authGuard } from './guards/auth.guard';
import { instructorGuard } from './guards/instructor.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'quizzes', component: QuizListComponent, canActivate: [authGuard] },
  { path: 'quizzes/take/:id', component: QuizTakeComponent, canActivate: [authGuard] },
  { path: 'quizzes/result/:id', component: QuizResultComponent, canActivate: [authGuard] },
  { path: 'quizzes/create', component: QuizEditorComponent, canActivate: [authGuard, instructorGuard] },
  { path: 'quizzes/edit/:id', component: QuizEditorComponent, canActivate: [authGuard, instructorGuard] },
  { path: 'my-quizzes', component: MyQuizzesComponent, canActivate: [authGuard, instructorGuard] },
  { path: 'categories', component: CategoryListComponent, canActivate: [authGuard] },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: 'dashboard' }
];
