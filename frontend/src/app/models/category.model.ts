export interface Category {
  id: number;
  name: string;
  description?: string;
  type: CategoryType;
  color?: string;
  icon?: string;
  isActive: boolean;
  createdAt: Date;
  updatedAt: Date;
}

export enum CategoryType {
  INCOME = 'INCOME',
  EXPENSE = 'EXPENSE',
  TRANSFER = 'TRANSFER'
}

export interface CreateCategoryRequest {
  name: string;
  description?: string;
  type: CategoryType;
  color?: string;
  icon?: string;
}
