import { useMutation, useQueryClient } from "@tanstack/react-query";

import {
  createEducation,
  updateEducation,
  deleteEducation,
} from "@/modules/career/api/education.api";

import { educationQueryKeys } from "@/modules/career/hooks/education.query";

import type {
  CreateEducationRequest,
  UpdateEducationRequest,
} from "@/modules/career/types/education.types";

export const useCreateEducation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: CreateEducationRequest) => createEducation(request),

    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: educationQueryKeys.lists(),
      });
    },
  });
};

export const useUpdateEducation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      educationId,
      request,
    }: {
      educationId: string;
      request: UpdateEducationRequest;
    }) => updateEducation(educationId, request),

    onSuccess: (updatedEducation) => {
      queryClient.setQueryData(
        educationQueryKeys.detail(updatedEducation.id),
        updatedEducation,
      );

      queryClient.invalidateQueries({
        queryKey: educationQueryKeys.lists(),
      });
    },
  });
};

export const useDeleteEducation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (educationId: string) => deleteEducation(educationId),

    onSuccess: (_, educationId) => {
      queryClient.removeQueries({
        queryKey: educationQueryKeys.detail(educationId),
      });

      queryClient.invalidateQueries({
        queryKey: educationQueryKeys.lists(),
      });
    },
  });
};
