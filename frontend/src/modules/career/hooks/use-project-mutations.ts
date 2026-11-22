import { useMutation, useQueryClient } from "@tanstack/react-query";

import { projectApi } from "../api/project.api";
import { candidateProjectQueryKeys } from "./project.query";
import type {
  CreateCandidateProjectRequest,
  UpdateCandidateProjectRequest,
} from "../types/project.types";

export const useCreateProject = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: CreateCandidateProjectRequest) =>
      projectApi.create(request),

    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: candidateProjectQueryKeys.list(),
      });
    },
  });
};

export const useUpdateProject = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      projectId,
      request,
    }: {
      projectId: string;
      request: UpdateCandidateProjectRequest;
    }) => projectApi.update(projectId, request),

    onSuccess: (updatedProject) => {
      queryClient.setQueryData(
        candidateProjectQueryKeys.detail(updatedProject.id),
        updatedProject,
      );

      queryClient.invalidateQueries({
        queryKey: candidateProjectQueryKeys.list(),
      });
    },
  });
};

export const useDeleteProject = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (projectId: string) => projectApi.delete(projectId),

    onSuccess: (_, projectId) => {
      queryClient.removeQueries({
        queryKey: candidateProjectQueryKeys.detail(projectId),
      });

      queryClient.invalidateQueries({
        queryKey: candidateProjectQueryKeys.list(),
      });
    },
  });
};
