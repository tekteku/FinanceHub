export interface Account {
  id: number;
  name: string;
  description?: string;
  type: AccountType;
  currency: string;
  currentBalance: number;
  initialBalance: number;
  totalTransactions: number;
  isActive: boolean;
  createdAt: Date;
  updatedAt: Date;
}

export enum AccountType {
  CHECKING = 'CHECKING',
  SAVINGS = 'SAVINGS',
  INVESTMENT = 'INVESTMENT',
  CREDIT_CARD = 'CREDIT_CARD',
  LOAN = 'LOAN',
  CRYPTO = 'CRYPTO',
  BUSINESS = 'BUSINESS'
}

export interface CreateAccountRequest {
  name: string;
  description?: string;
  type: AccountType;
  currency: string;
  initialBalance: number;
}
