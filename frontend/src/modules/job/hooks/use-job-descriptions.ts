import { useQuery } from "@tanstack/react-query";

import { getJobDescriptions } from "@/modules/job/api/job-description.api";

export const jobDescriptionQueryKeys = {
  all: ["job-descriptions"] as const,
  list: () => [...jobDescriptionQueryKeys.all, "list"] as const,
  detail: (id: string) =>
    [...jobDescriptionQueryKeys.all, "detail", id] as const,
};

export const useJobDescriptions = () => {
  return useQuery({
    queryKey: jobDescriptionQueryKeys.list(),
    queryFn: getJobDescriptions,
  });
};
