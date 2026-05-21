import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UploadPageComponent } from './features/upload/upload-page.component';

@Component({
    selector: 'app-root',
    imports: [UploadPageComponent],
    template: `<app-upload-page />`,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent {}
