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
  loading = true;

  constructor(private projectService: ProjectService) { }

  ngOnInit(): void {
    this.loadProjects();
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
}
