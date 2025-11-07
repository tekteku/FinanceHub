export interface Budget {
  id: number;
  name: string;
  description?: string;
  amount: number;
  amountSpent: number;
  amountRemaining: number;
  percentageUsed: number;
  periodType: BudgetPeriodType;
  categoryId?: number;
  status: BudgetStatus;
  createdAt: Date;
  updatedAt: Date;
}

export enum BudgetPeriodType {
  MONTHLY = 'MONTHLY',
  QUARTERLY = 'QUARTERLY',
  YEARLY = 'YEARLY'
}

export enum BudgetStatus {
  ON_TRACK = 'ON_TRACK',
  WARNING = 'WARNING',
  OVER_BUDGET = 'OVER_BUDGET'
}

export interface CreateBudgetRequest {
  name: string;
  description?: string;
  amount: number;
  periodType: BudgetPeriodType;
  categoryId?: number;
}
