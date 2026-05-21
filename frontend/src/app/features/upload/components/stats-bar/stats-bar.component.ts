import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { Stats } from '../../../../core/models/stats.model';

@Component({
    selector: 'app-stats-bar',
    imports: [MatCardModule],
    templateUrl: './stats-bar.component.html',
    styleUrl: './stats-bar.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class StatsBarComponent {
  readonly stats = input.required<Stats>();
}
