import { useMutation } from "@tanstack/react-query";

import { register } from "@/modules/auth/api/auth.api";

import type {
  RegisterRequest,
  RegisterResponse,
} from "@/modules/auth/types/auth.types";

export const useRegister = () => {
  return useMutation<RegisterResponse, Error, RegisterRequest>({
    mutationFn: register,
  });
};
