import apiClient from "@/core/api/axios";
import type { ApiResponse } from "@/core/api/api-response";

import type {
  CandidateProfile,
  CandidateProfileCompletion,
  CreateCandidateProfileRequest,
  UpdateCandidateProfileRequest,
} from "@/modules/career/types/profile.types";

export const getCandidateProfile = async (): Promise<CandidateProfile> => {
  const response = await apiClient.get<ApiResponse<CandidateProfile>>(
    "/api/v1/career/profile",
  );

  if (!response.data.data) {
    throw new Error(
      response.data.message ?? "Candidate profile could not be retrieved",
    );
  }

  return response.data.data;
};

export const getCandidateProfileCompletion =
  async (): Promise<CandidateProfileCompletion> => {
    const response = await apiClient.get<
      ApiResponse<CandidateProfileCompletion>
    >("/api/v1/career/profile/completion");

    if (!response.data.data) {
      throw new Error(
        response.data.message ?? "Profile completion could not be retrieved",
      );
    }

    return response.data.data;
  };

export const createCandidateProfile = async (
  request: CreateCandidateProfileRequest,
): Promise<CandidateProfile> => {
  const response = await apiClient.post<ApiResponse<CandidateProfile>>(
    "/api/v1/career/profile",
    request,
  );

  if (!response.data.data) {
    throw new Error(
      response.data.message ?? "Candidate profile creation failed",
    );
  }

  return response.data.data;
};

export const updateCandidateProfile = async (
  request: UpdateCandidateProfileRequest,
): Promise<CandidateProfile> => {
  const response = await apiClient.put<ApiResponse<CandidateProfile>>(
    "/api/v1/career/profile",
    request,
  );

  if (!response.data.data) {
    throw new Error(response.data.message ?? "Candidate profile update failed");
  }

  return response.data.data;
};
