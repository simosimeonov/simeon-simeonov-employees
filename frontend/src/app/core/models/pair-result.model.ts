export interface PairProjectBreakdown {
  projectId: number;
  dateFrom: string;
  dateTo: string;
  days: number;
}

export interface PairResult {
  employee1: number;
  employee2: number;
  totalDays: number;
  breakdown: PairProjectBreakdown[];
}
