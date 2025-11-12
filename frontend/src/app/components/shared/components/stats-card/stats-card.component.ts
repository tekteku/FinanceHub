import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-stats-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './stats-card.component.html',
  styleUrl: './stats-card.component.scss'
})
export class StatsCardComponent {
  @Input() icon: string = '📊';
  @Input() label: string = '';
  @Input() value: string | number = '0';
  @Input() trend: 'up' | 'down' | 'neutral' = 'neutral';
  @Input() trendValue: string = '';
  @Input() color: 'primary' | 'success' | 'danger' | 'info' | 'warning' = 'primary';
  @Input() loading: boolean = false;
}
