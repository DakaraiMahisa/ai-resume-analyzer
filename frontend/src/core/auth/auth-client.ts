import {
  type AxiosError,
  type AxiosInstance,
  type AxiosRequestConfig,
} from "axios";

import { refresh } from "@/modules/auth/api/auth.api";
import { tokenStorage } from "@/core/auth/token-storage";

type RetryableRequestConfig = AxiosRequestConfig & {
  _retry?: boolean;
};

let refreshPromise: Promise<string> | null = null;

const createRefreshPromise = async (): Promise<string> => {
  const refreshToken = tokenStorage.getRefreshToken();

  if (!refreshToken) {
    throw new Error("No refresh token available");
  }

  const response = await refresh({
    refreshToken,
  });

  tokenStorage.setAccessToken(response.accessToken);
  tokenStorage.setRefreshToken(response.refreshToken);

  return response.accessToken;
};

const getAccessToken = async (): Promise<string> => {
  if (!refreshPromise) {
    refreshPromise = createRefreshPromise().finally(() => {
      refreshPromise = null;
    });
  }

  return refreshPromise;
};

export const configureAuthInterceptors = (apiClient: AxiosInstance): void => {
  apiClient.interceptors.request.use((config) => {
    const accessToken = tokenStorage.getAccessToken();

    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }

    return config;
  });

  apiClient.interceptors.response.use(
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

      try {
        const accessToken = await getAccessToken();

        originalRequest.headers = {
          ...originalRequest.headers,
          Authorization: `Bearer ${accessToken}`,
        };

        return apiClient(originalRequest);
      } catch (refreshError) {
        tokenStorage.clear();

        return Promise.reject(refreshError);
      }
    },
  );
};
