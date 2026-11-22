export const professionalExperienceQueryKeys = {
  all: ["professional-experiences"] as const,

  lists: () => [...professionalExperienceQueryKeys.all, "list"] as const,

  list: () => [...professionalExperienceQueryKeys.lists()] as const,

  details: () => [...professionalExperienceQueryKeys.all, "detail"] as const,

  detail: (experienceId: string) =>
    [...professionalExperienceQueryKeys.details(), experienceId] as const,
};
