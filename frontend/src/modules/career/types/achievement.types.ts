export type AchievementType =
  | "AWARD"
  | "HONOR"
  | "COMPETITION"
  | "SCHOLARSHIP"
  | "RECOGNITION"
  | "OTHER";

export type CandidateAchievement = {
  id: string;
  achievementName: string;
  issuingOrganization: string | null;
  achievementType: AchievementType;
  issueDate: string | null;
  description: string | null;
  achievementUrl: string | null;
  createdAt: string;
  updatedAt: string;
};

export type CreateCandidateAchievementRequest = {
  achievementName: string;
  issuingOrganization: string | null;
  achievementType: AchievementType;
  issueDate: string | null;
  description: string | null;
  achievementUrl: string | null;
};

export type UpdateCandidateAchievementRequest = {
  achievementName?: string | null;
  issuingOrganization?: string | null;
  achievementType?: AchievementType | null;
  issueDate?: string | null;
  description?: string | null;
  achievementUrl?: string | null;
};
