import apiClient from "@/core/api/axios";
import type { ApiResponse } from "@/core/api/api-response";

import type {
  JobDescriptionDetailResponse,
  JobDescriptionSummaryResponse,
  JobDescriptionTextRequest,
  JobDescriptionUploadResponse,
} from "@/modules/job/types/job-description.types";

export const getJobDescriptions = async (): Promise<
  JobDescriptionSummaryResponse[]
> => {
  const response = await apiClient.get<
    ApiResponse<JobDescriptionSummaryResponse[]>
  >("/api/v1/job-descriptions");

  if (!response.data.data) {
    return [];
  }

  return response.data.data;
};

export const getJobDescription = async (
  id: string,
): Promise<JobDescriptionDetailResponse> => {
  const response = await apiClient.get<
    ApiResponse<JobDescriptionDetailResponse>
  >(`/api/v1/job-descriptions/${id}`);

  if (!response.data.data) {
    throw new Error(
      response.data.message ?? "Job description data was not returned",
    );
  }

  return response.data.data;
};

export const uploadJobDescription = async (
  file: File,
): Promise<JobDescriptionUploadResponse> => {
  const formData = new FormData();

  formData.append("file", file);

  const response = await apiClient.post<
    ApiResponse<JobDescriptionUploadResponse>
  >("/api/v1/job-descriptions", formData);

  if (!response.data.data) {
    throw new Error(response.data.message ?? "Job description upload failed");
  }

  return response.data.data;
};

export const createJobDescriptionFromText = async (
  request: JobDescriptionTextRequest,
): Promise<JobDescriptionUploadResponse> => {
  const response = await apiClient.post<
    ApiResponse<JobDescriptionUploadResponse>
  >("/api/v1/job-descriptions/text", request);

  if (!response.data.data) {
    throw new Error(response.data.message ?? "Job description creation failed");
  }

  return response.data.data;
};

export const deleteJobDescription = async (id: string): Promise<void> => {
  await apiClient.delete<ApiResponse<void>>(`/api/v1/job-descriptions/${id}`);
};
