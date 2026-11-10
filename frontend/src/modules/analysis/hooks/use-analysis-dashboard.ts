import { useQuery } from "@tanstack/react-query";
import {
  getAnalyses,
  getAnalysisDashboardSummary,
} from "@/modules/analysis/api/analysis.api";
import { useCurrentUser } from "@/modules/user/hooks/use-current-user";
import type { AnalysisStatus } from "@/modules/analysis/types/analysis.types";

export const analysisDashboardQueryKeys = {
  all: ["analysis-dashboard"] as const,

  user: (userId: string) =>
    [...analysisDashboardQueryKeys.all, userId] as const,

  summary: (userId: string) =>
    [...analysisDashboardQueryKeys.user(userId), "summary"] as const,

  recent: (
    userId: string,
    page: number,
    size: number,
    status?: AnalysisStatus,
  ) =>
    [
      ...analysisDashboardQueryKeys.user(userId),
      "recent",
      page,
      size,
      status ?? "ALL",
    ] as const,
};

export const useAnalysisDashboardSummary = () => {
  const { data: user } = useCurrentUser();

  return useQuery({
    queryKey: analysisDashboardQueryKeys.summary(user?.id ?? "unauthenticated"),
    queryFn: getAnalysisDashboardSummary,
    enabled: !!user?.id,
  });
};

export const useRecentAnalyses = (
  page = 0,
  size = 5,
  status?: AnalysisStatus,
) => {
  const { data: user } = useCurrentUser();

  return useQuery({
    queryKey: analysisDashboardQueryKeys.recent(
      user?.id ?? "unauthenticated",
      page,
      size,
      status,
    ),
    queryFn: () => getAnalyses(page, size, status),
    enabled: !!user?.id,
  });
};
