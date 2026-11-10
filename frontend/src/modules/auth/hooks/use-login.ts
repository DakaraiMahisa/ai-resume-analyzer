import { useMutation, useQueryClient } from "@tanstack/react-query";

import { useAuthStore } from "@/core/auth/auth-store";
import { login } from "@/modules/auth/api/auth.api";

import type {
  LoginRequest,
  LoginResponse,
} from "@/modules/auth/types/auth.types";

export const useLogin = () => {
  const queryClient = useQueryClient();

  const setAuthenticated = useAuthStore((state) => state.setAuthenticated);

  return useMutation<LoginResponse, Error, LoginRequest>({
    mutationFn: login,

    onSuccess: (response) => {
      queryClient.clear();

      setAuthenticated(response.accessToken, response.refreshToken);
    },
  });
};
