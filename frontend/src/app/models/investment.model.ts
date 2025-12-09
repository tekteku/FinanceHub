export interface Investment {
    id?: number;
    projectId: number;
    projectTitle?: string;
    investorId?: number;
    investorName?: string;
    amount: number;
    investedAt?: string;
}
