export type EmploymentType =
  | "FULL_TIME"
  | "PART_TIME"
  | "CONTRACT"
  | "INTERNSHIP"
  | "FREELANCE"
  | "SELF_EMPLOYED"
  | "TEMPORARY"
  | "OTHER";

export type ProfessionalExperience = {
  id: string;
  companyName: string;
  jobTitle: string;
  employmentType: EmploymentType | null;
  location: string | null;
  startDate: string;
  endDate: string | null;
  currentlyWorking: boolean;
  description: string | null;
  createdAt: string;
  updatedAt: string;
};

export type CreateProfessionalExperienceRequest = {
  companyName: string;
  jobTitle: string;
  employmentType?: EmploymentType | null;
  location?: string | null;
  startDate: string;
  endDate?: string | null;
  currentlyWorking: boolean;
  description?: string | null;
};

export type UpdateProfessionalExperienceRequest = {
  companyName?: string | null;
  jobTitle?: string | null;
  employmentType?: EmploymentType | null;
  location?: string | null;
  startDate?: string | null;
  endDate?: string | null;
  currentlyWorking?: boolean;
  description?: string | null;
};
