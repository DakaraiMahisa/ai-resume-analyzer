import type { DocumentProcessingStatus } from "@/modules/resume/types/resume.types";

export type JobDescriptionTextRequest = {
  title?: string;
  content: string;
};

export type JobDescriptionDetailResponse = {
  id: string;
  fileName: string;
  status: DocumentProcessingStatus;
  uploadedAt: string;
  updatedAt: string;
};

export type JobDescriptionSummaryResponse = {
  id: string;
  fileName: string;
  status: DocumentProcessingStatus;
  uploadedAt: string;
};

export type JobDescriptionUploadResponse = {
  jobDescriptionId: string;
  processingJobId: string;
  status: DocumentProcessingStatus;
  uploadedAt: string;
};
