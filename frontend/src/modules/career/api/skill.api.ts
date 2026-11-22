import type { ApiResponse } from "@/core/api/api-response";
import apiClient from "@/core/api/axios";

import type {
  CandidateSkill,
  CreateCandidateSkillRequest,
  UpdateCandidateSkillRequest,
} from "@/modules/career/types/skill.types";

const BASE_URL = "/api/v1/career/profile/skills";

export const skillApi = {
  getAll: async (): Promise<CandidateSkill[]> => {
    const response =
      await apiClient.get<ApiResponse<CandidateSkill[]>>(BASE_URL);

    if (!response.data.data) {
      throw new Error("Unable to retrieve skills.");
    }

    return response.data.data;
  },

  getById: async (skillId: string): Promise<CandidateSkill> => {
    const response = await apiClient.get<ApiResponse<CandidateSkill>>(
      `${BASE_URL}/${skillId}`,
    );

    if (!response.data.data) {
      throw new Error("Unable to retrieve skill.");
    }

    return response.data.data;
  },

  create: async (
    request: CreateCandidateSkillRequest,
  ): Promise<CandidateSkill> => {
    const response = await apiClient.post<ApiResponse<CandidateSkill>>(
      BASE_URL,
      request,
    );

    if (!response.data.data) {
      throw new Error("Unable to create skill.");
    }

    return response.data.data;
  },

  update: async (
    skillId: string,
    request: UpdateCandidateSkillRequest,
  ): Promise<CandidateSkill> => {
    const response = await apiClient.put<ApiResponse<CandidateSkill>>(
      `${BASE_URL}/${skillId}`,
      request,
    );

    if (!response.data.data) {
      throw new Error("Unable to update skill.");
    }

    return response.data.data;
  },

  delete: async (skillId: string): Promise<void> => {
    await apiClient.delete<ApiResponse<void>>(`${BASE_URL}/${skillId}`);
  },
};
