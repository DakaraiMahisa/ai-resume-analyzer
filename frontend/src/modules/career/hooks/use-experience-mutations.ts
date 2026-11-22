import { useMutation, useQueryClient } from "@tanstack/react-query";

import {
  createProfessionalExperience,
  updateProfessionalExperience,
  deleteProfessionalExperience,
} from "@/modules/career/api/experience.api";

import { professionalExperienceQueryKeys } from "@/modules/career/hooks/experience.query";

import type {
  CreateProfessionalExperienceRequest,
  UpdateProfessionalExperienceRequest,
} from "@/modules/career/types/experience.types";

export const useCreateProfessionalExperience = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: CreateProfessionalExperienceRequest) =>
      createProfessionalExperience(request),

    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: professionalExperienceQueryKeys.lists(),
      });
    },
  });
};

export const useUpdateProfessionalExperience = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      experienceId,
      request,
    }: {
      experienceId: string;
      request: UpdateProfessionalExperienceRequest;
    }) => updateProfessionalExperience(experienceId, request),

    onSuccess: (updatedExperience) => {
      queryClient.setQueryData(
        professionalExperienceQueryKeys.detail(updatedExperience.id),
        updatedExperience,
      );

      queryClient.invalidateQueries({
        queryKey: professionalExperienceQueryKeys.lists(),
      });
    },
  });
};

export const useDeleteProfessionalExperience = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (experienceId: string) =>
      deleteProfessionalExperience(experienceId),

    onSuccess: (_, experienceId) => {
      queryClient.removeQueries({
        queryKey: professionalExperienceQueryKeys.detail(experienceId),
      });

      queryClient.invalidateQueries({
        queryKey: professionalExperienceQueryKeys.lists(),
      });
    },
  });
};
