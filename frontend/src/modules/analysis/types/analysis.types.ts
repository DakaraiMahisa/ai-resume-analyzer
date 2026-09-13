export type ClaimPriority = "REQUIRED" | "PREFERRED" | "NOT_APPLICABLE";
export type AnalysisStatus = "RUNNING" | "COMPLETED" | "FAILED";
export type RequirementOperator = "ATOMIC" | "ALL" | "ANY";

export type MatchRelationship =
  | "EXACT"
  | "EQUIVALENT"
  | "SATISFIES"
  | "RELATED"
  | "UNRELATED";

export type MatchingMethod = "CANONICAL" | "ALIAS" | "SEMANTIC";

export type Requirement = {
  value: string;
  operator: RequirementOperator;
  components: Requirement[];
  originalText: string;
};

export type RequirementComponent = {
  requirementClaimId: string;
  value: string;
};

export type RequirementGroup = {
  operator: RequirementOperator;
  expressions: RequirementExpression[];
};

export type RequirementExpression = RequirementComponent | RequirementGroup;

export type RequirementMatch = {
  requirementClaimId: string;
  requirementComponentValue: string;
  resumeClaimId: string;
  relationship: MatchRelationship;
  method: MatchingMethod;
};

export type RequirementCoverage = {
  totalComponents: number;
  matchedComponents: number;
};

export type RequirementEvaluation = {
  expression: RequirementExpression;
  matches: RequirementMatch[];
  coverage: RequirementCoverage;
};

export type RequirementMatchResult = {
  requirement: Requirement;
  priority: ClaimPriority;
  evaluation: RequirementEvaluation;
};

export type ATSScoreBreakdown = {
  score: number;
  satisfiedCount: number;
  totalCount: number;
};

export type ATSRequirementScore = {
  result: RequirementMatchResult;
  score: number;
  satisfied: boolean;
};

export type ATSResult = {
  overallScore: number;
  required: ATSScoreBreakdown;
  preferred: ATSScoreBreakdown;
  requirementScores: ATSRequirementScore[];
};

export type RecommendationResponse = {
  overallAssessment: string;
  strengths: string[];
  criticalGaps: string[];
  recommendations: string[];
};

export type ResumeAnalysisResponse = {
  analysisId: string;
  resumeId: string;
  jobDescription: {
    id: string;
    displayName: string;
  };
  requirementResults: RequirementMatchResult[];
  ats: ATSResult;
  recommendation: RecommendationResponse;
};

export type AnalysisSummaryResponse = {
  analysisId: string;
  resumeId: string;
  resumeDisplayName: string;
  jobDescriptionId: string;
  jobDescriptionDisplayName: string;
  status: AnalysisStatus;
  overallScore: number;
  createdAt: string;
  completedAt: string | null;
};

export type AnalysisDashboardSummaryResponse = {
  totalAnalyses: number;
  completedAnalyses: number;
  averageOverallScore: number;
};
