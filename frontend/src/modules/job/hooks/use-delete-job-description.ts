import { useMutation, useQueryClient } from "@tanstack/react-query";

import { deleteJobDescription } from "@/modules/job/api/job-description.api";
import { jobDescriptionQueryKeys } from "@/modules/job/hooks/use-job-descriptions";

export const useDeleteJobDescription = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteJobDescription,
    onSuccess: () => {
      void queryClient.invalidateQueries({
        queryKey: jobDescriptionQueryKeys.list(),
      });
    },
  });
};
