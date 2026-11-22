import apiClient from "@/core/api/axios";
import type { ApiResponse } from "@/core/api/api-response";

import type {
  Education,
  CreateEducationRequest,
  UpdateEducationRequest,
} from "@/modules/career/types/education.types";

const BASE_URL = "/api/v1/career/profile/education";

export const getEducations = async (): Promise<Education[]> => {
  const response = await apiClient.get<ApiResponse<Education[]>>(BASE_URL);

  if (!response.data.data) {
    throw new Error(
      response.data.message ?? "Education records could not be retrieved",
    );
  }

  return response.data.data;
};

export const getEducation = async (educationId: string): Promise<Education> => {
  const response = await apiClient.get<ApiResponse<Education>>(
    `${BASE_URL}/${educationId}`,
  );

  if (!response.data.data) {
    throw new Error(
      response.data.message ?? "Education record could not be retrieved",
    );
  }

  return response.data.data;
};

export const createEducation = async (
  request: CreateEducationRequest,
): Promise<Education> => {
  const response = await apiClient.post<ApiResponse<Education>>(
    BASE_URL,
    request,
  );

  if (!response.data.data) {
    throw new Error(
      response.data.message ?? "Education record creation failed",
    );
  }

  return response.data.data;
};

export const updateEducation = async (
  educationId: string,
  request: UpdateEducationRequest,
): Promise<Education> => {
  const response = await apiClient.put<ApiResponse<Education>>(
    `${BASE_URL}/${educationId}`,
    request,
  );

  if (!response.data.data) {
    throw new Error(response.data.message ?? "Education record update failed");
  }

  return response.data.data;
};

export const deleteEducation = async (educationId: string): Promise<void> => {
  await apiClient.delete<ApiResponse<void>>(`${BASE_URL}/${educationId}`);
};
