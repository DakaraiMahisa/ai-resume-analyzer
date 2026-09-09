import { create } from "zustand";

import { tokenStorage } from "@/core/auth/token-storage";

type AuthState = {
  isInitializing: boolean;
  isAuthenticated: boolean;
  accessToken: string | null;

  setAuthenticated: (accessToken: string, refreshToken: string) => void;

  clearAuthentication: () => void;

  setInitializing: (isInitializing: boolean) => void;
};

export const useAuthStore = create<AuthState>((set) => ({
  isInitializing: true,
  isAuthenticated: Boolean(tokenStorage.getRefreshToken()),
  accessToken: tokenStorage.getAccessToken(),

  setAuthenticated: (accessToken, refreshToken) => {
    tokenStorage.setAccessToken(accessToken);
    tokenStorage.setRefreshToken(refreshToken);

    set({
      isAuthenticated: true,
      accessToken,
      isInitializing: false,
    });
  },

  clearAuthentication: () => {
    tokenStorage.clear();

    set({
      isAuthenticated: false,
      accessToken: null,
      isInitializing: false,
    });
  },

  setInitializing: (isInitializing) => {
    set({
      isInitializing,
    });
  },
}));
