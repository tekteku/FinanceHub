import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Project } from '../../../models/project.model';
import { ProjectService } from '../../../services/project.service';

@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './project-list.component.html',
  styleUrls: ['./project-list.component.scss']
})
export class ProjectListComponent implements OnInit {
  projects: Project[] = [];
  trendingProjects: Project[] = [];
  searchKeyword: string = '';
  loading = true;
  showTrending = true;

  constructor(private projectService: ProjectService) { }

  ngOnInit(): void {
    this.loadProjects();
    this.loadTrendingProjects();
  }

  loadProjects(): void {
    this.projectService.getAllActiveProjects().subscribe({
      next: (data) => {
        this.projects = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading projects', error);
        this.loading = false;
      }
    });
  }

  loadTrendingProjects(): void {
    this.projectService.getTrendingProjects().subscribe({
      next: (data) => {
        this.trendingProjects = data;
      },
      error: (error) => {
        console.error('Error loading trending projects', error);
      }
    });
  }

  searchProjects(): void {
    if (this.searchKeyword.trim()) {
      this.projectService.searchProjects(this.searchKeyword).subscribe({
        next: (data) => {
          this.projects = data;
          this.showTrending = false;
        },
        error: (error) => {
          console.error('Error searching projects', error);
        }
      });
    } else {
      this.loadProjects();
      this.showTrending = true;
    }
  }
}
