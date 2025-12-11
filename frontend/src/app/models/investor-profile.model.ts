export interface InvestorProfile {
    userId: number;
    username: string;
    fullName: string;
    email: string;
    totalInvested: number;
    projectsInvested: number;
    averageReturnRate?: number;
    totalReviews: number;
    averageRating?: number;
    createdAt: string;
}
