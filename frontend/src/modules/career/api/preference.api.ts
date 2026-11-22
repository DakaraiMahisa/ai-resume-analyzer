import apiClient from "@/core/api/axios";
import type { ApiResponse } from "@/core/api/api-response";

import type {
  CandidateCareerPreference,
  CreateCandidateCareerPreferenceRequest,
  UpdateCandidateCareerPreferenceRequest,
} from "../types/preference.types";

const BASE_URL = "/api/v1/career/profile/preferences";

export const preferenceApi = {
  get: async (): Promise<CandidateCareerPreference> => {
    const response =
      await apiClient.get<ApiResponse<CandidateCareerPreference>>(BASE_URL);

    if (!response.data.data) {
      throw new Error("Failed to retrieve career preferences");
    }

    return response.data.data;
  },

  create: async (
    request: CreateCandidateCareerPreferenceRequest,
  ): Promise<CandidateCareerPreference> => {
    const response = await apiClient.post<
      ApiResponse<CandidateCareerPreference>
    >(BASE_URL, request);

    if (!response.data.data) {
      throw new Error("Failed to create career preferences");
    }

    return response.data.data;
  },

  update: async (
    request: UpdateCandidateCareerPreferenceRequest,
  ): Promise<CandidateCareerPreference> => {
    const response = await apiClient.put<
      ApiResponse<CandidateCareerPreference>
    >(BASE_URL, request);

    if (!response.data.data) {
      throw new Error("Failed to update career preferences");
    }

    return response.data.data;
  },
};
