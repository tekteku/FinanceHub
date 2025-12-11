import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Observable } from 'rxjs';
import { InvestorProfile } from '../../../models/investor-profile.model';
import { InvestorProfileService } from '../../../services/investor-profile.service';

@Component({
  selector: 'app-investor-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './investor-profile.component.html',
  styleUrls: ['./investor-profile.component.scss']
})
export class InvestorProfileComponent implements OnInit {
  profile$!: Observable<InvestorProfile>;
  loading = true;

  constructor(private profileService: InvestorProfileService) { }

  ngOnInit(): void {
    this.profile$ = this.profileService.getMyProfile();
    this.profile$.subscribe({
      next: () => {
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}
