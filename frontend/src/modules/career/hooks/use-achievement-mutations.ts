import { useMutation, useQueryClient } from "@tanstack/react-query";

import { achievementApi } from "../api/achievement.api";
import { candidateAchievementQueryKeys } from "./achievement.query";

import type {
  CreateCandidateAchievementRequest,
  UpdateCandidateAchievementRequest,
} from "../types/achievement.types";

export const useAchievementMutations = () => {
  const queryClient = useQueryClient();

  const createAchievement = useMutation({
    mutationFn: (request: CreateCandidateAchievementRequest) =>
      achievementApi.create(request),

    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: candidateAchievementQueryKeys.lists(),
      });
    },
  });

  const updateAchievement = useMutation({
    mutationFn: ({
      achievementId,
      request,
    }: {
      achievementId: string;
      request: UpdateCandidateAchievementRequest;
    }) => achievementApi.update(achievementId, request),

    onSuccess: (updatedAchievement) => {
      queryClient.setQueryData(
        candidateAchievementQueryKeys.detail(updatedAchievement.id),
        updatedAchievement,
      );

      queryClient.invalidateQueries({
        queryKey: candidateAchievementQueryKeys.lists(),
      });
    },
  });

  const deleteAchievement = useMutation({
    mutationFn: (achievementId: string) => achievementApi.delete(achievementId),

    onSuccess: (_, achievementId) => {
      queryClient.removeQueries({
        queryKey: candidateAchievementQueryKeys.detail(achievementId),
      });

      queryClient.invalidateQueries({
        queryKey: candidateAchievementQueryKeys.lists(),
      });
    },
  });

  return {
    createAchievement,
    updateAchievement,
    deleteAchievement,
  };
};
