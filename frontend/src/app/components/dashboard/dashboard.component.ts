import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AccountService } from '../../services/account.service';
import { TransactionService } from '../../services/transaction.service';
import { BudgetService } from '../../services/budget.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  currentUser: any = null;
  isLoading = true;
  
  // Statistics
  totalBalance = 0;
  totalIncome = 0;
  totalExpenses = 0;
  accountsCount = 0;
  transactionsCount = 0;
  budgetsCount = 0;
  
  // Recent data
  recentTransactions: any[] = [];
  accounts: any[] = [];
  activeBudgets: any[] = [];
  
  // Chart data
  monthlyData: any[] = [];

  constructor(
    private authService: AuthService,
    private accountService: AccountService,
    private transactionService: TransactionService,
    private budgetService: BudgetService,
    private router: Router
  ) {}

  ngOnInit() {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
    
    this.loadDashboardData();
  }

  loadDashboardData() {
    this.isLoading = true;
    
    // Load accounts
    this.accountService.getAllAccounts().subscribe({
      next: (response) => {
        this.accounts = response.data;
        this.accountsCount = this.accounts.length;
        this.totalBalance = this.accounts.reduce((sum, acc) => sum + acc.currentBalance, 0);
      },
      error: (err) => console.error('Error loading accounts:', err)
    });
    
    // Load recent transactions
    this.transactionService.getRecentTransactions().subscribe({
      next: (response) => {
        this.recentTransactions = response.data.slice(0, 5);
      },
      error: (err) => console.error('Error loading transactions:', err)
    });
    
    // Load budgets
    this.budgetService.getActiveBudgets().subscribe({
      next: (response) => {
        this.activeBudgets = response.data.slice(0, 3);
        this.budgetsCount = response.data.length;
      },
      error: (err) => console.error('Error loading budgets:', err)
    });
    
    // Load income/expenses for current month
    const startDate = new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().split('T')[0];
    const endDate = new Date().toISOString().split('T')[0];
    
    this.transactionService.getTotalIncome(startDate, endDate).subscribe({
      next: (response) => {
        this.totalIncome = response.data;
      },
      error: (err) => console.error('Error loading income:', err)
    });
    
    this.transactionService.getTotalExpenses(startDate, endDate).subscribe({
      next: (response) => {
        this.totalExpenses = response.data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading expenses:', err);
        this.isLoading = false;
      }
    });
  }

  getTransactionIcon(type: string): string {
    const icons: any = {
      'INCOME': '💰',
      'EXPENSE': '💸',
      'TRANSFER': '🔄'
    };
    return icons[type] || '💳';
  }

  getTransactionClass(type: string): string {
    return type.toLowerCase();
  }

  getBudgetStatusIcon(status: string): string {
    const icons: any = {
      'ON_TRACK': '✅',
      'WARNING': '⚠️',
      'OVER_BUDGET': '❌'
    };
    return icons[status] || '📊';
  }

  navigateTo(path: string) {
    this.router.navigate([path]);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
