import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { UploadResponse } from '../models/upload-response.model';

@Injectable({ providedIn: 'root' })
export class EmployeePairService {
  private readonly http = inject(HttpClient);

  upload(file: File): Observable<UploadResponse> {
    const formData = new FormData();
    formData.append('file', file, file.name);
    return this.http.post<UploadResponse>('/api/employees/upload', formData);
  }
}
