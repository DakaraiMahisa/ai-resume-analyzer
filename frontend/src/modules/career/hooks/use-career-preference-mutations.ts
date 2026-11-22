import { useMutation, useQueryClient } from "@tanstack/react-query";

import { preferenceApi } from "../api/preference.api";
import { candidateCareerPreferenceQueryKeys } from "./preference.query";

import type {
  CreateCandidateCareerPreferenceRequest,
  UpdateCandidateCareerPreferenceRequest,
} from "../types/preference.types";

export const useCareerPreferenceMutations = () => {
  const queryClient = useQueryClient();

  const createPreferences = useMutation({
    mutationFn: (request: CreateCandidateCareerPreferenceRequest) =>
      preferenceApi.create(request),

    onSuccess: (preferences) => {
      queryClient.setQueryData(
        candidateCareerPreferenceQueryKeys.current(),
        preferences,
      );
    },
  });

  const updatePreferences = useMutation({
    mutationFn: (request: UpdateCandidateCareerPreferenceRequest) =>
      preferenceApi.update(request),

    onSuccess: (preferences) => {
      queryClient.setQueryData(
        candidateCareerPreferenceQueryKeys.current(),
        preferences,
      );
    },
  });

  return {
    createPreferences,
    updatePreferences,
  };
};
