import { HttpErrorResponse } from '@angular/common/http';

export interface ApiError {
  status: number;
  error: string;
  traceId?: string;
}

interface BackendErrorBody {
  error?: string;
  traceId?: string;
}

export function toApiError(response: HttpErrorResponse): ApiError {
  const body = (response.error ?? {}) as BackendErrorBody;
  const message = body.error ?? response.message ?? defaultMessage(response.status);
  return {
    status: response.status,
    error: message,
    traceId: body.traceId,
  };
}

function defaultMessage(status: number): string {
  switch (status) {
    case 0:
      return 'Could not reach server';
    case 400:
      return 'Bad request';
    case 413:
      return 'File too large';
    case 500:
      return 'Server error';
    default:
      return `HTTP ${status}`;
  }
}
