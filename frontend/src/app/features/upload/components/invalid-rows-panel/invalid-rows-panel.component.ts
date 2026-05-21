import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatTableModule } from '@angular/material/table';
import { InvalidRow } from '../../../../core/models/invalid-row.model';

@Component({
    selector: 'app-invalid-rows-panel',
    imports: [MatExpansionModule, MatTableModule],
    templateUrl: './invalid-rows-panel.component.html',
    styleUrl: './invalid-rows-panel.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class InvalidRowsPanelComponent {
  readonly rows = input<InvalidRow[]>([]);

  protected readonly columns = ['line', 'reason', 'raw'] as const;
}
