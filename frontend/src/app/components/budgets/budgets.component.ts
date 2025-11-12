import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BudgetService } from '../../services/budget.service';
import { CategoryService } from '../../services/category.service';
import { Budget, BudgetPeriodType, BudgetStatus, CreateBudgetRequest } from '../../models/budget.model';
import { Category } from '../../models/category.model';

@Component({
  selector: 'app-budgets',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './budgets.component.html',
  styleUrls: ['./budgets.component.scss']
})
export class BudgetsComponent implements OnInit {
  budgets: Budget[] = [];
  categories: Category[] = [];
  isLoading = false;
  error: string | null = null;
  showAddForm = false;
  budgetForm!: FormGroup;
  periodTypes = Object.values(BudgetPeriodType);
  editingBudgetId: number | null = null;

  constructor(
    private budgetService: BudgetService,
    private categoryService: CategoryService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadCategories();
    this.loadBudgets();
  }

  initForm(): void {
    this.budgetForm = this.fb.group({
      name: ['', [Validators.required]],
      description: [''],
      amount: [0, [Validators.required, Validators.min(0.01)]],
      periodType: [BudgetPeriodType.MONTHLY, [Validators.required]],
      categoryId: [null]
    });
  }

  loadCategories(): void {
    this.categoryService.getAllCategories().subscribe({
      next: (response) => {
        this.categories = response.data;
      },
      error: (err) => {
        console.error('Error loading categories:', err);
      }
    });
  }

  loadBudgets(): void {
    this.isLoading = true;
    this.error = null;

    this.budgetService.getAllBudgets().subscribe({
      next: (response) => {
        this.budgets = response.data;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to load budgets';
        this.isLoading = false;
        console.error('Error loading budgets:', err);
      }
    });
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) {
      this.budgetForm.reset({
        amount: 0,
        periodType: BudgetPeriodType.MONTHLY
      });
      this.editingBudgetId = null;
    }
  }

  onSubmit(): void {
    if (this.budgetForm.invalid) {
      return;
    }

    const budgetData: CreateBudgetRequest = this.budgetForm.value;

    if (this.editingBudgetId) {
      this.updateBudget(this.editingBudgetId, budgetData);
    } else {
      this.createBudget(budgetData);
    }
  }

  createBudget(budgetData: CreateBudgetRequest): void {
    this.isLoading = true;
    this.budgetService.createBudget(budgetData).subscribe({
      next: (response) => {
        this.budgets.push(response.data);
        this.toggleAddForm();
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to create budget';
        this.isLoading = false;
        console.error('Error creating budget:', err);
      }
    });
  }

  updateBudget(id: number, budgetData: CreateBudgetRequest): void {
    this.isLoading = true;
    this.budgetService.updateBudget(id, budgetData).subscribe({
      next: (response) => {
        const index = this.budgets.findIndex(b => b.id === id);
        if (index !== -1) {
          this.budgets[index] = response.data;
        }
        this.toggleAddForm();
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to update budget';
        this.isLoading = false;
        console.error('Error updating budget:', err);
      }
    });
  }

  editBudget(budget: Budget): void {
    this.editingBudgetId = budget.id;
    this.budgetForm.patchValue({
      name: budget.name,
      description: budget.description || '',
      amount: budget.amount,
      periodType: budget.periodType,
      categoryId: budget.categoryId
    });
    this.showAddForm = true;
  }

  deleteBudget(id: number): void {
    if (!confirm('Are you sure you want to delete this budget?')) {
      return;
    }

    this.budgetService.deleteBudget(id).subscribe({
      next: () => {
        this.budgets = this.budgets.filter(b => b.id !== id);
      },
      error: (err) => {
        this.error = 'Failed to delete budget';
        console.error('Error deleting budget:', err);
      }
    });
  }

  getBudgetStatusClass(status: BudgetStatus): string {
    return status.toLowerCase().replace('_', '-');
  }

  getBudgetStatusIcon(status: BudgetStatus): string {
    const icons = {
      [BudgetStatus.ON_TRACK]: '✅',
      [BudgetStatus.WARNING]: '⚠️',
      [BudgetStatus.OVER_BUDGET]: '❌'
    };
    return icons[status];
  }

  getPeriodTypeLabel(type: BudgetPeriodType): string {
    return type.charAt(0) + type.slice(1).toLowerCase();
  }
}
