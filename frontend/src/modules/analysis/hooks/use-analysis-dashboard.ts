import { useQuery } from "@tanstack/react-query";

import {
  getAnalyses,
  getAnalysisDashboardSummary,
} from "@/modules/analysis/api/analysis.api";

export const analysisDashboardQueryKeys = {
  all: ["analysis-dashboard"] as const,

  summary: () => [...analysisDashboardQueryKeys.all, "summary"] as const,

  recent: (page: number, size: number) =>
    [...analysisDashboardQueryKeys.all, "recent", page, size] as const,
};

export const useAnalysisDashboardSummary = () => {
  return useQuery({
    queryKey: analysisDashboardQueryKeys.summary(),
    queryFn: getAnalysisDashboardSummary,
  });
};

export const useRecentAnalyses = (page = 0, size = 5) => {
  return useQuery({
    queryKey: analysisDashboardQueryKeys.recent(page, size),
    queryFn: () => getAnalyses(page, size),
  });
};
