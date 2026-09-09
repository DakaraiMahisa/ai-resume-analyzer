import authApiClient from "@/core/api/auth-axios";

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
  const response = await authApiClient.post<LoginResponse>(
    "/api/v1/auth/login/public",
    request,
  );

  return response.data;
};

export const refresh = async (
  request: RefreshTokenRequest,
): Promise<RefreshTokenResponse> => {
  const response = await authApiClient.post<RefreshTokenResponse>(
    "/api/v1/auth/refresh/public",
    request,
  );

  return response.data;
};

export const logout = async (request: LogoutRequest): Promise<void> => {
  await authApiClient.post("/api/v1/auth/logout/public", request);
};

export const register = async (
  request: RegisterRequest,
): Promise<RegisterResponse> => {
  const response = await authApiClient.post<RegisterResponse>(
    "/api/v1/auth/register/public",
    request,
  );

  return response.data;
};
