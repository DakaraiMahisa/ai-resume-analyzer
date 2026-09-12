import { Link } from "react-router-dom";
import { useState } from "react";
import { ErrorState, LoadingState } from "@/components/feedback";
import { Badge, Card } from "@/components/ui";
import {
  useAnalysisDashboardSummary,
  useRecentAnalyses,
} from "@/modules/analysis";

function formatScore(score: number) {
  return `${Math.round(score * 100)}%`;
}

function formatDate(date: string) {
  return new Intl.DateTimeFormat("en", {
    month: "short",
    day: "numeric",
    year: "numeric",
  }).format(new Date(date));
}

function getStatusVariant(status: "RUNNING" | "COMPLETED" | "FAILED") {
  switch (status) {
    case "COMPLETED":
      return "success";

    case "RUNNING":
      return "info";

    case "FAILED":
      return "danger";

    default:
      return "default";
  }
}

function getStatusLabel(status: "RUNNING" | "COMPLETED" | "FAILED") {
  switch (status) {
    case "COMPLETED":
      return "Completed";

    case "RUNNING":
      return "Running";

    case "FAILED":
      return "Failed";

    default:
      return status;
  }
}

export function AnalysesPage() {
  const [page, setPage] = useState(0);

  const {
    data: analyses,
    isLoading: isAnalysesLoading,
    isError: isAnalysesError,
    refetch: refetchAnalyses,
  } = useRecentAnalyses(page, 5);

  const {
    data: summary,
    isLoading: isSummaryLoading,
    isError: isSummaryError,
    refetch: refetchSummary,
  } = useAnalysisDashboardSummary();

  if (isAnalysesLoading || isSummaryLoading) {
    return <LoadingState message="Loading analyses..." />;
  }

  if (isAnalysesError || isSummaryError) {
    return (
      <ErrorState
        title="Unable to load analyses"
        description="We couldn't retrieve your analysis history."
        actionLabel="Try again"
        onAction={() => {
          void refetchAnalyses();
          void refetchSummary();
        }}
      />
    );
  }

  const hasAnalyses = Boolean(analyses?.analyses.length);

  return (
    <div className="space-y-8">
      <header className="flex flex-col gap-5 sm:flex-row sm:items-end sm:justify-between">
        <div className="max-w-2xl">
          <p className="text-sm font-medium text-brand">Analysis history</p>

          <h1 className="mt-2 text-2xl font-semibold tracking-tight text-text-primary sm:text-3xl">
            Your resume-to-job matches
          </h1>

          <p className="mt-2 text-sm leading-6 text-text-secondary sm:text-base">
            Review your previous matches, understand your fit, and track how
            your resumes perform across different roles.
          </p>
        </div>

        <Link
          to="/app/analyze"
          className={[
            "inline-flex shrink-0 items-center justify-center",
            "rounded-md bg-brand px-4 py-2.5",
            "text-sm font-medium text-text-inverse",
            "transition-colors hover:bg-brand-hover",
            "focus-visible:outline-none",
            "focus-visible:ring-2 focus-visible:ring-brand/30",
            "focus-visible:ring-offset-2",
            "focus-visible:ring-offset-background",
          ].join(" ")}
        >
          Analyze a resume
        </Link>
      </header>

      <section
        aria-label="Analysis summary"
        className="grid gap-4 sm:grid-cols-3"
      >
        <Card className="p-5">
          <p className="text-sm text-text-secondary">Total analyses</p>

          <p className="mt-2 text-2xl font-semibold text-text-primary">
            {summary?.totalAnalyses ?? 0}
          </p>
        </Card>

        <Card className="p-5">
          <p className="text-sm text-text-secondary">Completed</p>

          <p className="mt-2 text-2xl font-semibold text-text-primary">
            {summary?.completedAnalyses ?? 0}
          </p>
        </Card>

        <Card className="p-5">
          <p className="text-sm text-text-secondary">Average match</p>

          <p className="mt-2 text-2xl font-semibold text-text-primary">
            {formatScore(summary?.averageOverallScore ?? 0)}
          </p>
        </Card>
      </section>

      <section>
        <div className="mb-4 flex items-end justify-between gap-4">
          <div>
            <h2 className="text-lg font-semibold text-text-primary">
              Recent analyses
            </h2>

            <p className="mt-1 text-sm text-text-secondary">
              Your latest resume-to-job comparisons.
            </p>
          </div>

          <Link
            to="/app/analyze"
            className="hidden text-sm font-medium text-brand hover:text-brand-hover sm:inline"
          >
            Analyze a resume
          </Link>
        </div>

        {!hasAnalyses ? (
          <Card className="p-8 text-center">
            <p className="text-sm font-medium text-text-primary">
              No analyses yet
            </p>

            <p className="mx-auto mt-1 max-w-md text-sm leading-6 text-text-secondary">
              Analyze a resume against a target job to see your match score,
              strengths, gaps, and recommendations.
            </p>
          </Card>
        ) : (
          <Card className="overflow-hidden">
            <div className="divide-y divide-border">
              {analyses?.analyses.map((analysis) => (
                <Link
                  key={analysis.analysisId}
                  to={`/app/analyses/${analysis.analysisId}`}
                  className={[
                    "group block px-5 py-5 transition-colors",
                    "hover:bg-surface-muted/50",
                    "focus-visible:outline-none",
                    "focus-visible:ring-2 focus-visible:ring-inset",
                    "focus-visible:ring-brand/30",
                  ].join(" ")}
                >
                  <div className="flex flex-col gap-5 lg:flex-row lg:items-center lg:justify-between">
                    <div className="min-w-0">
                      <div className="flex flex-wrap items-center gap-2">
                        <h3 className="truncate text-sm font-semibold text-text-primary">
                          {analysis.resumeDisplayName}
                        </h3>

                        <Badge variant={getStatusVariant(analysis.status)}>
                          {getStatusLabel(analysis.status)}
                        </Badge>
                      </div>

                      <div className="mt-2 flex min-w-0 items-center gap-2 text-sm">
                        <span className="shrink-0 text-text-tertiary">
                          Target job
                        </span>

                        <span className="truncate font-medium text-text-secondary">
                          {analysis.jobDescriptionDisplayName}
                        </span>
                      </div>

                      <p className="mt-2 text-xs text-text-tertiary">
                        Analyzed {formatDate(analysis.createdAt)}
                      </p>
                    </div>

                    <div className="flex shrink-0 items-center justify-between gap-6 lg:justify-end">
                      <div className="text-left lg:text-right">
                        <p className="text-xs font-medium uppercase tracking-wide text-text-tertiary">
                          Match
                        </p>

                        <p className="mt-1 text-2xl font-semibold tracking-tight text-text-primary">
                          {formatScore(analysis.overallScore)}
                        </p>
                      </div>

                      <span
                        aria-hidden="true"
                        className="text-lg text-text-tertiary transition-transform group-hover:translate-x-0.5"
                      >
                        →
                      </span>
                    </div>
                  </div>
                </Link>
              ))}
            </div>
          </Card>
        )}
        {analyses?.pagination && analyses.pagination.totalPages > 1 && (
          <div className="mt-4 flex items-center justify-between border-t border-border pt-4">
            <p className="text-sm text-text-secondary">
              Page {analyses.pagination.page + 1} of{" "}
              {analyses.pagination.totalPages}
            </p>

            <div className="flex items-center gap-2">
              <button
                type="button"
                disabled={page === 0}
                onClick={() => setPage((current) => current - 1)}
                className={[
                  "rounded-md border border-border px-3 py-2",
                  "text-sm font-medium text-text-primary",
                  "transition-colors hover:bg-surface-muted",
                  "disabled:cursor-not-allowed disabled:opacity-40",
                ].join(" ")}
              >
                Previous
              </button>

              <button
                type="button"
                disabled={page >= analyses.pagination.totalPages - 1}
                onClick={() => setPage((current) => current + 1)}
                className={[
                  "rounded-md border border-border px-3 py-2",
                  "text-sm font-medium text-text-primary",
                  "transition-colors hover:bg-surface-muted",
                  "disabled:cursor-not-allowed disabled:opacity-40",
                ].join(" ")}
              >
                Next
              </button>
            </div>
          </div>
        )}
      </section>
    </div>
  );
}
