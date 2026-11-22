export type CandidateProject = {
  id: string;
  projectName: string;
  role: string | null;
  description: string | null;
  technologies: string[];
  projectUrl: string | null;
  startDate: string | null;
  endDate: string | null;
  currentlyWorking: boolean;
  createdAt: string;
  updatedAt: string;
};

export type CreateCandidateProjectRequest = {
  projectName: string;
  role?: string | null;
  description?: string | null;
  technologies?: string[];
  projectUrl?: string | null;
  startDate?: string | null;
  endDate?: string | null;
  currentlyWorking: boolean;
};

export type UpdateCandidateProjectRequest = {
  projectName?: string | null;
  role?: string | null;
  description?: string | null;
  technologies?: string[] | null;
  projectUrl?: string | null;
  startDate?: string | null;
  endDate?: string | null;
  currentlyWorking?: boolean | null;
};
