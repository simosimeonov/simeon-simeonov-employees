import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'days', standalone: true })
export class DaysPipe implements PipeTransform {
  transform(value: number | null | undefined): string {
    if (value === null || value === undefined) {
      return '';
    }
    const n = Math.trunc(value);
    return n === 1 ? '1 day' : `${n} days`;
  }
}
