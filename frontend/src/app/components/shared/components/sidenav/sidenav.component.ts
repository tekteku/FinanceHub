import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-sidenav',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.scss'
})
export class SidenavComponent {
  isCollapsed = false;
  currentUser$;

  menuItems = [
    { icon: '📊', label: 'Dashboard', route: '/dashboard', badge: null },
    { icon: '💰', label: 'Accounts', route: '/accounts', badge: null },
    { icon: '💸', label: 'Transactions', route: '/transactions', badge: null },
    { icon: '📈', label: 'Budgets', route: '/budgets', badge: '3' },
    { icon: '📉', label: 'Analytics', route: '/analytics', badge: null },
    { icon: '🚀', label: 'Projects', route: '/projects', badge: 'New' },
    { icon: '💼', label: 'My Investments', route: '/my-investments', badge: null },
  ];

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    this.currentUser$ = this.authService.currentUser$;
  }

  toggleSidebar(): void {
    this.isCollapsed = !this.isCollapsed;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  isActive(route: string): boolean {
    return this.router.url === route;
  }
}
