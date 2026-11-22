import { useQuery } from "@tanstack/react-query";
import { projectApi } from "../api/project.api";
import { candidateProjectQueryKeys } from "./project.query";

export const useProjects = () => {
  return useQuery({
    queryKey: candidateProjectQueryKeys.list(),
    queryFn: projectApi.getAll,
  });
};

export const useProject = (projectId: string) => {
  return useQuery({
    queryKey: candidateProjectQueryKeys.detail(projectId),
    queryFn: () => projectApi.getById(projectId),
    enabled: Boolean(projectId),
  });
};
