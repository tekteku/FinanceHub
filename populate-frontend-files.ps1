#################################################################################
# FinanceHub - Complete Frontend Files Generator
# Author: @tekteku
# Date: 2025-11-07 08:45:56 UTC
# Description: Populates ALL empty frontend folders with complete files
#################################################################################

$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "   🎨 FinanceHub - Frontend Files Generator" -ForegroundColor Cyan
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

$frontendPath = "D:\Projets\ProjetInterview\FinanceHub\frontend\src\app"

if (-not (Test-Path $frontendPath)) {
    Write-Host "❌ Frontend app folder not found!" -ForegroundColor Red
    exit 1
}

#################################################################################
# GUARDS
#################################################################################

Write-Host "🛡️  Creating guards..." -ForegroundColor Yellow

# guards/auth.guard.ts
@"
import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.authService.isAuthenticated()) {
      return true;
    }
    
    // Redirect to login with return url
    return this.router.createUrlTree(['/login'], {
      queryParams: { returnUrl: state.url }
    });
  }
}
"@ | Out-File -FilePath "$frontendPath\guards\auth.guard.ts" -Encoding UTF8

# guards/auth.guard.spec.ts
@"
import { TestBed } from '@angular/core/testing';
import { AuthGuard } from './auth.guard';

describe('AuthGuard', () => {
  let guard: AuthGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(AuthGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
"@ | Out-File -FilePath "$frontendPath\guards\auth.guard.spec.ts" -Encoding UTF8

Write-Host "   ✅ Guards created!" -ForegroundColor Green

#################################################################################
# INTERCEPTORS
#################################################################################

Write-Host "🔌 Creating interceptors..." -ForegroundColor Yellow

# interceptors/auth.interceptor.ts
@"
import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.authService.getToken();
    
    if (token && !request.url.includes('/auth/')) {
      request = request.clone({
        setHeaders: {
          Authorization: ``Bearer `${token}``
        }
      });
    }
    
    return next.handle(request);
  }
}
"@ | Out-File -FilePath "$frontendPath\interceptors\auth.interceptor.ts" -Encoding UTF8

# interceptors/error.interceptor.ts
@"
import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { NotificationService } from '../services/notification.service';
import { AuthService } from '../services/auth.service';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(
    private router: Router,
    private notificationService: NotificationService,
    private authService: AuthService
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'An unexpected error occurred';
        
        if (error.error instanceof ErrorEvent) {
          // Client-side error
          errorMessage = error.error.message;
        } else {
          // Server-side error
          switch (error.status) {
            case 401:
              errorMessage = 'Unauthorized - Please login again';
              this.authService.logout();
              break;
            case 403:
              errorMessage = 'Access denied';
              break;
            case 404:
              errorMessage = 'Resource not found';
              break;
            case 500:
              errorMessage = 'Internal server error';
              break;
            default:
              errorMessage = error.error?.message || errorMessage;
          }
        }
        
        // Don't show notification for auth endpoints on 401
        if (!(request.url.includes('/auth/') && error.status === 401)) {
          this.notificationService.showError(errorMessage);
        }
        
        return throwError(() => error);
      })
    );
  }
}
"@ | Out-File -FilePath "$frontendPath\interceptors\error.interceptor.ts" -Encoding UTF8

# interceptors/loading.interceptor.ts
@"
import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { ThemeService } from '../services/theme.service';

@Injectable()
export class LoadingInterceptor implements HttpInterceptor {
  private activeRequests = 0;
  
  constructor(private themeService: ThemeService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (this.activeRequests === 0) {
      this.themeService.setLoading(true);
    }
    this.activeRequests++;
    
    return next.handle(request).pipe(
      finalize(() => {
        this.activeRequests--;
        if (this.activeRequests === 0) {
          this.themeService.setLoading(false);
        }
      })
    );
  }
}
"@ | Out-File -FilePath "$frontendPath\interceptors\loading.interceptor.ts" -Encoding UTF8

Write-Host "   ✅ Interceptors created!" -ForegroundColor Green

#################################################################################
# MODELS
#################################################################################

Write-Host "📦 Creating models..." -ForegroundColor Yellow

# models/index.ts
@"
export * from './user.model';
export * from './account.model';
export * from './transaction.model';
export * from './category.model';
export * from './budget.model';
export * from './analytics.model';
export * from './api-response.model';
"@ | Out-File -FilePath "$frontendPath\models\index.ts" -Encoding UTF8

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
"@ | Out-File -FilePath "$frontendPath\models\user.model.ts" -Encoding UTF8

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
"@ | Out-File -FilePath "$frontendPath\models\account.model.ts" -Encoding UTF8

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
"@ | Out-File -FilePath "$frontendPath\models\transaction.model.ts" -Encoding UTF8

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
"@ | Out-File -FilePath "$frontendPath\models\category.model.ts" -Encoding UTF8

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
"@ | Out-File -FilePath "$frontendPath\models\budget.model.ts" -Encoding UTF8

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
"@ | Out-File -FilePath "$frontendPath\models\analytics.model.ts" -Encoding UTF8

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
"@ | Out-File -FilePath "$frontendPath\models\api-response.model.ts" -Encoding UTF8

Write-Host "   ✅ Models created!" -ForegroundColor Green

#################################################################################
# SERVICES
#################################################################################

Write-Host "🔧 Creating services..." -ForegroundColor Yellow

# Services will be created in the next message due to length...

Write-Host ""
Write-Host "✅ Part 1 Complete! Running Part 2..." -ForegroundColor Green
Write-Host ""
