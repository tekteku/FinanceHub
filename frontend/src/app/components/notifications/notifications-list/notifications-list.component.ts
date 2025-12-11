import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Observable } from 'rxjs';
import { Notification } from '../../../models/notification.model';
import { NotificationService } from '../../../services/notification.service';

@Component({
  selector: 'app-notifications-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notifications-list.component.html',
  styleUrls: ['./notifications-list.component.scss']
})
export class NotificationsListComponent implements OnInit {
  notifications$!: Observable<Notification[]>;
  unreadCount$!: Observable<number>;

  constructor(private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.notifications$ = this.notificationService.getNotifications();
    this.unreadCount$ = this.notificationService.getUnreadCount();
  }

  toggleRead(notification: Notification): void {
    if (!notification.isRead && notification.id) {
      this.notificationService.markAsRead(notification.id).subscribe({
        next: () => {
          this.loadNotifications();
        }
      });
    }
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
