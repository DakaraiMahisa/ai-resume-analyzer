export const candidateAchievementQueryKeys = {
  all: ["candidate-achievements"] as const,

  lists: () => [...candidateAchievementQueryKeys.all, "list"] as const,

  list: () => [...candidateAchievementQueryKeys.lists()] as const,

  details: () => [...candidateAchievementQueryKeys.all, "detail"] as const,

  detail: (id: string) =>
    [...candidateAchievementQueryKeys.details(), id] as const,
};
