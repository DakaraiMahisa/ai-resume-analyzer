import apiClient from "@/core/api/axios";
import type { ApiResponse } from "@/core/api/api-response";
import type {
  CandidateCertification,
  CreateCandidateCertificationRequest,
  UpdateCandidateCertificationRequest,
} from "../types/certification.types";

const BASE_URL = "/api/v1/career/profile/certifications";

export const certificationApi = {
  async getAll(): Promise<CandidateCertification[]> {
    const response =
      await apiClient.get<ApiResponse<CandidateCertification[]>>(BASE_URL);

    if (!response.data.data) {
      throw new Error("Failed to retrieve certifications");
    }

    return response.data.data;
  },

  async getById(id: string): Promise<CandidateCertification> {
    const response = await apiClient.get<ApiResponse<CandidateCertification>>(
      `${BASE_URL}/${id}`,
    );

    if (!response.data.data) {
      throw new Error("Failed to retrieve certification");
    }

    return response.data.data;
  },

  async create(
    request: CreateCandidateCertificationRequest,
  ): Promise<CandidateCertification> {
    const response = await apiClient.post<ApiResponse<CandidateCertification>>(
      BASE_URL,
      request,
    );

    if (!response.data.data) {
      throw new Error("Failed to create certification");
    }

    return response.data.data;
  },

  async update(
    id: string,
    request: UpdateCandidateCertificationRequest,
  ): Promise<CandidateCertification> {
    const response = await apiClient.put<ApiResponse<CandidateCertification>>(
      `${BASE_URL}/${id}`,
      request,
    );

    if (!response.data.data) {
      throw new Error("Failed to update certification");
    }

    return response.data.data;
  },

  async delete(id: string): Promise<void> {
    await apiClient.delete<ApiResponse<null>>(`${BASE_URL}/${id}`);
  },
};
