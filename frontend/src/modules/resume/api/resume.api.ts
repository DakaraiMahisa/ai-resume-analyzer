import apiClient from "@/core/api/axios";
import type { ApiResponse } from "@/core/api/api-response";

import type {
  ResumeDetail,
  ResumeSummary,
  ResumeUploadResponse,
} from "@/modules/resume/types/resume.types";

export const getResumes = async (): Promise<ResumeSummary[]> => {
  const response =
    await apiClient.get<ApiResponse<ResumeSummary[]>>("/api/v1/resumes");

  if (!response.data.data) {
    return [];
  }

  return response.data.data;
};

export const getResume = async (id: string): Promise<ResumeDetail> => {
  const response = await apiClient.get<ApiResponse<ResumeDetail>>(
    `/api/v1/resumes/${id}`,
  );

  if (!response.data.data) {
    throw new Error(response.data.message ?? "Resume data was not returned");
  }

  return response.data.data;
};

export const uploadResume = async (
  file: File,
): Promise<ResumeUploadResponse> => {
  const formData = new FormData();

  formData.append("file", file);

  const response = await apiClient.post<ApiResponse<ResumeUploadResponse>>(
    "/api/v1/resumes",
    formData,
  );

  if (!response.data.data) {
    throw new Error(response.data.message ?? "Resume upload failed");
  }

  return response.data.data;
};
export const processResume = async (processingJobId: string): Promise<void> => {
  await apiClient.post<ApiResponse<void>>(
    `/api/v1/processing/jobs/${processingJobId}/process`,
  );
};
export const deleteResume = async (id: string): Promise<void> => {
  await apiClient.delete<ApiResponse<void>>(`/api/v1/resumes/${id}`);
};
