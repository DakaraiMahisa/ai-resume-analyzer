export type DocumentProcessingStatus =
  | "UPLOADED"
  | "PROCESSING"
  | "COMPLETED"
  | "FAILED";

export type ResumeSummary = {
  id: string;
  fileName: string;
  status: DocumentProcessingStatus;
  uploadedAt: string;
};

export type ResumeUploadResponse = {
  resumeId: string;
  processingJobId: string;
  status: DocumentProcessingStatus;
  uploadedAt: string;
};

export type ResumeDetail = {
  id: string;
  fileName: string;
  status: DocumentProcessingStatus;
  uploadedAt: string;
  updatedAt: string;
};
