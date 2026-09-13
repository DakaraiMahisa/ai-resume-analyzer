import { useMutation, useQueryClient } from "@tanstack/react-query";

import { createJobDescriptionFromText } from "@/modules/job/api/job-description.api";
import { jobDescriptionQueryKeys } from "@/modules/job/hooks/use-job-descriptions";

export const useCreateJobDescriptionFromText = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createJobDescriptionFromText,
    onSuccess: () => {
      void queryClient.invalidateQueries({
        queryKey: jobDescriptionQueryKeys.list(),
      });
    },
  });
};
