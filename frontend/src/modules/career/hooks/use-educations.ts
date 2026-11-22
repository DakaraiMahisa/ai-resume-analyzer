import { useQuery } from "@tanstack/react-query";

import {
  getEducation,
  getEducations,
} from "@/modules/career/api/education.api";

import { educationQueryKeys } from "@/modules/career/hooks/education.query";

export const useEducations = () => {
  return useQuery({
    queryKey: educationQueryKeys.list(),
    queryFn: getEducations,
  });
};

export const useEducation = (educationId: string) => {
  return useQuery({
    queryKey: educationQueryKeys.detail(educationId),
    queryFn: () => getEducation(educationId),
    enabled: Boolean(educationId),
  });
};
