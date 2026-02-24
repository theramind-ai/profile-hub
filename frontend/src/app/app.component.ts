import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, Router } from '@angular/router';
import { AuthService } from './services/auth.service';
import { StorageService } from './services/storage.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink],
  template: `
    <div class="app-wrapper">
      <nav class="navbar" *ngIf="isAuthenticated()">
        <div class="nav-container">
          <div class="nav-brand">
            <h1>ProfileHub</h1>
          </div>
          <div class="nav-menu">
            <a routerLink="/dashboard" class="nav-link">Dashboard</a>
            <a routerLink="/documents" class="nav-link">My Documents</a>
            <a routerLink="/upload" class="nav-link">Upload</a>
            <button (click)="logout()" class="btn-logout">Logout</button>
          </div>
        </div>
      </nav>

      <main class="main-content">
        <router-outlet></router-outlet>
      </main>

      <footer class="app-footer">
        <p>&copy; 2026 ProfileHub. All rights reserved.</p>
      </footer>
    </div>
  `,
  styles: [`
    .app-wrapper {
      display: flex;
      flex-direction: column;
      min-height: 100vh;
    }

    .navbar {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 0;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    }

    .nav-container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 70px;
    }

    .nav-brand h1 {
      margin: 0;
      font-size: 24px;
      font-weight: bold;
    }

    .nav-menu {
      display: flex;
      gap: 30px;
      align-items: center;
    }

    .nav-link {
      color: white;
      text-decoration: none;
      transition: opacity 0.3s;
      font-size: 14px;
    }

    .nav-link:hover {
      opacity: 0.8;
    }

    .btn-logout {
      background: rgba(255, 255, 255, 0.2);
      color: white;
      border: 1px solid white;
      padding: 8px 16px;
      border-radius: 5px;
      cursor: pointer;
      transition: all 0.3s;
      font-size: 14px;
    }

    .btn-logout:hover {
      background: white;
      color: #667eea;
    }

    .main-content {
      flex: 1;
    }

    .app-footer {
      background: #333;
      color: white;
      text-align: center;
      padding: 20px;
      margin-top: auto;
    }

    .app-footer p {
      margin: 0;
    }

    @media (max-width: 768px) {
      .nav-container {
        flex-direction: column;
        height: auto;
        padding: 10px 20px;
      }

      .nav-menu {
        flex-direction: column;
        gap: 10px;
        width: 100%;
      }

      .nav-link {
        display: block;
      }

      .btn-logout {
        width: 100%;
      }
    }
  `]
})
export class AppComponent {
  private authService = inject(AuthService);
  private storageService = inject(StorageService);
  private router = inject(Router);

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  logout(): void {
    this.authService.logout();
    this.storageService.clear();
    this.router.navigate(['/login']);
  }
}
