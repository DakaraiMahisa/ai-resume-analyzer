import { useQuery } from "@tanstack/react-query";

import {
  getCandidateProfile,
  getCandidateProfileCompletion,
} from "@/modules/career/api/profile.api";

export const candidateProfileQueryKeys = {
  all: ["career-profile"] as const,
  profile: () => [...candidateProfileQueryKeys.all, "details"] as const,
  completion: () => [...candidateProfileQueryKeys.all, "completion"] as const,
};

export const useCandidateProfile = () => {
  return useQuery({
    queryKey: candidateProfileQueryKeys.profile(),
    queryFn: getCandidateProfile,
  });
};

export const useCandidateProfileCompletion = () => {
  return useQuery({
    queryKey: candidateProfileQueryKeys.completion(),
    queryFn: getCandidateProfileCompletion,
  });
};
