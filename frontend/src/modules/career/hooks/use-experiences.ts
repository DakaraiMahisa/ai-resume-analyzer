import { useQuery } from "@tanstack/react-query";

import {
  getProfessionalExperience,
  getProfessionalExperiences,
} from "@/modules/career/api/experience.api";

import { professionalExperienceQueryKeys } from "@/modules/career/hooks/experience.query";

export const useProfessionalExperiences = () => {
  return useQuery({
    queryKey: professionalExperienceQueryKeys.list(),
    queryFn: getProfessionalExperiences,
  });
};

export const useProfessionalExperience = (experienceId: string) => {
  return useQuery({
    queryKey: professionalExperienceQueryKeys.detail(experienceId),
    queryFn: () => getProfessionalExperience(experienceId),
    enabled: Boolean(experienceId),
  });
};
