import { useMutation } from "@tanstack/react-query";

import { useAuthStore } from "@/core/auth/auth-store";
import { tokenStorage } from "@/core/auth/token-storage";
import { logout } from "@/modules/auth/api/auth.api";

export const useLogout = () => {
  const clearAuthentication = useAuthStore(
    (state) => state.clearAuthentication,
  );

  return useMutation({
    mutationFn: async () => {
      const refreshToken = tokenStorage.getRefreshToken();

      if (!refreshToken) {
        return;
      }

      await logout({
        refreshToken,
      });
    },

    onSettled: () => {
      clearAuthentication();
    },
  });
};
