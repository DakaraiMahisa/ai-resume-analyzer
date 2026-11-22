export type CandidateCertification = {
  id: string;
  certificationName: string;
  issuingOrganization: string;
  issueDate: string | null;
  expirationDate: string | null;
  credentialId: string | null;
  credentialUrl: string | null;
  doesNotExpire: boolean;
  createdAt: string;
  updatedAt: string;
};

export type CreateCandidateCertificationRequest = {
  certificationName: string;
  issuingOrganization: string;
  issueDate?: string | null;
  expirationDate?: string | null;
  credentialId?: string | null;
  credentialUrl?: string | null;
  doesNotExpire: boolean;
};

export type UpdateCandidateCertificationRequest = {
  certificationName?: string | null;
  issuingOrganization?: string | null;
  issueDate?: string | null;
  expirationDate?: string | null;
  credentialId?: string | null;
  credentialUrl?: string | null;
  doesNotExpire?: boolean | null;
};
