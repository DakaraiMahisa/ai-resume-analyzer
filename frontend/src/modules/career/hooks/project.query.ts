export const candidateProjectQueryKeys = {
  all: ["candidate-projects"] as const,

  lists: () => [...candidateProjectQueryKeys.all, "list"] as const,

  list: () => [...candidateProjectQueryKeys.lists()] as const,

  details: () => [...candidateProjectQueryKeys.all, "detail"] as const,

  detail: (projectId: string) =>
    [...candidateProjectQueryKeys.details(), projectId] as const,
};
