import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DocumentService } from '../../services/document.service';

@Component({
  selector: 'app-document-upload',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="upload-container">
      <div class="upload-card">
        <h2>Upload Document</h2>
        
        <div *ngIf="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <div *ngIf="successMessage" class="success-message">
          {{ successMessage }}
        </div>

        <form (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label for="file">Choose File</label>
            <div class="file-input-wrapper">
              <input 
                type="file" 
                id="file" 
                #fileInput
                (change)="onFileSelected($event)"
                accept=".pdf,.doc,.docx,.xlsx,.xls,.ppt,.pptx,.jpg,.jpeg,.png,.gif,.zip,.rar,.txt"
              />
              <span class="file-name" *ngIf="selectedFileName">
                {{ selectedFileName }}
              </span>
              <span class="placeholder" *ngIf="!selectedFileName">
                Click to select a file or drag and drop
              </span>
            </div>
            <small class="info">
              Max file size: 50MB. Allowed types: PDF, DOC, DOCX, XLSX, XLS, PPT, PPTX, JPG, PNG, GIF, ZIP, RAR, TXT
            </small>
          </div>

          <div class="form-group">
            <label for="description">Description (Optional)</label>
            <textarea 
              id="description"
              [(ngModel)]="description"
              name="description"
              placeholder="Add a description for your document"
              rows="4"
            ></textarea>
          </div>

          <div class="form-group checkbox">
            <input 
              type="checkbox" 
              id="isPublic"
              [(ngModel)]="isPublic"
              name="isPublic"
            />
            <label for="isPublic">Make this document public</label>
          </div>

          <button 
            type="submit" 
            class="btn-submit"
            [disabled]="loading || !selectedFile"
          >
            {{ loading ? 'Uploading...' : 'Upload Document' }}
          </button>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .upload-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 20px;
    }

    .upload-card {
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

    label {
      display: block;
      margin-bottom: 8px;
      color: #555;
      font-weight: 500;
    }

    .file-input-wrapper {
      position: relative;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 40px;
      border: 2px dashed #ddd;
      border-radius: 5px;
      background: #fafafa;
      cursor: pointer;
      transition: border-color 0.3s;
    }

    .file-input-wrapper:hover {
      border-color: #667eea;
    }

    #file {
      position: absolute;
      opacity: 0;
      width: 100%;
      height: 100%;
      cursor: pointer;
    }

    .file-name {
      color: #667eea;
      font-weight: 500;
    }

    .placeholder {
      color: #999;
    }

    .info {
      display: block;
      margin-top: 5px;
      color: #999;
      font-size: 12px;
    }

    textarea {
      width: 100%;
      padding: 10px;
      border: 1px solid #ddd;
      border-radius: 5px;
      font-size: 14px;
      resize: vertical;
      font-family: inherit;
    }

    textarea:focus {
      outline: none;
      border-color: #667eea;
    }

    .checkbox {
      display: flex;
      align-items: center;
    }

    .checkbox input[type="checkbox"] {
      width: auto;
      margin-right: 10px;
      cursor: pointer;
    }

    .checkbox label {
      margin: 0;
      cursor: pointer;
    }

    .error-message {
      background: #ffebee;
      color: #d32f2f;
      padding: 10px;
      border-radius: 5px;
      margin-bottom: 20px;
    }

    .success-message {
      background: #e8f5e9;
      color: #2e7d32;
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

    @media (max-width: 480px) {
      .upload-card {
        padding: 24px;
      }

      .file-input-wrapper {
        padding: 24px;
      }
    }
  `]
})
export class DocumentUploadComponent {
  selectedFile: File | null = null;
  selectedFileName = '';
  description = '';
  isPublic = false;
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private documentService: DocumentService) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.selectedFileName = this.selectedFile.name;
      this.errorMessage = '';
    }
  }

  onSubmit(): void {
    if (this.selectedFile) {
      this.loading = true;
      this.errorMessage = '';
      this.successMessage = '';

      this.documentService.uploadDocument(
        this.selectedFile,
        this.description,
        this.isPublic
      ).subscribe({
        next: (response) => {
          this.loading = false;
          this.successMessage = 'Document uploaded successfully!';
          this.resetForm();
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        },
        error: (error) => {
          this.loading = false;
          this.errorMessage = error.error?.message || 'Upload failed. Please try again.';
        }
      });
    }
  }

  private resetForm(): void {
    this.selectedFile = null;
    this.selectedFileName = '';
    this.description = '';
    this.isPublic = false;
  }
}
