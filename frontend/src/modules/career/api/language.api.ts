import apiClient from "@/core/api/axios";
import type { ApiResponse } from "@/core/api/api-response";
import type {
  CandidateLanguage,
  CreateCandidateLanguageRequest,
  UpdateCandidateLanguageRequest,
} from "../types/language.types";

const BASE_URL = "/api/v1/career/profile/languages";

export const languageApi = {
  async getAll(): Promise<CandidateLanguage[]> {
    const response =
      await apiClient.get<ApiResponse<CandidateLanguage[]>>(BASE_URL);

    if (!response.data.data) {
      throw new Error("Failed to retrieve languages");
    }

    return response.data.data;
  },

  async getById(id: string): Promise<CandidateLanguage> {
    const response = await apiClient.get<ApiResponse<CandidateLanguage>>(
      `${BASE_URL}/${id}`,
    );

    if (!response.data.data) {
      throw new Error("Failed to retrieve language");
    }

    return response.data.data;
  },

  async create(
    request: CreateCandidateLanguageRequest,
  ): Promise<CandidateLanguage> {
    const response = await apiClient.post<ApiResponse<CandidateLanguage>>(
      BASE_URL,
      request,
    );

    if (!response.data.data) {
      throw new Error("Failed to create language");
    }

    return response.data.data;
  },

  async update(
    id: string,
    request: UpdateCandidateLanguageRequest,
  ): Promise<CandidateLanguage> {
    const response = await apiClient.put<ApiResponse<CandidateLanguage>>(
      `${BASE_URL}/${id}`,
      request,
    );

    if (!response.data.data) {
      throw new Error("Failed to update language");
    }

    return response.data.data;
  },

  async delete(id: string): Promise<void> {
    await apiClient.delete<ApiResponse<null>>(`${BASE_URL}/${id}`);
  },
};
