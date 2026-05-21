import { InvalidRow } from './invalid-row.model';
import { PairResult } from './pair-result.model';
import { Stats } from './stats.model';

export interface UploadResponse {
  topPairs: PairResult[];
  invalidRows: InvalidRow[];
  stats: Stats;
}
