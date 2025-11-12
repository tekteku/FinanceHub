import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Transaction, CreateTransactionRequest, TransactionType } from '../models/transaction.model';
import { ApiResponse, PageResponse } from '../models/api-response.model';

/**
 * Service for managing transactions.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private apiUrl = `${environment.apiUrl}/transactions`;

  constructor(private http: HttpClient) {}

  getAllTransactions(page: number = 0, size: number = 20): Observable<PageResponse<Transaction>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'transactionDate,desc');
    
    return this.http.get<PageResponse<Transaction>>(this.apiUrl, { params });
  }

  getTransactionsByType(type: TransactionType, page: number = 0, size: number = 20): Observable<PageResponse<Transaction>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<PageResponse<Transaction>>(`${this.apiUrl}/type/${type}`, { params });
  }

  getTransactionsByAccount(accountId: number, page: number = 0, size: number = 20): Observable<PageResponse<Transaction>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<PageResponse<Transaction>>(`${this.apiUrl}/account/${accountId}`, { params });
  }

  getRecentTransactions(): Observable<ApiResponse<Transaction[]>> {
    return this.http.get<ApiResponse<Transaction[]>>(`${this.apiUrl}/recent`);
  }

  getTransactionById(id: number): Observable<ApiResponse<Transaction>> {
    return this.http.get<ApiResponse<Transaction>>(`${this.apiUrl}/${id}`);
  }

  createTransaction(request: CreateTransactionRequest): Observable<ApiResponse<Transaction>> {
    return this.http.post<ApiResponse<Transaction>>(this.apiUrl, request);
  }

  updateTransaction(id: number, request: CreateTransactionRequest): Observable<ApiResponse<Transaction>> {
    return this.http.put<ApiResponse<Transaction>>(`${this.apiUrl}/${id}`, request);
  }

  deleteTransaction(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }

  getTransactionsByDateRange(startDate: string, endDate: string): Observable<ApiResponse<Transaction[]>> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    
    return this.http.get<ApiResponse<Transaction[]>>(`${this.apiUrl}/range`, { params });
  }

  getTotalIncome(startDate: string, endDate: string): Observable<ApiResponse<number>> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    
    return this.http.get<ApiResponse<number>>(`${this.apiUrl}/income/total`, { params });
  }

  getTotalExpenses(startDate: string, endDate: string): Observable<ApiResponse<number>> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    
    return this.http.get<ApiResponse<number>>(`${this.apiUrl}/expenses/total`, { params });
  }
}
