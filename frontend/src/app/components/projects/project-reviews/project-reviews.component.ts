import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProjectReview } from '../../../models/project-review.model';
import { ProjectReviewService } from '../../../services/project-review.service';

@Component({
  selector: 'app-project-reviews',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './project-reviews.component.html',
  styleUrls: ['./project-reviews.component.scss']
})
export class ProjectReviewsComponent implements OnInit {
  @Input() projectId!: number;
  
  reviews: ProjectReview[] = [];
  newReview: ProjectReview = {
    projectId: 0,
    rating: 0,
    comment: ''
  };
  averageRating = 0;

  constructor(private reviewService: ProjectReviewService) { }

  ngOnInit(): void {
    if (this.projectId) {
      this.loadReviews();
      this.loadAverageRating();
    }
  }

  loadReviews(): void {
    this.reviewService.getProjectReviews(this.projectId).subscribe({
      next: (data) => {
        this.reviews = data;
      },
      error: (error) => {
        console.error('Error loading reviews', error);
      }
    });
  }

  loadAverageRating(): void {
    this.reviewService.getAverageRating(this.projectId).subscribe({
      next: (rating) => {
        this.averageRating = rating || 0;
      },
      error: (error) => {
        console.error('Error loading average rating', error);
      }
    });
  }

  setRating(rating: number): void {
    this.newReview.rating = rating;
  }

  onAddReview(): void {
    if (this.newReview.rating && this.newReview.comment) {
      this.newReview.projectId = this.projectId;
      this.reviewService.createReview(this.projectId, this.newReview).subscribe({
        next: (review) => {
          this.reviews.unshift(review);
          this.newReview = { projectId: this.projectId, rating: 0, comment: '' };
          this.loadAverageRating();
        },
        error: (error) => {
          console.error('Error creating review', error);
          alert('Failed to submit review');
        }
      });
    }
  }
}
