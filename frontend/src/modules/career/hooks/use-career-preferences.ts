import { useQuery } from "@tanstack/react-query";

import { preferenceApi } from "../api/preference.api";
import { candidateCareerPreferenceQueryKeys } from "./preference.query";

export const useCareerPreferences = () => {
  return useQuery({
    queryKey: candidateCareerPreferenceQueryKeys.current(),
    queryFn: preferenceApi.get,
  });
};
