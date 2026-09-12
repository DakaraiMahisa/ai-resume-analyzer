import { useQuery } from "@tanstack/react-query";

import { getAnalysis } from "@/modules/analysis/api/analysis.api";

export const analysisQueryKeys = {
  all: ["analyses"] as const,
  detail: (analysisId: string) =>
    [...analysisQueryKeys.all, "detail", analysisId] as const,
};

export const useAnalysis = (analysisId: string) => {
  return useQuery({
    queryKey: analysisQueryKeys.detail(analysisId),
    queryFn: () => getAnalysis(analysisId),
    enabled: Boolean(analysisId),
  });
};
