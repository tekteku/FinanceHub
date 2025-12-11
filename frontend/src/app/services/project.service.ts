import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Project } from '../models/project.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = `${environment.apiUrl}/projects`;

  constructor(private http: HttpClient) { }

  getAllActiveProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(this.apiUrl);
  }

  getMyProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.apiUrl}/my-projects`);
  }

  createProject(project: Project): Observable<Project> {
    return this.http.post<Project>(this.apiUrl, project);
  }

  getProjectById(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/${id}`);
  }

  getTrendingProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.apiUrl}/trending`);
  }

  searchProjects(keyword: string): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.apiUrl}/search`, { params: { keyword } });
  }
}
