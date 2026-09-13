import { useMutation, useQueryClient } from "@tanstack/react-query";

import { processResume } from "@/modules/resume/api/resume.api";
import { resumeQueryKeys } from "@/modules/resume/hooks/use-resumes";

type ProcessResumeVariables = {
  processingJobId: string;
  resumeId: string;
};

export function useProcessResume() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ processingJobId }: ProcessResumeVariables) =>
      processResume(processingJobId),

    onSuccess: (_, variables) => {
      void queryClient.invalidateQueries({
        queryKey: resumeQueryKeys.list(),
      });

      void queryClient.invalidateQueries({
        queryKey: resumeQueryKeys.detail(variables.resumeId),
      });
    },
  });
}
