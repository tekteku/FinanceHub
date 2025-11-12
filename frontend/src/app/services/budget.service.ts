import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Budget, CreateBudgetRequest } from '../models/budget.model';
import { ApiResponse } from '../models/api-response.model';

/**
 * Service for managing budgets.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-10
 */
@Injectable({
  providedIn: 'root'
})
export class BudgetService {
  private apiUrl = `${environment.apiUrl}/budgets`;

  constructor(private http: HttpClient) { }

  getAllBudgets(): Observable<ApiResponse<Budget[]>> {
    return this.http.get<ApiResponse<Budget[]>>(this.apiUrl);
  }

  getActiveBudgets(): Observable<ApiResponse<Budget[]>> {
    return this.http.get<ApiResponse<Budget[]>>(`${this.apiUrl}/active`);
  }

  getBudgetById(id: number): Observable<ApiResponse<Budget>> {
    return this.http.get<ApiResponse<Budget>>(`${this.apiUrl}/${id}`);
  }

  createBudget(budget: CreateBudgetRequest): Observable<ApiResponse<Budget>> {
    return this.http.post<ApiResponse<Budget>>(this.apiUrl, budget);
  }

  updateBudget(id: number, budget: CreateBudgetRequest): Observable<ApiResponse<Budget>> {
    return this.http.put<ApiResponse<Budget>>(`${this.apiUrl}/${id}`, budget);
  }

  deleteBudget(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }

  getBudgetsByCategory(categoryId: number): Observable<ApiResponse<Budget[]>> {
    return this.http.get<ApiResponse<Budget[]>>(`${this.apiUrl}/category/${categoryId}`);
  }
}
