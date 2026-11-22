export type Education = {
  id: string;
  institution: string;
  degree: string;
  fieldOfStudy: string | null;
  location: string | null;
  startDate: string;
  endDate: string | null;
  currentlyStudying: boolean;
  grade: string | null;
  description: string | null;
  createdAt: string;
  updatedAt: string;
};

export type CreateEducationRequest = {
  institution: string;
  degree: string;
  fieldOfStudy?: string | null;
  location?: string | null;
  startDate: string;
  endDate?: string | null;
  currentlyStudying: boolean;
  grade?: string | null;
  description?: string | null;
};

export type UpdateEducationRequest = {
  institution?: string | null;
  degree?: string | null;
  fieldOfStudy?: string | null;
  location?: string | null;
  startDate?: string | null;
  endDate?: string | null;
  currentlyStudying?: boolean;
  grade?: string | null;
  description?: string | null;
};
