import { useMutation, useQueryClient } from "@tanstack/react-query";

import { skillApi } from "@/modules/career/api/skill.api";
import { candidateSkillQueryKeys } from "@/modules/career/hooks/skill.query";

import type {
  CreateCandidateSkillRequest,
  UpdateCandidateSkillRequest,
} from "@/modules/career/types/skill.types";

export function useCreateSkill() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: CreateCandidateSkillRequest) =>
      skillApi.create(request),

    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: candidateSkillQueryKeys.lists(),
      });
    },
  });
}

export function useUpdateSkill() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      skillId,
      request,
    }: {
      skillId: string;
      request: UpdateCandidateSkillRequest;
    }) => skillApi.update(skillId, request),

    onSuccess: (updatedSkill) => {
      queryClient.setQueryData(
        candidateSkillQueryKeys.detail(updatedSkill.id),
        updatedSkill,
      );

      queryClient.invalidateQueries({
        queryKey: candidateSkillQueryKeys.lists(),
      });
    },
  });
}

export function useDeleteSkill() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (skillId: string) => skillApi.delete(skillId),

    onSuccess: (_, skillId) => {
      queryClient.removeQueries({
        queryKey: candidateSkillQueryKeys.detail(skillId),
      });

      queryClient.invalidateQueries({
        queryKey: candidateSkillQueryKeys.lists(),
      });
    },
  });
}
