import { useQuery } from "@tanstack/react-query";

import { getResume } from "@/modules/resume/api/resume.api";
import { resumeQueryKeys } from "@/modules/resume/hooks/use-resumes";

export const useResume = (id: string) => {
  return useQuery({
    queryKey: resumeQueryKeys.detail(id),
    queryFn: () => getResume(id),
    enabled: Boolean(id),
  });
};
