export const candidateCareerPreferenceQueryKeys = {
  all: ["candidate-career-preferences"] as const,

  current: () =>
    [...candidateCareerPreferenceQueryKeys.all, "current"] as const,
};
