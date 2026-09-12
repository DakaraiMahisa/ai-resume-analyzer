import type { ApiResponse, Pagination } from "@/core/api/api-response";
import apiClient from "@/core/api/axios";
import type {
  ResumeAnalysisResponse,
  AnalysisSummaryResponse,
  AnalysisDashboardSummaryResponse,
} from "@/modules/analysis/types/analysis.types";

export const analyzeResume = async (
  resumeId: string,
  jobDescriptionId: string,
): Promise<ResumeAnalysisResponse> => {
  const response = await apiClient.post<ApiResponse<ResumeAnalysisResponse>>(
    `/api/v1/analysis/resumes/${resumeId}/job-descriptions/${jobDescriptionId}/analyze`,
  );

  if (!response.data.data) {
    throw new Error(response.data.message ?? "Resume analysis failed");
  }

  return response.data.data;
};

export const getAnalysis = async (
  analysisId: string,
): Promise<ResumeAnalysisResponse> => {
  const response = await apiClient.get<ApiResponse<ResumeAnalysisResponse>>(
    `/api/v1/analysis/${analysisId}`,
  );

  if (!response.data.data) {
    throw new Error(
      response.data.message ?? "Analysis result was not returned",
    );
  }

  return response.data.data;
};

export const getAnalyses = async (
  page = 0,
  size = 10,
): Promise<{
  analyses: AnalysisSummaryResponse[];
  pagination: Pagination | null;
}> => {
  const response = await apiClient.get<ApiResponse<AnalysisSummaryResponse[]>>(
    "/api/v1/analysis",
    {
      params: {
        page,
        size,
      },
    },
  );

  return {
    analyses: response.data.data ?? [],
    pagination: response.data.pagination,
  };
};

export const getAnalysisDashboardSummary =
  async (): Promise<AnalysisDashboardSummaryResponse> => {
    const response = await apiClient.get<
      ApiResponse<AnalysisDashboardSummaryResponse>
    >("/api/v1/analysis/summary");

    if (!response.data.data) {
      throw new Error(
        response.data.message ?? "Analysis dashboard summary was not returned",
      );
    }

    return response.data.data;
  };
