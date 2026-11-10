import { useQuery } from "@tanstack/react-query";
import {
  getAnalyses,
  getAnalysisDashboardSummary,
} from "@/modules/analysis/api/analysis.api";
import { useCurrentUser } from "@/modules/user/hooks/use-current-user";

export const analysisDashboardQueryKeys = {
  all: ["analysis-dashboard"] as const,

  user: (userId: string) =>
    [...analysisDashboardQueryKeys.all, userId] as const,

  summary: (userId: string) =>
    [...analysisDashboardQueryKeys.user(userId), "summary"] as const,

  recent: (userId: string, page: number, size: number) =>
    [...analysisDashboardQueryKeys.user(userId), "recent", page, size] as const,
};

export const useAnalysisDashboardSummary = () => {
  const { data: user } = useCurrentUser();

  return useQuery({
    queryKey: analysisDashboardQueryKeys.summary(user?.id ?? "unauthenticated"),
    queryFn: getAnalysisDashboardSummary,
    enabled: !!user?.id,
  });
};

export const useRecentAnalyses = (page = 0, size = 5) => {
  const { data: user } = useCurrentUser();

  return useQuery({
    queryKey: analysisDashboardQueryKeys.recent(
      user?.id ?? "unauthenticated",
      page,
      size,
    ),
    queryFn: () => getAnalyses(page, size),
    enabled: !!user?.id,
  });
};
