import apiClient from "@/core/api/axios";
import type { ApiResponse } from "@/core/api/api-response";

import type {
  CandidateAchievement,
  CreateCandidateAchievementRequest,
  UpdateCandidateAchievementRequest,
} from "../types/achievement.types";

const BASE_URL = "/api/v1/career/profile/achievements";

export const achievementApi = {
  getAll: async (): Promise<CandidateAchievement[]> => {
    const response =
      await apiClient.get<ApiResponse<CandidateAchievement[]>>(BASE_URL);

    if (!response.data.data) {
      throw new Error("Failed to retrieve achievements");
    }

    return response.data.data;
  },

  getById: async (achievementId: string): Promise<CandidateAchievement> => {
    const response = await apiClient.get<ApiResponse<CandidateAchievement>>(
      `${BASE_URL}/${achievementId}`,
    );

    if (!response.data.data) {
      throw new Error("Failed to retrieve achievement");
    }

    return response.data.data;
  },

  create: async (
    request: CreateCandidateAchievementRequest,
  ): Promise<CandidateAchievement> => {
    const response = await apiClient.post<ApiResponse<CandidateAchievement>>(
      BASE_URL,
      request,
    );

    if (!response.data.data) {
      throw new Error("Failed to create achievement");
    }

    return response.data.data;
  },

  update: async (
    achievementId: string,
    request: UpdateCandidateAchievementRequest,
  ): Promise<CandidateAchievement> => {
    const response = await apiClient.put<ApiResponse<CandidateAchievement>>(
      `${BASE_URL}/${achievementId}`,
      request,
    );

    if (!response.data.data) {
      throw new Error("Failed to update achievement");
    }

    return response.data.data;
  },

  delete: async (achievementId: string): Promise<void> => {
    await apiClient.delete<ApiResponse<void>>(`${BASE_URL}/${achievementId}`);
  },
};
