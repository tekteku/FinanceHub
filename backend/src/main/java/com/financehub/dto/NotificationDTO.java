package com.financehub.dto;

import com.financehub.entity.Notification;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private Notification.NotificationType type;
    private Boolean isRead;
    private String createdAt;
}
