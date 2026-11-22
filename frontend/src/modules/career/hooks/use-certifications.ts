import { useQuery } from "@tanstack/react-query";
import { certificationApi } from "../api/certification.api";
import { candidateCertificationQueryKeys } from "./certification.query";

export function useCertifications() {
  return useQuery({
    queryKey: candidateCertificationQueryKeys.list(),
    queryFn: certificationApi.getAll,
  });
}
