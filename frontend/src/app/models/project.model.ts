export enum ProjectStatus {
    PENDING = 'PENDING',
    ACTIVE = 'ACTIVE',
    FUNDED = 'FUNDED',
    REJECTED = 'REJECTED',
    COMPLETED = 'COMPLETED'
}

export interface Project {
    id?: number;
    title: string;
    description: string;
    targetAmount: number;
    currentAmount?: number;
    status?: ProjectStatus;
    ownerId?: number;
    ownerName?: string;
    createdAt?: string;
}
