import { useParams } from "react-router-dom";

import { ErrorState, LoadingState } from "@/components/feedback";
import {
  AnalysisHeader,
  MatchOverview,
  MatchSummary,
  Recommendations,
  RequirementCoverageSection,
  useAnalysis,
} from "@/modules/analysis";

export function AnalysisResultPage() {
  const { analysisId } = useParams<{ analysisId: string }>();

  const {
    data: analysis,
    isLoading,
    isError,
    refetch,
  } = useAnalysis(analysisId ?? "");

  if (isLoading) {
    return <LoadingState message="Loading analysis..." />;
  }

  if (isError || !analysis) {
    return (
      <ErrorState
        title="Unable to load analysis"
        description="We couldn't retrieve this analysis. It may no longer be available."
        actionLabel="Try again"
        onAction={() => void refetch()}
      />
    );
  }

  return (
    <div className="space-y-8">
      <AnalysisHeader analysis={analysis} />
      <MatchOverview analysis={analysis} />
      <MatchSummary analysis={analysis} />
      <RequirementCoverageSection analysis={analysis} />
      <Recommendations analysis={analysis} />
    </div>
  );
}
