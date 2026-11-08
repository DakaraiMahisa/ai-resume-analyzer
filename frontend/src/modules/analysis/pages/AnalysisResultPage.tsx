import { useParams } from "react-router-dom";

import { ErrorState, LoadingState } from "@/components/feedback";
import { Button, Card } from "@/components/ui";
import {
  AnalysisHeader,
  MatchOverview,
  MatchSummary,
  Recommendations,
  RequirementCoverageSection,
  useAnalysis,
  useDeleteAnalysis,
  useRetryAnalysis,
} from "@/modules/analysis";

export function AnalysisResultPage() {
  const { analysisId } = useParams<{ analysisId: string }>();

  const {
    data: analysis,
    isLoading,
    isError,
    refetch,
  } = useAnalysis(analysisId ?? "");
  const retryMutation = useRetryAnalysis();
  const deleteMutation = useDeleteAnalysis();

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

  if (analysis.status === "RUNNING") {
    return (
      <Card className="p-8 text-center">
        <LoadingState message="Your analysis is still running. This page will update automatically." />
        <Button
          variant="secondary"
          size="sm"
          className="mt-4"
          onClick={() => void refetch()}
        >
          Refresh status
        </Button>
      </Card>
    );
  }

  if (analysis.status === "FAILED") {
    return (
      <Card className="space-y-5 p-8">
        <div>
          <h1 className="text-xl font-semibold text-text-primary">
            Analysis could not be completed
          </h1>
          <p className="mt-2 text-sm leading-6 text-text-secondary">
            {analysis.error?.message ??
              "We couldn't complete this analysis. Please try again later."}
          </p>
        </div>
        <div className="flex flex-wrap gap-3">
          {analysis.error?.retryable && (
            <Button
              loading={retryMutation.isPending}
              onClick={() =>
                retryMutation.mutate(analysis.analysisId, {
                  onSuccess: (retry) =>
                    window.history.replaceState(
                      null,
                      "",
                      `/app/analyses/${retry.analysisId}`,
                    ),
                })
              }
            >
              Try again
            </Button>
          )}
          <Button
            variant="danger"
            loading={deleteMutation.isPending}
            onClick={() => {
              if (window.confirm("Delete this failed analysis?")) {
                deleteMutation.mutate(analysis.analysisId, {
                  onSuccess: () => window.history.back(),
                });
              }
            }}
          >
            Delete analysis
          </Button>
        </div>
      </Card>
    );
  }

  if (!analysis.result) {
    return (
      <ErrorState
        title="Analysis result unavailable"
        description="The analysis completed without a result. Please try again."
        actionLabel="Refresh"
        onAction={() => void refetch()}
      />
    );
  }

  return (
    <div className="space-y-8">
      <AnalysisHeader analysis={analysis.result} />
      <div className="flex justify-end">
        <Button
          variant="danger"
          size="sm"
          loading={deleteMutation.isPending}
          onClick={() => {
            if (window.confirm("Delete this analysis?")) {
              deleteMutation.mutate(analysis.analysisId, {
                onSuccess: () => window.history.back(),
              });
            }
          }}
        >
          Delete analysis
        </Button>
      </div>
      <MatchOverview analysis={analysis.result} />
      <MatchSummary analysis={analysis.result} />
      <RequirementCoverageSection analysis={analysis.result} />
      <Recommendations analysis={analysis.result} />
    </div>
  );
}
