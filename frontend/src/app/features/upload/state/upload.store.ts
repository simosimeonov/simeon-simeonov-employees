import { Injectable, computed, inject, signal } from '@angular/core';
import { EmployeePairService } from '../../../core/api/employee-pair.service';
import { ApiError } from '../../../core/api/api-error';
import { UploadResponse } from '../../../core/models/upload-response.model';

@Injectable()
export class UploadStore {
  private readonly api = inject(EmployeePairService);

  readonly selectedFile = signal<File | null>(null);
  readonly response = signal<UploadResponse | null>(null);
  readonly loading = signal(false);
  readonly error = signal<ApiError | null>(null);

  readonly hasResult = computed(() => this.response() !== null);
  readonly hasInvalidRows = computed(
    () => (this.response()?.invalidRows.length ?? 0) > 0,
  );
  readonly canUpload = computed(() => this.selectedFile() !== null && !this.loading());

  selectFile(file: File | null): void {
    this.selectedFile.set(file);
    this.error.set(null);
  }

  upload(): void {
    const file = this.selectedFile();
    if (!file || this.loading()) {
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    this.response.set(null);

    this.api.upload(file).subscribe({
      next: (response) => {
        this.response.set(response);
        this.loading.set(false);
      },
      error: (err: ApiError) => {
        this.error.set(err);
        this.loading.set(false);
      },
    });
  }

  reset(): void {
    this.response.set(null);
    this.selectedFile.set(null);
    this.error.set(null);
  }
}
