import { useMutation, useQueryClient } from "@tanstack/react-query";

import { deleteResume } from "@/modules/resume/api/resume.api";
import { resumeQueryKeys } from "@/modules/resume/hooks/use-resumes";

export const useDeleteResume = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteResume,

    onSuccess: (_, id) => {
      queryClient.removeQueries({
        queryKey: resumeQueryKeys.detail(id),
      });

      void queryClient.invalidateQueries({
        queryKey: resumeQueryKeys.list(),
      });
    },
  });
};
