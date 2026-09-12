import { useMutation, useQueryClient } from "@tanstack/react-query";

import { uploadJobDescription } from "@/modules/job/api/job-description.api";
import { jobDescriptionQueryKeys } from "@/modules/job/hooks/use-job-descriptions";

export const useUploadJobDescription = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: uploadJobDescription,
    onSuccess: () => {
      void queryClient.invalidateQueries({
        queryKey: jobDescriptionQueryKeys.list(),
      });
    },
  });
};
