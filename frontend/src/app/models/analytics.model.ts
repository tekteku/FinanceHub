export interface FinancialSummary {
  totalIncome: number;
  totalExpenses: number;
  balance: number;
  currency: string;
}

export interface DashboardData {
  summary: FinancialSummary;
  recentTransactions: any[];
  accounts: any[];
  budgets: any[];
}
