import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Project } from '../../../models/project.model';
import { Account } from '../../../models/account.model';
import { ProjectService } from '../../../services/project.service';
import { InvestmentService } from '../../../services/investment.service';
import { AccountService } from '../../../services/account.service';

@Component({
  selector: 'app-project-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './project-detail.component.html',
  styleUrls: ['./project-detail.component.scss']
})
export class ProjectDetailComponent implements OnInit {
  project: Project | null = null;
  accounts: Account[] = [];
  selectedAccountId: number | null = null;
  investmentAmount: number = 0;
  isInvested = false;

  constructor(
    private route: ActivatedRoute,
    private projectService: ProjectService,
    private investmentService: InvestmentService,
    private accountService: AccountService
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadProject(+id);
    }
    this.loadAccounts();
  }

  loadProject(id: number): void {
    this.projectService.getProjectById(id).subscribe({
      next: (data) => {
        this.project = data;
      },
      error: (error) => {
        console.error('Error loading project', error);
      }
    });
  }

  loadAccounts(): void {
    this.accountService.getActiveAccounts().subscribe({
      next: (response) => {
        this.accounts = response.data;
        if (this.accounts.length > 0) {
          this.selectedAccountId = this.accounts[0].id;
        }
      },
      error: (error) => {
        console.error('Error loading accounts', error);
      }
    });
  }

  invest(): void {
    if (this.project && this.project.id && this.investmentAmount > 0 && this.selectedAccountId) {
      this.investmentService.invest(this.project.id, this.investmentAmount, this.selectedAccountId).subscribe({
        next: (data) => {
          this.isInvested = true;
          this.loadProject(this.project!.id!); // Reload to update progress
          this.investmentAmount = 0;
        },
        error: (error) => {
          console.error('Error investing', error);
          alert('Investment failed. Please check your account balance and try again.');
        }
      });
    }
  }
}
