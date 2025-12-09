import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Investment } from '../models/investment.model';

@Injectable({
  providedIn: 'root'
})
export class InvestmentService {
  private apiUrl = `${environment.apiUrl}/investments`;

  constructor(private http: HttpClient) { }

  invest(projectId: number, amount: number, accountId: number): Observable<Investment> {
    return this.http.post<Investment>(this.apiUrl, { projectId, amount, accountId });
  }

  getMyInvestments(): Observable<Investment[]> {
    return this.http.get<Investment[]>(`${this.apiUrl}/my-investments`);
  }

  getTotalInvested(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/total-invested`);
  }
}
