import { useQuery } from "@tanstack/react-query";

import { getCurrentUser } from "@/modules/user/api/user.api";

export const currentUserQueryKeys = {
  all: ["current-user"] as const,
};

export const useCurrentUser = () => {
  return useQuery({
    queryKey: currentUserQueryKeys.all,
    queryFn: getCurrentUser,
  });
};
