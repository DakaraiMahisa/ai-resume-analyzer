export type SalaryPeriod = "MONTHLY" | "ANNUAL";

export type JobSearchStatus =
  | "ACTIVELY_LOOKING"
  | "OPEN_TO_OPPORTUNITIES"
  | "NOT_LOOKING";

export type PreferredEmploymentType =
  | "FULL_TIME"
  | "PART_TIME"
  | "CONTRACT"
  | "INTERNSHIP"
  | "TEMPORARY"
  | "FREELANCE";

export type PreferredWorkMode = "REMOTE" | "HYBRID" | "ONSITE";

export type CandidateCareerPreference = {
  id: string;

  preferredJobTitles: string[];
  preferredEmploymentTypes: PreferredEmploymentType[];
  preferredWorkModes: PreferredWorkMode[];
  preferredLocations: string[];
  preferredIndustries: string[];

  minimumSalary: number | null;
  maximumSalary: number | null;
  salaryCurrency: string | null;
  salaryPeriod: SalaryPeriod | null;

  willingToRelocate: boolean | null;
  noticePeriodDays: number | null;
  jobSearchStatus: JobSearchStatus | null;

  createdAt: string;
  updatedAt: string;
};

export type CreateCandidateCareerPreferenceRequest = {
  preferredJobTitles: string[];
  preferredEmploymentTypes: PreferredEmploymentType[];
  preferredWorkModes: PreferredWorkMode[];
  preferredLocations: string[];
  preferredIndustries: string[];

  minimumSalary: number | null;
  maximumSalary: number | null;
  salaryCurrency: string | null;
  salaryPeriod: SalaryPeriod | null;

  willingToRelocate: boolean | null;
  noticePeriodDays: number | null;
  jobSearchStatus: JobSearchStatus | null;
};

export type UpdateCandidateCareerPreferenceRequest = {
  preferredJobTitles?: string[] | null;
  preferredEmploymentTypes?: PreferredEmploymentType[] | null;
  preferredWorkModes?: PreferredWorkMode[] | null;
  preferredLocations?: string[] | null;
  preferredIndustries?: string[] | null;

  minimumSalary?: number | null;
  maximumSalary?: number | null;
  salaryCurrency?: string | null;
  salaryPeriod?: SalaryPeriod | null;

  willingToRelocate?: boolean | null;
  noticePeriodDays?: number | null;
  jobSearchStatus?: JobSearchStatus | null;

  clearMinimumSalary?: boolean;
  clearMaximumSalary?: boolean;
  clearSalaryCurrency?: boolean;
  clearSalaryPeriod?: boolean;
  clearWillingToRelocate?: boolean;
  clearNoticePeriodDays?: boolean;
  clearJobSearchStatus?: boolean;
};
