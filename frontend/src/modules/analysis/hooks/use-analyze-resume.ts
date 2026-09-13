import { useMutation } from "@tanstack/react-query";

import { analyzeResume } from "@/modules/analysis/api/analysis.api";

type AnalyzeResumeVariables = {
  resumeId: string;
  jobDescriptionId: string;
};

export const useAnalyzeResume = () => {
  return useMutation({
    mutationFn: ({ resumeId, jobDescriptionId }: AnalyzeResumeVariables) =>
      analyzeResume(resumeId, jobDescriptionId),
  });
};
