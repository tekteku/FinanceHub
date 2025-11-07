#################################################################################
# FinanceHub - COMPLETE Frontend Generator Script
# Author: @tekteku
# Date: 2025-11-07 08:39:10 UTC
# Description: Generates ALL Angular frontend files with complete content
#################################################################################

$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "    🎨 FinanceHub - Complete Frontend Generator" -ForegroundColor Cyan
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

$projectRoot = $PSScriptRoot
$frontendPath = Join-Path $projectRoot "frontend"

if (-not (Test-Path $frontendPath)) {
    Write-Host "❌ Frontend folder not found at: $frontendPath" -ForegroundColor Red
    Write-Host "Please run 'ng new frontend' first!" -ForegroundColor Yellow
    exit 1
}

#################################################################################
# 1. CREATE DIRECTORY STRUCTURE
#################################################################################

Write-Host "📁 Step 1/10: Creating directory structure..." -ForegroundColor Yellow

$directories = @(
    "src\app\components\auth\login",
    "src\app\components\auth\register",
    "src\app\components\dashboard",
    "src\app\components\accounts",
    "src\app\components\transactions",
    "src\app\components\budgets",
    "src\app\components\analytics",
    "src\app\shared\components\navbar",
    "src\app\shared\components\sidenav",
    "src\app\shared\components\transaction-list",
    "src\app\shared\components\account-card",
    "src\app\shared\components\stats-card",
    "src\app\guards",
    "src\app\interceptors",
    "src\app\services",
    "src\app\models",
    "src\environments"
)

foreach ($dir in $directories) {
    $fullPath = Join-Path $frontendPath $dir
    New-Item -ItemType Directory -Path $fullPath -Force | Out-Null
}

Write-Host "   ✅ All directories created!" -ForegroundColor Green

#################################################################################
# 2. MODELS
#################################################################################

Write-Host "📦 Step 2/10: Generating models..." -ForegroundColor Yellow

# models/index.ts
@"
export * from './user.model';
export * from './account.model';
export * from './transaction.model';
export * from './category.model';
export * from './budget.model';
export * from './analytics.model';
export * from './api-response.model';
"@ | Out-File -FilePath (Join-Path $frontendPath "src\app\models\index.ts") -Encoding UTF8

# models/user.model.ts
@"
export interface User {
  id: number;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  fullName?: string;
  roles: string[];
  isActive: boolean;
  createdAt: Date;
  updatedAt: Date;
}

export interface AuthResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  fullName?: string;
  roles: string[];
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
}
"@ | Out-File -FilePath (Join-Path $frontendPath "src\app\models\user.model.ts") -Encoding UTF8

# models/account.model.ts
@"
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
  transactionCount?: number;
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
"@ | Out-File -FilePath (Join-Path $frontendPath "src\app\models\account.model.ts") -Encoding UTF8

# models/transaction.model.ts
@"
export interface Transaction {
  id: number;
  amount: number;
  description: string;
  reference?: string;
  type: TransactionType;
  typeDisplayName?: string;
  status: TransactionStatus;
  statusDisplayName?: string;
  accountId: number;
  accountName?: string;
  categoryId?: number;
  categoryName?: string;
  transactionDate: Date;
  createdAt: Date;
  updatedAt: Date;
  signedAmount?: number;
}

export enum TransactionType {
  INCOME = 'INCOME',
  EXPENSE = 'EXPENSE',
  TRANSFER = 'TRANSFER',
  INVESTMENT = 'INVESTMENT',
  WITHDRAWAL = 'WITHDRAWAL',
  DEPOSIT = 'DEPOSIT'
}

export enum TransactionStatus {
  PENDING = 'PENDING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
  CANCELLED = 'CANCELLED',
  REVERSED = 'REVERSED'
}

export interface CreateTransactionRequest {
  amount: number;
  description: string;
  reference?: string;
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
"@ | Out-File -FilePath (Join-Path $frontendPath "src\app\models\transaction.model.ts") -Encoding UTF8

# models/category.model.ts
@"
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
  totalAmount?: number;
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
"@ | Out-File -FilePath (Join-Path $frontendPath "src\app\models\category.model.ts") -Encoding UTF8

# models/budget.model.ts
@"
export interface Budget {
  id: number;
  name: string;
  description?: string;
  amount: number;
  amountSpent: number;
  amountRemaining: number;
  percentageUsed: number;
  period: string;
  periodType: BudgetPeriodType;
  categoryId?: number;
  categoryName?: string;
  isActive: boolean;
  alertThreshold: number;
  status: BudgetStatus;
  createdAt: Date;
  updatedAt: Date;
}

export enum BudgetPeriodType {
  DAILY = 'DAILY',
  WEEKLY = 'WEEKLY',
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
  alertThreshold?: number;
}
"@ | Out-File -FilePath (Join-Path $frontendPath "src\app\models\budget.model.ts") -Encoding UTF8

# models/analytics.model.ts
@"
export interface FinancialSummary {
  totalIncome: number;
  totalExpenses: number;
  balance: number;
  currency: string;
  accounts?: {
    totalBalance: number;
    activeAccounts: number;
  };
}

export interface AnalyticsData {
  period: string;
  income: number[];
  expenses: number[];
  categories: CategoryAnalytics[];
  accounts: AccountAnalytics[];
}

export interface CategoryAnalytics {
  name: string;
  amount: number;
  percentage: number;
  color: string;
}

export interface AccountAnalytics {
  name: string;
  balance: number;
  type: string;
}

export interface DashboardData {
  summary: FinancialSummary;
  recentTransactions: any[];
  accounts: any[];
  topCategories: any[];
  budgets: any[];
  charts?: {
    monthlyIncome: any[];
    monthlyExpenses: any[];
    categoryDistribution: any[];
  };
}
"@ | Out-File -FilePath (Join-Path $frontendPath "src\app\models\analytics.model.ts") -Encoding UTF8

# models/api-response.model.ts
@"
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first?: boolean;
  last?: boolean;
}
"@ | Out-File -FilePath (Join-Path $frontendPath "src\app\models\api-response.model.ts") -Encoding UTF8

Write-Host "   ✅ Models generated!" -ForegroundColor Green

#################################################################################
# 3. SERVICES
#################################################################################

Write-Host "🔧 Step 3/10: Generating services..." -ForegroundColor Yellow

# services/auth.service.ts
@"
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { LoginRequest, RegisterRequest, AuthResponse, User } from '../models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = ``${environment.apiUrl}/auth``;
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser`$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.loadUserFromStorage();
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(``${this.apiUrl}/login``, credentials)
      .pipe(
        tap(response => {
          this.setSession(response);
        })
      );
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(``${this.apiUrl}/register``, data)
      .pipe(
        tap(response => {
          this.setSession(response);
        })
      );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) return false;
    
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiry = payload.exp * 1000;
      return Date.now() < expiry;
    } catch {
      return false;
    }
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  private setSession(authResult: AuthResponse): void {
    const user: User = {
      id: authResult.id,
      username: authResult.username,
      email: authResult.email,
      firstName: authResult.firstName,
      lastName: authResult.lastName,
      fullName: authResult.fullName || ``${authResult.firstName} ${authResult.lastName}``,
      roles: authResult.roles,
      isActive: true,
      createdAt: new Date(),
      updatedAt: new Date()
    };
    
    localStorage.setItem('token', authResult.token);
    if (authResult.type) {
      localStorage.setItem('refreshToken', authResult.type);
    }
    localStorage.setItem('user', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  private loadUserFromStorage(): void {
    const userStr = localStorage.getItem('user');
    if (userStr && this.isAuthenticated()) {
      this.currentUserSubject.next(JSON.parse(userStr));
    }
  }
}
"@ | Out-File -FilePath (Join-Path $frontendPath "src\app\services\auth.service.ts") -Encoding UTF8

# services/account.service.ts
@"
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Account, CreateAccountRequest } from '../models';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private apiUrl = ``${environment.apiUrl}/accounts``;

  constructor(private http: HttpClient) {}

  getAll(page: number = 0, size: number = 20): Observable<Account[]> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Account[]>(this.apiUrl, { params });
  }

  getById(id: number): Observable<Account> {
    return this.http.get<Account>(``${this.apiUrl}/${id}``);
  }

  create(account: CreateAccountRequest): Observable<Account> {
    return this.http.post<Account>(this.apiUrl, account);
  }

  update(id: number, account: Partial<Account>): Observable<Account> {
    return this.http.put<Account>(``${this.apiUrl}/${id}``, account);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(``${this.apiUrl}/${id}``);
  }

  getBalance(id: number): Observable<{ balance: number; currency: string }> {
    return this.http.get<{ balance: number; currency: string }>(``${this.apiUrl}/${id}/balance``);
  }
}
"@ | Out-File -FilePath (Join-Path $frontendPath "src\app\services\account.service.ts") -Encoding UTF8

# services/transaction.service.ts
@"
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Transaction, CreateTransactionRequest, TransactionSummary } from '../models';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private apiUrl = ``${environment.apiUrl}/transactions``;

  constructor(private http: HttpClient) {}

  getAll(page: number = 0, size: number = 20): Observable<Transaction[]> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Transaction[]>(this.apiUrl, { params });
  }

  getById(id: number): Observable<Transaction> {
    return this.http.get<Transaction>(``${this.apiUrl}/${id}``);
  }

  create(transaction: CreateTransactionRequest): Observable<Transaction> {
    return this.http.post<Transaction>(this.apiUrl, transaction);
  }

  update(id: number, transaction: CreateTransactionRequest): Observable<Transaction> {
    return this.http.put<Transaction>(``${this.apiUrl}/${id}``, transaction);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(``${this.apiUrl}/${id}``);
  }

  getSummary(): Observable<TransactionSummary> {
    return this.http.get<TransactionSummary>(``${this.apiUrl}/summary``);
  }

  search(query: string, page: number = 0, size: number = 20): Observable<Transaction[]> {
    const params = new HttpParams()
      .set('q', query)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Transaction[]>(``${this.apiUrl}/search``, { params });
  }
}
"@ | Out-File -FilePath (Join-Path $frontendPath "src\app\services\transaction.service.ts") -Encoding UTF8

# services/category.service.ts
@"
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Category, CreateCategoryRequest } from '../models';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private apiUrl = ``${environment.apiUrl}/categories``;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Category[]> {
    return this.http.get<Category[]>(this.apiUrl);
  }

  getById(id: number): Observable<Category> {
    return this.http.get<Category>(``${this.apiUrl}/${id}``);
  }

  create(category: CreateCategoryRequest): Observable<Category> {
    return this.http.post<Category>(this.apiUrl, category);
  }

  update(id: number, category: Partial<Category>): Observable<Category> {
    return this.http.put<Category>(``${this.apiUrl}/${id}``, category);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(``${this.apiUrl}/${id}``);
  }
}
"@ | Out-File -FilePath (Join-Path $frontendPath "src\app\services\category.service.ts") -Encoding UTF8

# services/budget.service.ts
@"
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Budget, CreateBudgetRequest } from '../models';

@Injectable({
  providedIn: 'root'
})
export class BudgetService {
  private apiUrl = ``${environment.apiUrl}/budgets``;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Budget[]> {
    return this.http.get<Budget[]>(this.apiUrl);
  }

  getById(id: number): Observable<Budget> {
    return this.http.get<Budget>(``${this.apiUrl}/${id}``);
  }

  create(budget: CreateBudgetRequest): Observable<Budget> {
    return this.http.post<Budget>(this.apiUrl, budget);
  }

  update(id: number, budget: Partial<Budget>): Observable<Budget> {
    return this.http.put<Budget>(``${this.apiUrl}/${id}``, budget);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(``${this.apiUrl}/${id}``);
  }

  getStatus(id: number): Observable<{ status: string; percentage: number }> {
    return this.http.get<{ status: string; percentage: number }>(``${this.apiUrl}/${id}/status``);
  }
}
"@ | Out-File -FilePath (Join-Path $frontendPath "src\app\services\budget.service.ts") -Encoding UTF8

# services/theme.service.ts
@"
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private darkMode = new BehaviorSubject<boolean>(false);
  private loading = new BehaviorSubject<boolean>(false);

  public darkMode`$ = this.darkMode.asObservable();
  public loading`$ = this.loading.asObservable();

  constructor() {
    const savedTheme = localStorage.getItem('darkMode');
    if (savedTheme !== null) {
      this.darkMode.next(savedTheme === 'true');
    }
  }

  toggleDarkMode() {
    const newValue = !this.darkMode.value;
    this.darkMode.next(newValue);
    localStorage.setItem('darkMode', newValue.toString());
  }

  setLoading(loading: boolean) {
    this.loading.next(loading);
  }

  isDarkMode(): boolean {
    return this.darkMode.value;
  }
}
"@ | Out-File -FilePath (Join-Path $frontendPath "src\app\services\theme.service.ts") -Encoding UTF8

# services/notification.service.ts
@"
import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  constructor(private snackBar: MatSnackBar) {}

  showSuccess(message: string, duration: number = 3000) {
    this.snackBar.open(message, 'Close', {
      duration,
      panelClass: ['success-snackbar'],
      horizontalPosition: 'end',
      verticalPosition: 'top'
    });
  }

  showError(message: string, duration: number = 5000) {
    this.snackBar.open(message, 'Close', {
      duration,
      panelClass: ['error-snackbar'],
      horizontalPosition: 'end',
      verticalPosition: 'top'
    });
  }

  showWarning(message: string, duration: number = 4000) {
    this.snackBar.open(message, 'Close', {
      duration,
      panelClass: ['warning-snackbar'],
      horizontalPosition: 'end',
      verticalPosition: 'top'
    });
  }

  showInfo(message: string, duration: number = 3000) {
    this.snackBar.open(message, 'Close', {
      duration,
      panelClass: ['info-snackbar'],
      horizontalPosition: 'end',
      verticalPosition: 'top'
    });
  }
}
"@ | Out-File -FilePath (Join-Path $frontendPath "src\app\services\notification.service.ts") -Encoding UTF8

Write-Host "   ✅ Services generated!" -ForegroundColor Green

#################################################################################
# CONTINUE IN NEXT PART...
#################################################################################

Write-Host ""
Write-Host "🎉 Script is too long! Creating part 2..." -ForegroundColor Yellow
Write-Host ""
