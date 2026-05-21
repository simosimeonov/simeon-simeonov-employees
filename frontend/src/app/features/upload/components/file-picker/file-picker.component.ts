import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

@Component({
    selector: 'app-file-picker',
    imports: [DecimalPipe, MatButtonModule, MatCardModule, MatIconModule],
    templateUrl: './file-picker.component.html',
    styleUrl: './file-picker.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class FilePickerComponent {
  readonly file = input<File | null>(null);
  readonly disabled = input(false);

  readonly fileSelected = output<File | null>();
  readonly submitted = output<void>();

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const selected = input.files?.[0] ?? null;
    this.fileSelected.emit(selected);
    input.value = '';
  }

  triggerSubmit(): void {
    this.submitted.emit();
  }
}
