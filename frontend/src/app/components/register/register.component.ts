import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { StorageService } from '../../services/storage.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="register-container">
      <div class="register-card">
        <h2>Register - ProfileHub</h2>
        
        <div *ngIf="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label for="email">Email</label>
            <input 
              type="email" 
              id="email" 
              formControlName="email"
              placeholder="your@email.com"
              [class.is-invalid]="isFieldInvalid('email')"
            />
            <small *ngIf="isFieldInvalid('email')" class="error">
              Email is required and must be valid
            </small>
          </div>

          <div class="form-group">
            <label for="username">Username</label>
            <input 
              type="text" 
              id="username" 
              formControlName="username"
              placeholder="Choose a username"
              [class.is-invalid]="isFieldInvalid('username')"
            />
            <small *ngIf="isFieldInvalid('username')" class="error">
              Username is required (3-50 characters)
            </small>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="firstName">First Name</label>
              <input 
                type="text" 
                id="firstName" 
                formControlName="firstName"
                placeholder="John"
                [class.is-invalid]="isFieldInvalid('firstName')"
              />
              <small *ngIf="isFieldInvalid('firstName')" class="error">
                First name is required
              </small>
            </div>

            <div class="form-group">
              <label for="lastName">Last Name</label>
              <input 
                type="text" 
                id="lastName" 
                formControlName="lastName"
                placeholder="Doe"
                [class.is-invalid]="isFieldInvalid('lastName')"
              />
              <small *ngIf="isFieldInvalid('lastName')" class="error">
                Last name is required
              </small>
            </div>
          </div>

          <div class="form-group">
            <label for="password">Password</label>
            <input 
              type="password" 
              id="password" 
              formControlName="password"
              placeholder="Enter a strong password"
              [class.is-invalid]="isFieldInvalid('password')"
            />
            <small *ngIf="isFieldInvalid('password')" class="error">
              Password is required (minimum 6 characters)
            </small>
          </div>

          <button 
            type="submit" 
            class="btn-submit"
            [disabled]="loading || !registerForm.valid"
          >
            {{ loading ? 'Creating account...' : 'Register' }}
          </button>
        </form>

        <p class="login-link">
          Already have an account? <a routerLink="/login">Login here</a>
        </p>
      </div>
    </div>
  `,
  styles: [`
    .register-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 20px;
    }

    .register-card {
      background: white;
      padding: 40px;
      border-radius: 10px;
      box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
      width: 100%;
      max-width: 500px;
    }

    h2 {
      text-align: center;
      color: #333;
      margin-bottom: 30px;
    }

    .form-group {
      margin-bottom: 20px;
    }

    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 15px;
    }

    label {
      display: block;
      margin-bottom: 5px;
      color: #555;
      font-weight: 500;
    }

    input {
      width: 100%;
      padding: 10px;
      border: 1px solid #ddd;
      border-radius: 5px;
      font-size: 14px;
      transition: border-color 0.3s;
    }

    input:focus {
      outline: none;
      border-color: #667eea;
    }

    input.is-invalid {
      border-color: #d32f2f;
    }

    .error {
      color: #d32f2f;
      display: block;
      margin-top: 5px;
      font-size: 12px;
    }

    .error-message {
      background: #ffebee;
      color: #d32f2f;
      padding: 10px;
      border-radius: 5px;
      margin-bottom: 20px;
    }

    .btn-submit {
      width: 100%;
      padding: 12px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border: none;
      border-radius: 5px;
      font-size: 16px;
      cursor: pointer;
      transition: opacity 0.3s;
    }

    .btn-submit:hover:not(:disabled) {
      opacity: 0.9;
    }

    .btn-submit:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    .login-link {
      text-align: center;
      margin-top: 20px;
      color: #555;
    }

    .login-link a {
      color: #667eea;
      text-decoration: none;
      font-weight: 500;
    }

    .login-link a:hover {
      text-decoration: underline;
    }
  `]
})
export class RegisterComponent {
  registerForm: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private storageService: StorageService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.loading = true;
      this.errorMessage = '';

      this.authService.register(this.registerForm.value).subscribe({
        next: (response) => {
          this.storageService.saveToken(response.token);
          this.storageService.saveUser(response.user);
          this.router.navigate(['/dashboard']);
        },
        error: (error) => {
          this.loading = false;
          this.errorMessage = error.error?.message || 'Registration failed. Please try again.';
        }
      });
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.registerForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }
}
