import apiClient from "@/core/api/axios";
import { configureAuthInterceptor } from "@/core/api/auth-interceptor";
import { refresh } from "@/modules/auth/api/auth.api";

export const configureAuth = (): void => {
  configureAuthInterceptor(apiClient, async (refreshToken) => {
    const response = await refresh({ refreshToken });

    return {
      accessToken: response.accessToken,
      refreshToken: response.refreshToken,
    };
  });
};
