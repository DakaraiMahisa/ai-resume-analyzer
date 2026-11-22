export const candidateLanguageQueryKeys = {
  all: ["candidate-languages"] as const,

  lists: () => [...candidateLanguageQueryKeys.all, "list"] as const,

  list: () => [...candidateLanguageQueryKeys.lists()] as const,

  details: () => [...candidateLanguageQueryKeys.all, "detail"] as const,

  detail: (id: string) =>
    [...candidateLanguageQueryKeys.details(), id] as const,
};
