import { Directive, EventEmitter, Input, Output } from '@angular/core';

export type SortDirection = 'asc' | 'desc' | '';

@Directive({
  selector: '[appKpiSortable]'
})
export class KpiSortableDirective {
  @Input() appKpiSortable: string;
  @Output() sort = new EventEmitter<SortEvent>();
  direction: SortDirection = '';

  constructor() { }

  onClick() {
    this.direction = this.direction === 'asc' ? 'desc' : 'asc';
    this.sort.emit({ column: this.appKpiSortable, direction: this.direction });
  }
}

export interface SortEvent {
  column: string;
  direction: SortDirection;
}
