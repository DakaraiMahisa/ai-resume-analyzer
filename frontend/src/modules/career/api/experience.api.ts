import apiClient from "@/core/api/axios";
import type { ApiResponse } from "@/core/api/api-response";

import type {
  ProfessionalExperience,
  CreateProfessionalExperienceRequest,
  UpdateProfessionalExperienceRequest,
} from "@/modules/career/types/experience.types";

const BASE_URL = "/api/v1/career/profile/experiences";

export const getProfessionalExperiences = async (): Promise<
  ProfessionalExperience[]
> => {
  const response =
    await apiClient.get<ApiResponse<ProfessionalExperience[]>>(BASE_URL);

  if (!response.data.data) {
    throw new Error(
      response.data.message ??
        "Professional experiences could not be retrieved",
    );
  }

  return response.data.data;
};

export const getProfessionalExperience = async (
  experienceId: string,
): Promise<ProfessionalExperience> => {
  const response = await apiClient.get<ApiResponse<ProfessionalExperience>>(
    `${BASE_URL}/${experienceId}`,
  );

  if (!response.data.data) {
    throw new Error(
      response.data.message ?? "Professional experience could not be retrieved",
    );
  }

  return response.data.data;
};

export const createProfessionalExperience = async (
  request: CreateProfessionalExperienceRequest,
): Promise<ProfessionalExperience> => {
  const response = await apiClient.post<ApiResponse<ProfessionalExperience>>(
    BASE_URL,
    request,
  );

  if (!response.data.data) {
    throw new Error(
      response.data.message ?? "Professional experience creation failed",
    );
  }

  return response.data.data;
};

export const updateProfessionalExperience = async (
  experienceId: string,
  request: UpdateProfessionalExperienceRequest,
): Promise<ProfessionalExperience> => {
  const response = await apiClient.put<ApiResponse<ProfessionalExperience>>(
    `${BASE_URL}/${experienceId}`,
    request,
  );

  if (!response.data.data) {
    throw new Error(
      response.data.message ?? "Professional experience update failed",
    );
  }

  return response.data.data;
};

export const deleteProfessionalExperience = async (
  experienceId: string,
): Promise<void> => {
  await apiClient.delete<ApiResponse<void>>(`${BASE_URL}/${experienceId}`);
};
