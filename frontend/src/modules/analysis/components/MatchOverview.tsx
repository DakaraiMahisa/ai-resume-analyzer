import { Card } from "@/components/ui";
import type { ResumeAnalysisResponse } from "@/modules/analysis";

type MatchOverviewProps = {
  analysis: ResumeAnalysisResponse;
};

function formatScore(score: number): string {
  return `${Math.round(score * 100)}%`;
}

export function MatchOverview({ analysis }: MatchOverviewProps) {
  const { ats } = analysis;

  return (
    <Card className="overflow-hidden">
      <div className="border-b border-border px-6 py-5">
        <p className="text-xs font-semibold uppercase tracking-[0.14em] text-brand">
          Match overview
        </p>

        <h2 className="mt-1 text-lg font-semibold tracking-tight text-text-primary">
          Your resume match at a glance
        </h2>

        <p className="mt-1 text-sm leading-6 text-text-secondary">
          A summary of how closely your resume aligns with the target job
          requirements.
        </p>
      </div>

      <div className="grid divide-y divide-border sm:grid-cols-3 sm:divide-x sm:divide-y-0">
        <div className="px-6 py-6">
          <p className="text-sm font-medium text-text-secondary">
            Overall match
          </p>

          <p className="mt-2 font-mono text-4xl font-semibold tracking-tight text-text-primary">
            {formatScore(ats.overallScore)}
          </p>

          <p className="mt-2 text-sm text-text-tertiary">
            Across all evaluated requirements
          </p>
        </div>

        <div className="px-6 py-6">
          <p className="text-sm font-medium text-text-secondary">Required</p>

          <p className="mt-2 font-mono text-3xl font-semibold tracking-tight text-text-primary">
            {formatScore(ats.required.score)}
          </p>

          <p className="mt-2 text-sm text-text-tertiary">
            {ats.required.satisfiedCount} of {ats.required.totalCount} satisfied
          </p>
        </div>

        <div className="px-6 py-6">
          <p className="text-sm font-medium text-text-secondary">Preferred</p>

          <p className="mt-2 font-mono text-3xl font-semibold tracking-tight text-text-primary">
            {formatScore(ats.preferred.score)}
          </p>

          <p className="mt-2 text-sm text-text-tertiary">
            {ats.preferred.satisfiedCount} of {ats.preferred.totalCount}{" "}
            satisfied
          </p>
        </div>
      </div>
    </Card>
  );
}
