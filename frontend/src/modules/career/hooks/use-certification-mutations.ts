import { useMutation, useQueryClient } from "@tanstack/react-query";

import { certificationApi } from "../api/certification.api";
import { candidateCertificationQueryKeys } from "./certification.query";

export function useCreateCertification() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: certificationApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: candidateCertificationQueryKeys.list(),
      });
    },
  });
}

export function useUpdateCertification() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      id,
      request,
    }: {
      id: string;
      request: Parameters<typeof certificationApi.update>[1];
    }) => certificationApi.update(id, request),

    onSuccess: (certification) => {
      queryClient.setQueryData(
        candidateCertificationQueryKeys.detail(certification.id),
        certification,
      );

      queryClient.invalidateQueries({
        queryKey: candidateCertificationQueryKeys.list(),
      });
    },
  });
}

export function useDeleteCertification() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: certificationApi.delete,
    onSuccess: (_, id) => {
      queryClient.removeQueries({
        queryKey: candidateCertificationQueryKeys.detail(id),
      });

      queryClient.invalidateQueries({
        queryKey: candidateCertificationQueryKeys.list(),
      });
    },
  });
}
