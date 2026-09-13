import authApiClient from "@/core/api/auth-axios";
import type { ApiResponse } from "@/core/api/api-response";

import type {
  LoginRequest,
  LoginResponse,
  LogoutRequest,
  RefreshTokenRequest,
  RefreshTokenResponse,
  RegisterRequest,
  RegisterResponse,
} from "../types/auth.types";

export const login = async (request: LoginRequest): Promise<LoginResponse> => {
  const response = await authApiClient.post<ApiResponse<LoginResponse>>(
    "/api/v1/auth/login/public",
    request,
  );

  if (!response.data.data) {
    throw new Error(response.data.message ?? "Login failed");
  }

  return response.data.data;
};

export const refresh = async (
  request: RefreshTokenRequest,
): Promise<RefreshTokenResponse> => {
  const response = await authApiClient.post<ApiResponse<RefreshTokenResponse>>(
    "/api/v1/auth/refresh/public",
    request,
  );

  if (!response.data.data) {
    throw new Error(response.data.message ?? "Token refresh failed");
  }

  return response.data.data;
};

export const logout = async (request: LogoutRequest): Promise<void> => {
  await authApiClient.post<ApiResponse<void>>(
    "/api/v1/auth/logout/public",
    request,
  );
};

export const register = async (
  request: RegisterRequest,
): Promise<RegisterResponse> => {
  const response = await authApiClient.post<ApiResponse<RegisterResponse>>(
    "/api/v1/auth/register/public",
    request,
  );

  if (!response.data.data) {
    throw new Error(response.data.message ?? "Registration failed");
  }

  return response.data.data;
};
