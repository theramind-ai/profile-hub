import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DocumentService {
  private apiUrl = 'https://profile-hub-1hs4.onrender.com/api/documents';

  constructor(private http: HttpClient) { }

  uploadDocument(file: File, description?: string, isPublic?: boolean): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    if (description) {
      formData.append('description', description);
    }
    if (isPublic !== undefined) {
      formData.append('isPublic', isPublic.toString());
    }

    return this.http.post<any>(`${this.apiUrl}/upload`, formData);
  }

  getDocument(documentId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${documentId}`);
  }

  getUserDocuments(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/user/documents`);
  }

  getUserDocumentsPaginated(page: number = 0, size: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(`${this.apiUrl}/user/documents/paginated`, { params });
  }

  getPublicDocuments(page: number = 0, size: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(`${this.apiUrl}/public`, { params });
  }

  updateDocument(documentId: number, documentData: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${documentId}`, documentData);
  }

  deleteDocument(documentId: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${documentId}`);
  }

  downloadDocument(documentId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/download/${documentId}`, {
      responseType: 'blob'
    });
  }
}
