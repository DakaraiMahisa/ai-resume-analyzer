export type SkillCategory =
  | "TECHNICAL"
  | "SOFT"
  | "LANGUAGE"
  | "TOOL"
  | "DOMAIN";

export type ProficiencyLevel =
  | "BEGINNER"
  | "INTERMEDIATE"
  | "ADVANCED"
  | "EXPERT";

export type CandidateSkill = {
  id: string;
  skillName: string;
  category: SkillCategory;
  proficiencyLevel: ProficiencyLevel;
  yearsOfExperience: number | null;
  createdAt: string;
  updatedAt: string;
};

export type CreateCandidateSkillRequest = {
  skillName: string;
  category: SkillCategory;
  proficiencyLevel: ProficiencyLevel;
  yearsOfExperience?: number | null;
};

export type UpdateCandidateSkillRequest = {
  skillName?: string | null;
  category?: SkillCategory | null;
  proficiencyLevel?: ProficiencyLevel | null;
  yearsOfExperience?: number | null;
  clearYearsOfExperience?: boolean;
};
