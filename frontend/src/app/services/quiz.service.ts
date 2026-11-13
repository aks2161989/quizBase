import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { QuizSummaryDto, QuizDetailDto, QuizCreateRequest, QuizResultDto, QuizSubmissionDto } from '../models/quiz.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class QuizService {
  private readonly apiUrl = `${environment.apiUrl}/quizzes`;
  latestResult = signal<QuizResultDto | null>(null);

  constructor(private http: HttpClient) {}

  getAllQuizzes(): Observable<QuizSummaryDto[]> {
    return this.http.get<QuizSummaryDto[]>(this.apiUrl);
  }

  getQuizById(id: number): Observable<QuizDetailDto> {
    return this.http.get<QuizDetailDto>(`${this.apiUrl}/${id}`);
  }

  getQuizForPlay(id: number): Observable<QuizDetailDto> {
    return this.http.get<QuizDetailDto>(`${this.apiUrl}/${id}/play`);
  }

  getMyQuizzes(): Observable<QuizSummaryDto[]> {
    return this.http.get<QuizSummaryDto[]>(`${this.apiUrl}/my-quizzes`);
  }

  createQuiz(data: QuizCreateRequest): Observable<QuizSummaryDto> {
    return this.http.post<QuizSummaryDto>(this.apiUrl, data);
  }

  updateQuiz(id: number, data: QuizCreateRequest): Observable<QuizSummaryDto> {
    return this.http.put<QuizSummaryDto>(`${this.apiUrl}/${id}`, data);
  }

  deleteQuiz(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  submitQuiz(id: number, answers: { [key: number]: number }): Observable<QuizResultDto> {
    const payload: QuizSubmissionDto = { answers };
    return this.http.post<QuizResultDto>(`${this.apiUrl}/${id}/submit`, payload).pipe(
      tap(result => this.latestResult.set(result))
    );
  }
}
