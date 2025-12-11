import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ProjectReview } from '../models/project-review.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectReviewService {
  private apiUrl = `${environment.apiUrl}/reviews`;

  constructor(private http: HttpClient) { }

  createReview(projectId: number, review: ProjectReview): Observable<ProjectReview> {
    return this.http.post<ProjectReview>(`${this.apiUrl}/projects/${projectId}`, review);
  }

  getProjectReviews(projectId: number): Observable<ProjectReview[]> {
    return this.http.get<ProjectReview[]>(`${this.apiUrl}/projects/${projectId}`);
  }

  getAverageRating(projectId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/projects/${projectId}/rating`);
  }
}
