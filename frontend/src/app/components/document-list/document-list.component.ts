import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DocumentService } from '../../services/document.service';

@Component({
    selector: 'app-document-list',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="list-container">
      <div class="list-card">
        <h2>My Documents</h2>
        
        <div *ngIf="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <div *ngIf="loading" class="loading">
          Loading documents...
        </div>

        <div *ngIf="!loading && documents.length === 0" class="no-docs">
          You haven't uploaded any documents yet.
        </div>

        <div class="doc-grid" *ngIf="!loading && documents.length > 0">
          <div class="doc-item" *ngFor="let doc of documents">
            <div class="doc-info">
              <span class="doc-name">{{ doc.name }}</span>
              <span class="doc-meta">
                {{ (doc.fileSize / 1024).toFixed(2) }} KB | {{ doc.createdAt | date:'short' }}
              </span>
              <p class="doc-desc" *ngIf="doc.description">{{ doc.description }}</p>
              <span class="badge" [class.public]="doc.isPublic">
                {{ doc.isPublic ? 'Public' : 'Private' }}
              </span>
            </div>
            <div class="doc-actions">
              <button (click)="download(doc)" class="btn-download" [disabled]="actionLoading === doc.id">
                {{ actionLoading === doc.id ? '...' : 'Download' }}
              </button>
              <button (click)="delete(doc.id)" class="btn-delete" [disabled]="actionLoading === doc.id">
                Delete
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
    styles: [`
    .list-container {
      padding: 30px;
      background: #f5f5f5;
      min-height: 100vh;
    }
    .list-card {
      background: white;
      padding: 40px;
      border-radius: 10px;
      box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
      max-width: 900px;
      margin: 0 auto;
    }
    h2 { color: #333; margin-bottom: 20px; }
    .doc-grid { display: grid; gap: 15px; }
    .doc-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 15px;
      border: 1px solid #eee;
      border-radius: 8px;
    }
    .doc-name { font-weight: bold; color: #333; display: block; }
    .doc-meta { font-size: 12px; color: #999; }
    .doc-desc { font-size: 14px; color: #666; margin: 5px 0; }
    .badge {
      font-size: 10px;
      padding: 2px 6px;
      border-radius: 10px;
      background: #eee;
    }
    .badge.public { background: #e8f5e9; color: #2e7d32; }
    .doc-actions { display: flex; gap: 10px; }
    button {
      padding: 8px 12px;
      border-radius: 5px;
      border: none;
      cursor: pointer;
      font-size: 14px;
    }
    .btn-download { background: #667eea; color: white; }
    .btn-delete { background: #f44336; color: white; }
    .error-message { color: #f44336; margin-bottom: 10px; }
    .loading, .no-docs { text-align: center; color: #999; padding: 20px; }
  `]
})
export class DocumentListComponent implements OnInit {
    documents: any[] = [];
    loading = true;
    errorMessage = '';
    actionLoading: number | null = null;

    constructor(private documentService: DocumentService) { }

    ngOnInit(): void {
        this.loadDocuments();
    }

    loadDocuments(): void {
        this.documentService.getUserDocuments().subscribe({
            next: (docs) => {
                this.documents = docs;
                this.loading = false;
            },
            error: () => {
                this.errorMessage = 'Failed to load documents.';
                this.loading = false;
            }
        });
    }

    download(doc: any): void {
        this.actionLoading = doc.id;
        this.documentService.downloadDocument(doc.id).subscribe({
            next: (blob) => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = doc.fileName;
                a.click();
                window.URL.revokeObjectURL(url);
                this.actionLoading = null;
            },
            error: () => {
                this.errorMessage = 'Download failed.';
                this.actionLoading = null;
            }
        });
    }

    delete(id: number): void {
        if (confirm('Are you sure?')) {
            this.actionLoading = id;
            this.documentService.deleteDocument(id).subscribe({
                next: () => {
                    this.loadDocuments();
                    this.actionLoading = null;
                },
                error: () => {
                    this.errorMessage = 'Delete failed.';
                    this.actionLoading = null;
                }
            });
        }
    }
}
