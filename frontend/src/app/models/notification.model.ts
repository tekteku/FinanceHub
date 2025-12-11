export interface Notification {
    id?: number;
    title: string;
    message: string;
    type: NotificationType;
    isRead: boolean;
    createdAt?: string;
}

export enum NotificationType {
    INVESTMENT = 'INVESTMENT',
    PROJECT_UPDATE = 'PROJECT_UPDATE',
    REVIEW = 'REVIEW',
    MILESTONE = 'MILESTONE',
    SYSTEM = 'SYSTEM'
}
