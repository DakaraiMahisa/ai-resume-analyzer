import { useMutation, useQueryClient } from "@tanstack/react-query";

import { processJobDescription } from "@/modules/job/api/job-description.api";
import { jobDescriptionQueryKeys } from "@/modules/job/hooks/use-job-descriptions";

type ProcessJobDescriptionVariables = {
  processingJobId: string;
  jobDescriptionId: string;
};

export function useProcessJobDescription() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ processingJobId }: ProcessJobDescriptionVariables) =>
      processJobDescription(processingJobId),

    onSuccess: (_, variables) => {
      void queryClient.invalidateQueries({
        queryKey: jobDescriptionQueryKeys.list(),
      });

      void queryClient.invalidateQueries({
        queryKey: jobDescriptionQueryKeys.detail(variables.jobDescriptionId),
      });
    },
  });
}
