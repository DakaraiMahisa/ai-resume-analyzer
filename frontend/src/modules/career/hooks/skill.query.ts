export const candidateSkillQueryKeys = {
  all: ["candidate-skills"] as const,

  lists: () => [...candidateSkillQueryKeys.all, "list"] as const,

  list: () => [...candidateSkillQueryKeys.lists()] as const,

  details: () => [...candidateSkillQueryKeys.all, "detail"] as const,

  detail: (skillId: string) =>
    [...candidateSkillQueryKeys.details(), skillId] as const,
};
