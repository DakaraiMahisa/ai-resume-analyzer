import apiClient from "@/core/api/axios";
import type { ApiResponse } from "@/core/api/api-response";

import type { CurrentUserResponse } from "@/modules/user/types/user.types";

export const getCurrentUser = async (): Promise<CurrentUserResponse> => {
  const response =
    await apiClient.get<ApiResponse<CurrentUserResponse>>("/api/v1/users/me");

  if (!response.data.data) {
    throw new Error(
      response.data.message ?? "Current user could not be retrieved",
    );
  }

  return response.data.data;
};
