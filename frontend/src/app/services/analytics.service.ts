import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { FinancialSummary, DashboardData } from '../models/analytics.model';

/**
 * Service for fetching analytics and dashboard data.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-10
 */
@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private apiUrl = `${environment.apiUrl}/analytics`;

  constructor(private http: HttpClient) { }

  getDashboardData(): Observable<ApiResponse<DashboardData>> {
    return this.http.get<ApiResponse<DashboardData>>(`${this.apiUrl}/dashboard`);
  }

  getFinancialSummary(startDate: string, endDate: string): Observable<ApiResponse<FinancialSummary>> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    
    return this.http.get<ApiResponse<FinancialSummary>>(`${this.apiUrl}/summary`, { params });
  }

  getExpensesByCategory(startDate: string, endDate: string): Observable<ApiResponse<any[]>> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    
    return this.http.get<ApiResponse<any[]>>(`${this.apiUrl}/expenses/by-category`, { params });
  }

  getMonthlyTrends(startDate: string, endDate: string): Observable<ApiResponse<any[]>> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    
    return this.http.get<ApiResponse<any[]>>(`${this.apiUrl}/trends/monthly`, { params });
  }

  getCashFlow(startDate: string, endDate: string): Observable<ApiResponse<any>> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/cashflow`, { params });
  }
}
