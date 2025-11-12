import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Account } from '../../../../models/account.model';

@Component({
  selector: 'app-account-card',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule, MatButtonModule],
  templateUrl: './account-card.component.html',
  styleUrls: ['./account-card.component.scss']
})
export class AccountCardComponent implements OnInit {
  @Input() account!: Account;
  
  constructor() { }

  ngOnInit(): void {
    // Component initialization logic
  }

  getAccountIcon(): string {
    switch (this.account?.type?.toLowerCase()) {
      case 'checking':
        return 'account_balance';
      case 'savings':
        return 'savings';
      case 'credit_card':
        return 'credit_card';
      case 'investment':
        return 'trending_up';
      default:
        return 'account_balance_wallet';
    }
  }

  getBalanceClass(): string {
    return this.account?.balance >= 0 ? 'positive' : 'negative';
  }
}
