import { Link } from "react-router-dom";

import { LoadingState } from "@/components/feedback";
import { Card } from "@/components/ui";
import { useResume } from "@/modules/resume";
import type { ResumeAnalysisResponse } from "@/modules/analysis";

type AnalysisHeaderProps = {
  analysis: ResumeAnalysisResponse;
};

export function AnalysisHeader({ analysis }: AnalysisHeaderProps) {
  const { data: resume, isLoading: isResumeLoading } = useResume(
    analysis.resumeId,
  );

  if (isResumeLoading) {
    return <LoadingState message="Loading analysis details..." />;
  }

  return (
    <div className="space-y-5">
      <Link
        to="/app/analyze"
        className={[
          "inline-flex items-center gap-2 text-sm font-medium",
          "text-text-secondary transition-colors",
          "hover:text-text-primary",
        ].join(" ")}
      >
        <span aria-hidden="true">←</span>
        Back to analyze
      </Link>

      <div className="flex flex-col gap-6 lg:flex-row lg:items-end lg:justify-between">
        <div className="max-w-3xl">
          <p className="text-xs font-semibold uppercase tracking-[0.14em] text-brand">
            Resume match
          </p>

          <h1 className="mt-2 text-2xl font-semibold tracking-tight text-text-primary sm:text-3xl">
            How well your resume fits this job
          </h1>

          <p className="mt-2 max-w-2xl text-sm leading-6 text-text-secondary">
            Review the requirements you satisfy, the gaps holding your match
            back, and the improvements that can strengthen your application.
          </p>
        </div>

        <Card className="shrink-0 p-4 sm:min-w-72">
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-1">
            <div>
              <p className="text-xs font-medium uppercase tracking-wide text-text-tertiary">
                Resume
              </p>
              <p className="mt-1 truncate text-sm font-medium text-text-primary">
                {resume?.fileName ?? "Unavailable"}
              </p>
            </div>

            <div>
              <p className="text-xs font-medium uppercase tracking-wide text-text-tertiary">
                Target job
              </p>
              <p className="mt-1 truncate text-sm font-medium text-text-primary">
                {analysis.jobDescription.displayName ?? "Unavailable"}
              </p>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
}
