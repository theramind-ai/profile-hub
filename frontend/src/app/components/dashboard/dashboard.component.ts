import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DocumentService } from '../../services/document.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="dashboard-container">
      <div class="dashboard-header">
        <h1>Dashboard - Welcome {{ userName }}</h1>
        <p>Manage your profile and documents</p>
      </div>

      <div class="dashboard-grid">
        <div class="dashboard-card">
          <h3>My Profile</h3>
          <p>View and edit your profile information</p>
          <button (click)="goToProfile()" class="btn-card">View Profile</button>
        </div>

        <div class="dashboard-card">
          <h3>My Documents</h3>
          <p *ngIf="documentCount !== null">{{ documentCount }} documents</p>
          <p *ngIf="documentCount === null">Loading...</p>
          <button (click)="goToDocuments()" class="btn-card">Manage Documents</button>
        </div>

        <div class="dashboard-card">
          <h3>Upload Document</h3>
          <p>Upload a new document to your profile</p>
          <button (click)="goToUpload()" class="btn-card">Upload</button>
        </div>

        <div class="dashboard-card">
          <h3>Public Gallery</h3>
          <p>View documents shared by other users</p>
          <button (click)="goToGallery()" class="btn-card">Browse</button>
        </div>
      </div>

      <div class="quick-stats">
        <div class="stat-item">
          <span class="stat-label">Profile Status:</span>
          <span class="stat-value">Active</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">Member Since:</span>
          <span class="stat-value">{{ joinDate }}</span>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container {
      padding: 30px;
      background: #f5f5f5;
      min-height: 100vh;
    }

    .dashboard-header {
      margin-bottom: 40px;
    }

    .dashboard-header h1 {
      color: #333;
      font-size: 32px;
      margin: 0 0 10px 0;
    }

    .dashboard-header p {
      color: #666;
      font-size: 16px;
      margin: 0;
    }

    .dashboard-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 20px;
      margin-bottom: 40px;
    }

    .dashboard-card {
      background: white;
      padding: 25px;
      border-radius: 10px;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
      transition: transform 0.3s, box-shadow 0.3s;
    }

    .dashboard-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
    }

    .dashboard-card h3 {
      color: #333;
      margin: 0 0 10px 0;
    }

    .dashboard-card p {
      color: #666;
      margin: 0 0 20px 0;
      font-size: 14px;
    }

    .btn-card {
      width: 100%;
      padding: 10px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      transition: opacity 0.3s;
    }

    .btn-card:hover {
      opacity: 0.9;
    }

    .quick-stats {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 20px;
    }

    .stat-item {
      background: white;
      padding: 20px;
      border-radius: 10px;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .stat-label {
      color: #666;
      font-weight: 500;
    }

    .stat-value {
      color: #667eea;
      font-weight: bold;
      font-size: 16px;
    }

    @media (max-width: 480px) {
      .dashboard-container {
        padding: 20px 16px;
      }

      .dashboard-header h1 {
        font-size: 24px;
      }
    }
  `]
})
export class DashboardComponent implements OnInit {
  userName = '';
  documentCount: number | null = null;
  joinDate = '';

  constructor(
    private userService: UserService,
    private documentService: DocumentService
  ) {}

  ngOnInit(): void {
    this.loadUserData();
  }

  private loadUserData(): void {
    // Get current user
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.userName = user.firstName || user.username;
        this.joinDate = new Date(user.createdAt).toLocaleDateString();
      },
      error: (error) => {
        console.error('Error loading user data', error);
      }
    });

    // Get document count
    this.documentService.getUserDocuments().subscribe({
      next: (documents) => {
        this.documentCount = documents.length;
      },
      error: (error) => {
        console.error('Error loading documents', error);
      }
    });
  }

  goToProfile(): void {
    // Navigate to profile component
  }

  goToDocuments(): void {
    // Navigate to documents component
  }

  goToUpload(): void {
    // Navigate to upload component
  }

  goToGallery(): void {
    // Navigate to gallery component
  }
}
