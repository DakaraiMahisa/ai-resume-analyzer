import type {
  AxiosError,
  AxiosInstance,
  InternalAxiosRequestConfig,
} from "axios";

import { tokenStorage } from "@/core/auth/token-storage";

type RefreshAccessToken = (refreshToken: string) => Promise<{
  accessToken: string;
  refreshToken: string;
}>;

type RetryableRequestConfig = InternalAxiosRequestConfig & {
  _retry?: boolean;
};

export const configureAuthInterceptor = (
  client: AxiosInstance,
  refreshAccessToken: RefreshAccessToken,
): void => {
  client.interceptors.request.use((config) => {
    const accessToken = tokenStorage.getAccessToken();

    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }

    return config;
  });

  client.interceptors.response.use(
    (response) => response,
    async (error: AxiosError) => {
      const originalRequest = error.config as
        | RetryableRequestConfig
        | undefined;

      if (
        error.response?.status !== 401 ||
        !originalRequest ||
        originalRequest._retry
      ) {
        return Promise.reject(error);
      }

      originalRequest._retry = true;

      const refreshToken = tokenStorage.getRefreshToken();

      if (!refreshToken) {
        tokenStorage.clear();
        return Promise.reject(error);
      }

      try {
        const response = await refreshAccessToken(refreshToken);

        tokenStorage.setAccessToken(response.accessToken);
        tokenStorage.setRefreshToken(response.refreshToken);

        originalRequest.headers.Authorization = `Bearer ${response.accessToken}`;

        return client(originalRequest);
      } catch (refreshError) {
        tokenStorage.clear();

        return Promise.reject(refreshError);
      }
    },
  );
};
