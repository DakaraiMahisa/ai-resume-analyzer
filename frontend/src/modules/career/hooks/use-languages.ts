import { useQuery } from "@tanstack/react-query";

import { languageApi } from "../api/language.api";
import { candidateLanguageQueryKeys } from "./language.query";

export function useLanguages() {
  return useQuery({
    queryKey: candidateLanguageQueryKeys.list(),
    queryFn: languageApi.getAll,
  });
}
