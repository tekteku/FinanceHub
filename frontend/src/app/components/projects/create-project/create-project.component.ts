import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { Project } from '../../../models/project.model';
import { ProjectService } from '../../../services/project.service';

@Component({
  selector: 'app-create-project',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './create-project.component.html',
  styleUrls: ['./create-project.component.scss']
})
export class CreateProjectComponent {
  project: Project = {
    title: '',
    description: '',
    targetAmount: 0
  };

  constructor(
    private projectService: ProjectService,
    private router: Router
  ) { }

  onSubmit(): void {
    this.projectService.createProject(this.project).subscribe({
      next: (data) => {
        console.log('Project created', data);
        this.router.navigate(['/projects']);
      },
      error: (error) => {
        console.error('Error creating project', error);
        alert('Failed to create project. Please try again.');
      }
    });
  }
}
