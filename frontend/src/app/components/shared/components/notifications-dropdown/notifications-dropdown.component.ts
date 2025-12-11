import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Observable } from 'rxjs';
import { Notification } from '../../../models/notification.model';
import { NotificationService } from '../../../services/notification.service';

@Component({
  selector: 'app-notifications-dropdown',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notifications-dropdown.component.html',
  styleUrls: ['./notifications-dropdown.component.scss']
})
export class NotificationsDropdownComponent implements OnInit {
  unreadNotifications$!: Observable<Notification[]>;
  unreadCount$!: Observable<number>;

  constructor(private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.unreadNotifications$ = this.notificationService.getUnreadNotifications();
    this.unreadCount$ = this.notificationService.getUnreadCount();
  }

  markAsRead(id: number): void {
    this.notificationService.markAsRead(id).subscribe({
      next: () => {
        this.unreadNotifications$ = this.notificationService.getUnreadNotifications();
        this.unreadCount$ = this.notificationService.getUnreadCount();
      }
    });
  }

  getIcon(type: string): string {
    const icons: any = {
      'INVESTMENT': '💰',
      'PROJECT_UPDATE': '📢',
      'REVIEW': '⭐',
      'MILESTONE': '🎯',
      'SYSTEM': '⚙️'
    };
    return icons[type] || '🔔';
  }
}
