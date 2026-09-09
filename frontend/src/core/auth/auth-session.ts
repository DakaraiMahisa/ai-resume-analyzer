import { refresh } from "@/modules/auth/api/auth.api";
import { useAuthStore } from "@/core/auth/auth-store";
import { tokenStorage } from "@/core/auth/token-storage";

export const restoreAuthSession = async (): Promise<void> => {
  const refreshToken = tokenStorage.getRefreshToken();

  if (!refreshToken) {
    useAuthStore.getState().clearAuthentication();
    return;
  }

  try {
    const response = await refresh({ refreshToken });

    useAuthStore
      .getState()
      .setAuthenticated(response.accessToken, response.refreshToken);
  } catch {
    useAuthStore.getState().clearAuthentication();
  }
};
