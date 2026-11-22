import apiClient from "@/core/api/axios";
import type { ApiResponse } from "@/core/api/api-response";
import type {
  CandidateProject,
  CreateCandidateProjectRequest,
  UpdateCandidateProjectRequest,
} from "../types/project.types";

const BASE_URL = "/api/v1/career/profile/projects";

export const projectApi = {
  getAll: async (): Promise<CandidateProject[]> => {
    const response =
      await apiClient.get<ApiResponse<CandidateProject[]>>(BASE_URL);

    if (!response.data.data) {
      throw new Error("Failed to fetch projects");
    }

    return response.data.data;
  },

  getById: async (projectId: string): Promise<CandidateProject> => {
    const response = await apiClient.get<ApiResponse<CandidateProject>>(
      `${BASE_URL}/${projectId}`,
    );

    if (!response.data.data) {
      throw new Error("Failed to fetch project");
    }

    return response.data.data;
  },

  create: async (
    request: CreateCandidateProjectRequest,
  ): Promise<CandidateProject> => {
    const response = await apiClient.post<ApiResponse<CandidateProject>>(
      BASE_URL,
      request,
    );

    if (!response.data.data) {
      throw new Error("Failed to create project");
    }

    return response.data.data;
  },

  update: async (
    projectId: string,
    request: UpdateCandidateProjectRequest,
  ): Promise<CandidateProject> => {
    const response = await apiClient.put<ApiResponse<CandidateProject>>(
      `${BASE_URL}/${projectId}`,
      request,
    );

    if (!response.data.data) {
      throw new Error("Failed to update project");
    }

    return response.data.data;
  },

  delete: async (projectId: string): Promise<void> => {
    await apiClient.delete<ApiResponse<void>>(`${BASE_URL}/${projectId}`);
  },
};
