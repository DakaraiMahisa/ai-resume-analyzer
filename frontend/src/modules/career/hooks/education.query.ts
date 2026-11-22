export const educationQueryKeys = {
  all: ["education"] as const,
  lists: () => [...educationQueryKeys.all, "list"] as const,
  list: () => [...educationQueryKeys.lists()] as const,
  details: () => [...educationQueryKeys.all, "detail"] as const,
  detail: (educationId: string) =>
    [...educationQueryKeys.details(), educationId] as const,
};
