import { useQuery } from "@tanstack/react-query";

import { skillApi } from "@/modules/career/api/skill.api";
import { candidateSkillQueryKeys } from "@/modules/career/hooks/skill.query";

export function useSkills() {
  return useQuery({
    queryKey: candidateSkillQueryKeys.list(),
    queryFn: skillApi.getAll,
  });
}

export function useSkill(skillId: string) {
  return useQuery({
    queryKey: candidateSkillQueryKeys.detail(skillId),
    queryFn: () => skillApi.getById(skillId),
    enabled: Boolean(skillId),
  });
}
