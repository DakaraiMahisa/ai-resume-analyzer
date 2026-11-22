export type CandidateProfile = {
  id: string;
  professionalHeadline: string | null;
  professionalSummary: string | null;
  phone: string | null;
  location: string | null;
  linkedinUrl: string | null;
  githubUrl: string | null;
  portfolioUrl: string | null;
  careerObjective: string | null;
  createdAt: string;
  updatedAt: string;
};

export type CandidateProfileCompletion = {
  completionPercentage: number;
  completedFields: number;
  totalFields: number;
  missingFields: string[];
  isComplete: boolean;
};

export type CreateCandidateProfileRequest = {
  professionalHeadline?: string | null;
  professionalSummary?: string | null;
  phone?: string | null;
  location?: string | null;
  linkedinUrl?: string | null;
  githubUrl?: string | null;
  portfolioUrl?: string | null;
  careerObjective?: string | null;
};

export type UpdateCandidateProfileRequest = {
  professionalHeadline?: string | null;
  professionalSummary?: string | null;
  phone?: string | null;
  location?: string | null;
  linkedinUrl?: string | null;
  githubUrl?: string | null;
  portfolioUrl?: string | null;
  careerObjective?: string | null;
};
