export const candidateCertificationQueryKeys = {
  all: ["candidate-certifications"] as const,

  lists: () => [...candidateCertificationQueryKeys.all, "list"] as const,

  list: () => [...candidateCertificationQueryKeys.lists()] as const,

  details: () => [...candidateCertificationQueryKeys.all, "detail"] as const,

  detail: (id: string) =>
    [...candidateCertificationQueryKeys.details(), id] as const,
};
