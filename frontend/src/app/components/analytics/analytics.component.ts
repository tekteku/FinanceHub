import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AnalyticsService } from '../../services/analytics.service';
import { TransactionService } from '../../services/transaction.service';
import { FinancialSummary } from '../../models/analytics.model';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.scss']
})
export class AnalyticsComponent implements OnInit {
  isLoading = false;
  error: string | null = null;
  dateRangeForm!: FormGroup;
  
  summary: FinancialSummary | null = null;
  expensesByCategory: any[] = [];
  monthlyTrends: any[] = [];
  cashFlow: any = null;

  // Default date range: last 30 days
  startDate: string;
  endDate: string;

  constructor(
    private analyticsService: AnalyticsService,
    private transactionService: TransactionService,
    private fb: FormBuilder
  ) {
    const today = new Date();
    this.endDate = today.toISOString().split('T')[0];
    
    const thirtyDaysAgo = new Date();
    thirtyDaysAgo.setDate(today.getDate() - 30);
    this.startDate = thirtyDaysAgo.toISOString().split('T')[0];
  }

  ngOnInit(): void {
    this.initForm();
    this.loadAnalytics();
  }

  initForm(): void {
    this.dateRangeForm = this.fb.group({
      startDate: [this.startDate],
      endDate: [this.endDate]
    });
  }

  loadAnalytics(): void {
    this.isLoading = true;
    this.error = null;

    const { startDate, endDate } = this.dateRangeForm.value;

    // Load financial summary
    this.analyticsService.getFinancialSummary(startDate, endDate).subscribe({
      next: (response) => {
        this.summary = response.data;
      },
      error: (err) => {
        console.error('Error loading summary:', err);
      }
    });

    // Load expenses by category
    this.analyticsService.getExpensesByCategory(startDate, endDate).subscribe({
      next: (response) => {
        this.expensesByCategory = response.data;
      },
      error: (err) => {
        console.error('Error loading expenses by category:', err);
      }
    });

    // Load monthly trends
    this.analyticsService.getMonthlyTrends(12).subscribe({
      next: (response) => {
        this.monthlyTrends = response.data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading monthly trends:', err);
        this.isLoading = false;
      }
    });

    // Load cash flow
    this.analyticsService.getCashFlow(startDate, endDate).subscribe({
      next: (response) => {
        this.cashFlow = response.data;
      },
      error: (err) => {
        console.error('Error loading cash flow:', err);
      }
    });
  }

  onDateRangeChange(): void {
    const { startDate, endDate } = this.dateRangeForm.value;
    if (startDate && endDate) {
      this.loadAnalytics();
    }
  }

  setQuickRange(days: number): void {
    const today = new Date();
    const startDate = new Date();
    startDate.setDate(today.getDate() - days);

    this.dateRangeForm.patchValue({
      startDate: startDate.toISOString().split('T')[0],
      endDate: today.toISOString().split('T')[0]
    });

    this.loadAnalytics();
  }

  getCategoryColor(index: number): string {
    const colors = [
      '#667eea', '#764ba2', '#f093fb', '#4facfe',
      '#43e97b', '#fa709a', '#fee140', '#30cfd0',
      '#a8edea', '#fed6e3', '#c471f5', '#fa709a'
    ];
    return colors[index % colors.length];
  }

  getTotalExpensesByCategory(): number {
    return this.expensesByCategory.reduce((sum, cat) => sum + (cat.amount || 0), 0);
  }

  getCategoryPercentage(amount: number): number {
    const total = this.getTotalExpensesByCategory();
    return total > 0 ? (amount / total) * 100 : 0;
  }

  getMaxTrendValue(): number {
    if (this.monthlyTrends.length === 0) return 1;
    
    const maxIncome = Math.max(...this.monthlyTrends.map(t => t.income || 0));
    const maxExpenses = Math.max(...this.monthlyTrends.map(t => t.expenses || 0));
    
    return Math.max(maxIncome, maxExpenses) || 1;
  }
}
