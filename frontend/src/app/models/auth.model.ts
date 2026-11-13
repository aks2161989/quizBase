export interface AuthResponse {
  token: string | null;
  username: string;
  role: string;
  email: string;
  fullName: string;
}

export interface LoginRequest {
  username: string;
  password?: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password?: string;
  fullname: string;
  role: string;
}
