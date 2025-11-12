import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Transaction } from '../../../../models/transaction.model';

@Component({
  selector: 'app-transaction-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './transaction-list.component.html',
  styleUrl: './transaction-list.component.scss'
})
export class TransactionListComponent {
  @Input() transactions: Transaction[] = [];
  @Input() loading: boolean = false;
  @Input() showAccount: boolean = true;
  @Input() maxItems: number = 5;

  get displayTransactions(): Transaction[] {
    return this.transactions.slice(0, this.maxItems);
  }

  getTransactionIcon(type: string): string {
    switch (type?.toUpperCase()) {
      case 'INCOME':
        return '💰';
      case 'EXPENSE':
        return '💸';
      case 'TRANSFER':
        return '🔄';
      default:
        return '💵';
    }
  }

  getTransactionClass(type: string): string {
    return type?.toLowerCase() || 'income';
  }

  formatDate(date: string | Date): string {
    if (!date) return '';
    const d = new Date(date);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    if (d.toDateString() === today.toDateString()) {
      return 'Today';
    } else if (d.toDateString() === yesterday.toDateString()) {
      return 'Yesterday';
    } else {
      return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    }
  }

  formatAmount(amount: number, type: string): string {
    const prefix = type === 'INCOME' ? '+' : type === 'EXPENSE' ? '-' : '';
    return `${prefix}$${Math.abs(amount).toFixed(2)}`;
  }
}
