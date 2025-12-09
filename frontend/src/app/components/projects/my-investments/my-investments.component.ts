import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Investment } from '../../../models/investment.model';
import { InvestmentService } from '../../../services/investment.service';

@Component({
  selector: 'app-my-investments',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './my-investments.component.html',
  styleUrls: ['./my-investments.component.scss']
})
export class MyInvestmentsComponent implements OnInit {
  investments: Investment[] = [];

  constructor(private investmentService: InvestmentService) { }

  ngOnInit(): void {
    this.loadInvestments();
  }

  loadInvestments(): void {
    this.investmentService.getMyInvestments().subscribe({
      next: (data) => {
        this.investments = data;
      },
      error: (error) => {
        console.error('Error loading investments', error);
      }
    });
  }
}
