import { useMutation, useQueryClient } from "@tanstack/react-query";

import { languageApi } from "../api/language.api";
import { candidateLanguageQueryKeys } from "./language.query";

export function useCreateLanguage() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: languageApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: candidateLanguageQueryKeys.list(),
      });
    },
  });
}

export function useUpdateLanguage() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      id,
      request,
    }: {
      id: string;
      request: Parameters<typeof languageApi.update>[1];
    }) => languageApi.update(id, request),

    onSuccess: (language) => {
      queryClient.setQueryData(
        candidateLanguageQueryKeys.detail(language.id),
        language,
      );

      queryClient.invalidateQueries({
        queryKey: candidateLanguageQueryKeys.list(),
      });
    },
  });
}

export function useDeleteLanguage() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: languageApi.delete,
    onSuccess: (_, id) => {
      queryClient.removeQueries({
        queryKey: candidateLanguageQueryKeys.detail(id),
      });

      queryClient.invalidateQueries({
        queryKey: candidateLanguageQueryKeys.list(),
      });
    },
  });
}
