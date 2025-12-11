import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { InvestorProfile } from '../models/investor-profile.model';

@Injectable({
  providedIn: 'root'
})
export class InvestorProfileService {
  private apiUrl = `${environment.apiUrl}/investor-profiles`;

  constructor(private http: HttpClient) { }

  getMyProfile(): Observable<InvestorProfile> {
    return this.http.get<InvestorProfile>(`${this.apiUrl}/me`);
  }

  getInvestorProfile(userId: number): Observable<InvestorProfile> {
    return this.http.get<InvestorProfile>(`${this.apiUrl}/${userId}`);
  }
}
