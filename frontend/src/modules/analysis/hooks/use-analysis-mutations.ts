import { useMutation, useQueryClient } from "@tanstack/react-query";

import {
  analysisQueryKeys,
} from "@/modules/analysis/hooks/use-analysis";
import {
  deleteAnalysis,
  retryAnalysis,
} from "@/modules/analysis/api/analysis.api";
import { analysisDashboardQueryKeys } from "@/modules/analysis/hooks/use-analysis-dashboard";

export const useRetryAnalysis = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: retryAnalysis,
    onSuccess: (analysis) => {
      void queryClient.removeQueries({
        queryKey: analysisQueryKeys.detail(analysis.analysisId),
      });
      void queryClient.invalidateQueries({
        queryKey: analysisDashboardQueryKeys.all,
      });
    },
  });
};

export const useDeleteAnalysis = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: deleteAnalysis,
    onSuccess: (_, analysisId) => {
      void queryClient.removeQueries({
        queryKey: analysisQueryKeys.detail(analysisId),
      });
      void queryClient.invalidateQueries({
        queryKey: analysisDashboardQueryKeys.all,
      });
    },
  });
};
