export interface ProjectReview {
    id?: number;
    projectId: number;
    investorId?: number;
    investorName?: string;
    rating: number;
    comment?: string;
    helpful?: boolean;
    createdAt?: string;
}
