import { useQuery } from "@tanstack/react-query";

import { getResumes } from "@/modules/resume/api/resume.api";

export const resumeQueryKeys = {
  all: ["resumes"] as const,
  list: () => [...resumeQueryKeys.all, "list"] as const,
  detail: (id: string) => [...resumeQueryKeys.all, "detail", id] as const,
};

export const useResumes = () => {
  return useQuery({
    queryKey: resumeQueryKeys.list(),
    queryFn: getResumes,
  });
};
