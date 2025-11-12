import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TransactionService } from '../../services/transaction.service';
import { AccountService } from '../../services/account.service';
import { CategoryService } from '../../services/category.service';
import { Transaction, TransactionType, CreateTransactionRequest } from '../../models/transaction.model';
import { Account } from '../../models/account.model';
import { Category } from '../../models/category.model';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.scss']
})
export class TransactionsComponent implements OnInit {
  transactions: Transaction[] = [];
  accounts: Account[] = [];
  categories: Category[] = [];
  isLoading = false;
  error: string | null = null;
  showAddForm = false;
  transactionForm!: FormGroup;
  transactionTypes = Object.values(TransactionType);
  editingTransactionId: number | null = null;
  
  // Pagination
  currentPage = 0;
  pageSize = 20;
  totalPages = 0;

  // Filters
  selectedType: TransactionType | null = null;
  selectedAccountId: number | null = null;

  constructor(
    private transactionService: TransactionService,
    private accountService: AccountService,
    private categoryService: CategoryService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadAccounts();
    this.loadCategories();
    this.loadTransactions();
  }

  initForm(): void {
    this.transactionForm = this.fb.group({
      amount: [0, [Validators.required, Validators.min(0.01)]],
      description: ['', [Validators.required]],
      type: [TransactionType.EXPENSE, [Validators.required]],
      accountId: [null, [Validators.required]],
      categoryId: [null],
      transactionDate: [new Date().toISOString().split('T')[0], [Validators.required]]
    });
  }

  loadAccounts(): void {
    this.accountService.getActiveAccounts().subscribe({
      next: (response) => {
        this.accounts = response.data;
      },
      error: (err) => {
        console.error('Error loading accounts:', err);
      }
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

  loadTransactions(): void {
    this.isLoading = true;
    this.error = null;

    let observable;
    
    if (this.selectedType) {
      observable = this.transactionService.getTransactionsByType(this.selectedType, this.currentPage, this.pageSize);
    } else if (this.selectedAccountId) {
      observable = this.transactionService.getTransactionsByAccount(this.selectedAccountId, this.currentPage, this.pageSize);
    } else {
      observable = this.transactionService.getAllTransactions(this.currentPage, this.pageSize);
    }

    observable.subscribe({
      next: (response) => {
        this.transactions = response.content;
        this.totalPages = response.totalPages;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to load transactions';
        this.isLoading = false;
        console.error('Error loading transactions:', err);
      }
    });
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) {
      this.transactionForm.reset({
        amount: 0,
        type: TransactionType.EXPENSE,
        transactionDate: new Date().toISOString().split('T')[0]
      });
      this.editingTransactionId = null;
    }
  }

  onSubmit(): void {
    if (this.transactionForm.invalid) {
      return;
    }

    const transactionData: CreateTransactionRequest = {
      ...this.transactionForm.value,
      transactionDate: new Date(this.transactionForm.value.transactionDate)
    };

    if (this.editingTransactionId) {
      this.updateTransaction(this.editingTransactionId, transactionData);
    } else {
      this.createTransaction(transactionData);
    }
  }

  createTransaction(transactionData: CreateTransactionRequest): void {
    this.isLoading = true;
    this.transactionService.createTransaction(transactionData).subscribe({
      next: (response) => {
        this.loadTransactions();
        this.toggleAddForm();
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to create transaction';
        this.isLoading = false;
        console.error('Error creating transaction:', err);
      }
    });
  }

  updateTransaction(id: number, transactionData: CreateTransactionRequest): void {
    this.isLoading = true;
    this.transactionService.updateTransaction(id, transactionData).subscribe({
      next: (response) => {
        this.loadTransactions();
        this.toggleAddForm();
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to update transaction';
        this.isLoading = false;
        console.error('Error updating transaction:', err);
      }
    });
  }

  editTransaction(transaction: Transaction): void {
    this.editingTransactionId = transaction.id;
    this.transactionForm.patchValue({
      amount: transaction.amount,
      description: transaction.description,
      type: transaction.type,
      accountId: transaction.accountId,
      categoryId: transaction.categoryId,
      transactionDate: new Date(transaction.transactionDate).toISOString().split('T')[0]
    });
    this.showAddForm = true;
  }

  deleteTransaction(id: number): void {
    if (!confirm('Are you sure you want to delete this transaction?')) {
      return;
    }

    this.transactionService.deleteTransaction(id).subscribe({
      next: () => {
        this.loadTransactions();
      },
      error: (err) => {
        this.error = 'Failed to delete transaction';
        console.error('Error deleting transaction:', err);
      }
    });
  }

  filterByType(type: TransactionType | null): void {
    this.selectedType = type;
    this.selectedAccountId = null;
    this.currentPage = 0;
    this.loadTransactions();
  }

  filterByAccount(accountId: number | null): void {
    this.selectedAccountId = accountId;
    this.selectedType = null;
    this.currentPage = 0;
    this.loadTransactions();
  }

  onAccountFilterChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const accountId = target.value ? +target.value : null;
    this.filterByAccount(accountId);
  }

  clearFilters(): void {
    this.selectedType = null;
    this.selectedAccountId = null;
    this.currentPage = 0;
    this.loadTransactions();
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadTransactions();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadTransactions();
    }
  }

  getTransactionIcon(type: TransactionType): string {
    const icons = {
      [TransactionType.INCOME]: '💰',
      [TransactionType.EXPENSE]: '💸',
      [TransactionType.TRANSFER]: '🔄'
    };
    return icons[type];
  }

  getTransactionClass(type: TransactionType): string {
    return type.toLowerCase();
  }
}
