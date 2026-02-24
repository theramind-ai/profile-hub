import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StorageService } from './storage.service';

interface LoginRequest {
  email: string;
  password: string;
}

interface RegisterRequest {
  email: string;
  username: string;
  password: string;
  firstName: string;
  lastName: string;
}

interface AuthResponse {
  token: string;
  tokenType: string;
  expiresIn: number;
  user: any;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(
    private http: HttpClient,
    private storage: StorageService
  ) {}

  login(email: string, password: string): Observable<AuthResponse> {
    const payload: LoginRequest = { email, password };
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, payload);
  }

  register(data: any): Observable<AuthResponse> {
    const payload: RegisterRequest = {
      email: data.email,
      username: data.username,
      password: data.password,
      firstName: data.firstName,
      lastName: data.lastName
    };
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, payload);
  }

  logout(): void {
    this.storage.removeToken();
  }

  isAuthenticated(): boolean {
    return !!this.storage.getToken();
  }

  getToken(): string | null {
    return this.storage.getToken();
  }
}
