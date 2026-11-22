import { useMutation, useQueryClient } from "@tanstack/react-query";

import {
  createCandidateProfile,
  updateCandidateProfile,
} from "@/modules/career/api/profile.api";

import { candidateProfileQueryKeys } from "@/modules/career/hooks/use-profile";

import type {
  CreateCandidateProfileRequest,
  UpdateCandidateProfileRequest,
} from "@/modules/career/types/profile.types";

export const useCreateCandidateProfile = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: CreateCandidateProfileRequest) =>
      createCandidateProfile(request),

    onSuccess: (profile) => {
      queryClient.setQueryData(candidateProfileQueryKeys.profile(), profile);

      queryClient.invalidateQueries({
        queryKey: candidateProfileQueryKeys.completion(),
      });
    },
  });
};

export const useUpdateCandidateProfile = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: UpdateCandidateProfileRequest) =>
      updateCandidateProfile(request),

    onSuccess: (profile) => {
      queryClient.setQueryData(candidateProfileQueryKeys.profile(), profile);

      queryClient.invalidateQueries({
        queryKey: candidateProfileQueryKeys.completion(),
      });
    },
  });
};
