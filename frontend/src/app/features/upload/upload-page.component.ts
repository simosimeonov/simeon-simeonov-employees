import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { FilePickerComponent } from './components/file-picker/file-picker.component';
import { EmployeesBreakdownTableComponent } from './components/employees-breakdown-table/employees-breakdown-table.component';
import { InvalidRowsPanelComponent } from './components/invalid-rows-panel/invalid-rows-panel.component';
import { StatsBarComponent } from './components/stats-bar/stats-bar.component';
import { UploadStore } from './state/upload.store';

@Component({
    selector: 'app-upload-page',
    imports: [
        MatProgressBarModule,
        MatButtonModule,
        MatCardModule,
        MatIconModule,
        FilePickerComponent,
        EmployeesBreakdownTableComponent,
        InvalidRowsPanelComponent,
        StatsBarComponent,
    ],
    providers: [UploadStore],
    templateUrl: './upload-page.component.html',
    styleUrl: './upload-page.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class UploadPageComponent {
  protected readonly store = inject(UploadStore);

  onFileSelected(file: File | null): void {
    this.store.selectFile(file);
  }

  onSubmit(): void {
    this.store.upload();
  }

  onReset(): void {
    this.store.reset();
  }
}
