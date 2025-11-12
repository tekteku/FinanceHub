import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AccountService } from '../../services/account.service';
import { Account, AccountType, CreateAccountRequest } from '../../models/account.model';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './accounts.component.html',
  styleUrls: ['./accounts.component.scss']
})
export class AccountsComponent implements OnInit {
  accounts: Account[] = [];
  isLoading = false;
  error: string | null = null;
  showAddForm = false;
  accountForm!: FormGroup;
  accountTypes = Object.values(AccountType);
  editingAccountId: number | null = null;

  constructor(
    private accountService: AccountService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadAccounts();
  }

  initForm(): void {
    this.accountForm = this.fb.group({
      name: ['', [Validators.required]],
      description: [''],
      type: [AccountType.CHECKING, [Validators.required]],
      currency: ['USD', [Validators.required]],
      initialBalance: [0, [Validators.required, Validators.min(0)]]
    });
  }

  loadAccounts(): void {
    this.isLoading = true;
    this.error = null;

    this.accountService.getAllAccounts().subscribe({
      next: (response) => {
        this.accounts = response.data;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to load accounts';
        this.isLoading = false;
        console.error('Error loading accounts:', err);
      }
    });
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) {
      this.accountForm.reset({
        type: AccountType.CHECKING,
        currency: 'USD',
        initialBalance: 0
      });
      this.editingAccountId = null;
    }
  }

  onSubmit(): void {
    if (this.accountForm.invalid) {
      return;
    }

    const accountData: CreateAccountRequest = this.accountForm.value;

    if (this.editingAccountId) {
      this.updateAccount(this.editingAccountId, accountData);
    } else {
      this.createAccount(accountData);
    }
  }

  createAccount(accountData: CreateAccountRequest): void {
    this.isLoading = true;
    this.accountService.createAccount(accountData).subscribe({
      next: (response) => {
        this.accounts.push(response.data);
        this.toggleAddForm();
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to create account';
        this.isLoading = false;
        console.error('Error creating account:', err);
      }
    });
  }

  updateAccount(id: number, accountData: CreateAccountRequest): void {
    this.isLoading = true;
    this.accountService.updateAccount(id, accountData).subscribe({
      next: (response) => {
        const index = this.accounts.findIndex(a => a.id === id);
        if (index !== -1) {
          this.accounts[index] = response.data;
        }
        this.toggleAddForm();
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to update account';
        this.isLoading = false;
        console.error('Error updating account:', err);
      }
    });
  }

  editAccount(account: Account): void {
    this.editingAccountId = account.id;
    this.accountForm.patchValue({
      name: account.name,
      description: account.description || '',
      type: account.type,
      currency: account.currency,
      initialBalance: account.initialBalance
    });
    this.showAddForm = true;
  }

  deleteAccount(id: number): void {
    if (!confirm('Are you sure you want to delete this account?')) {
      return;
    }

    this.accountService.deleteAccount(id).subscribe({
      next: () => {
        this.accounts = this.accounts.filter(a => a.id !== id);
      },
      error: (err) => {
        this.error = 'Failed to delete account';
        console.error('Error deleting account:', err);
      }
    });
  }

  getAccountTypeLabel(type: AccountType): string {
    return type.replace(/_/g, ' ');
  }

  getAccountIcon(type: AccountType): string {
    const icons: { [key in AccountType]: string } = {
      [AccountType.CHECKING]: '🏦',
      [AccountType.SAVINGS]: '💰',
      [AccountType.INVESTMENT]: '📈',
      [AccountType.CREDIT_CARD]: '💳',
      [AccountType.LOAN]: '🏠',
      [AccountType.CRYPTO]: '₿',
      [AccountType.BUSINESS]: '🏢'
    };
    return icons[type] || '💼';
  }
}
