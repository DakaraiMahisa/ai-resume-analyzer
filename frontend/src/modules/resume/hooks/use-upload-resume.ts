import { useMutation, useQueryClient } from "@tanstack/react-query";

import { uploadResume } from "@/modules/resume/api/resume.api";
import { resumeQueryKeys } from "@/modules/resume/hooks/use-resumes";

export const useUploadResume = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: uploadResume,

    onSuccess: () => {
      void queryClient.invalidateQueries({
        queryKey: resumeQueryKeys.list(),
      });
    },
  });
};
