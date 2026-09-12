import { useQuery } from "@tanstack/react-query";

import { getJobDescription } from "@/modules/job/api/job-description.api";
import { jobDescriptionQueryKeys } from "@/modules/job/hooks/use-job-descriptions";

export const useJobDescription = (id: string) => {
  return useQuery({
    queryKey: jobDescriptionQueryKeys.detail(id),
    queryFn: () => getJobDescription(id),
    enabled: Boolean(id),
  });
};
