export interface OptionDto {
  id?: number;
  text: string;
  isCorrect?: boolean | null;
}

export interface QuestionDto {
  id?: number;
  text: string;
  options: OptionDto[];
}

export interface QuizSummaryDto {
  id: number;
  title: string;
  description: string;
  categoryId: number;
  categoryName: string;
  creatorId: number;
  creatorUsername: string;
  questionCount: number;
  createdDate?: string;
}

export interface QuizDetailDto {
  id: number;
  title: string;
  description: string;
  categoryId: number;
  categoryName: string;
  creatorId: number;
  creatorUsername: string;
  createdDate?: string;
  questions: QuestionDto[];
}

export interface QuizCreateRequest {
  id?: number;
  title: string;
  description: string;
  categoryId: number;
  questions: {
    id?: number;
    text: string;
    options: {
      id?: number;
      text: string;
      isCorrect?: boolean;
    }[];
  }[];
}

export interface QuizSubmissionDto {
  answers: { [questionId: number]: number };
}

export interface QuizResultDto {
  quizId: number;
  quizTitle: string;
  score: number;
  total: number;
  correctCount: number;
  incorrectCount: number;
  unansweredCount: number;
  percentage: number;
  passed: boolean;
}
