import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { PairResult } from '../../../../core/models/pair-result.model';

export interface BreakdownRow {
  employee1: number;
  employee2: number;
  projectId: number;
  dateFrom: string;
  dateTo: string;
  days: number;
}

@Component({
    selector: 'app-employees-breakdown-table',
    imports: [MatCardModule, MatTableModule],
    templateUrl: './employees-breakdown-table.component.html',
    styleUrl: './employees-breakdown-table.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EmployeesBreakdownTableComponent {
  readonly pairs = input<PairResult[]>([]);

  readonly rows = computed<BreakdownRow[]>(() =>
    this.pairs().flatMap((p) =>
      p.breakdown.map<BreakdownRow>((b) => ({
        employee1: p.employee1,
        employee2: p.employee2,
        projectId: b.projectId,
        dateFrom: b.dateFrom,
        dateTo: b.dateTo,
        days: b.days,
      })),
    ),
  );

  protected readonly columns = ['employee1', 'employee2', 'projectId', 'period', 'days'] as const;
}
