import { useQuery } from "@tanstack/react-query";

import { achievementApi } from "../api/achievement.api";
import { candidateAchievementQueryKeys } from "./achievement.query";

export const useAchievements = () => {
  return useQuery({
    queryKey: candidateAchievementQueryKeys.list(),
    queryFn: achievementApi.getAll,
  });
};
