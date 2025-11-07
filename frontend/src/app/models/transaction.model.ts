export interface Transaction {
  id: number;
  amount: number;
  description: string;
  reference?: string;
  type: TransactionType;
  status: TransactionStatus;
  accountId: number;
  accountName?: string;
  categoryId?: number;
  categoryName?: string;
  transactionDate: Date;
  createdAt: Date;
  updatedAt: Date;
}

export enum TransactionType {
  INCOME = 'INCOME',
  EXPENSE = 'EXPENSE',
  TRANSFER = 'TRANSFER'
}

export enum TransactionStatus {
  PENDING = 'PENDING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED'
}

export interface CreateTransactionRequest {
  amount: number;
  description: string;
  type: TransactionType;
  accountId: number;
  categoryId?: number;
  transactionDate?: Date;
}

export interface TransactionSummary {
  totalIncome: number;
  totalExpenses: number;
  balance: number;
  currency: string;
}
