import { Card } from "@/components/ui";
import type { ResumeAnalysisResponse } from "@/modules/analysis";

type MatchSummaryProps = {
  analysis: ResumeAnalysisResponse;
};

export function MatchSummary({ analysis }: MatchSummaryProps) {
  const { recommendation } = analysis;

  return (
    <div className="grid gap-6 lg:grid-cols-2">
      <Card className="p-6">
        <div className="flex items-start gap-3">
          <div
            className="mt-0.5 flex h-8 w-8 shrink-0 items-center justify-center rounded-full border border-success/20 bg-success/5 text-success"
            aria-hidden="true"
          >
            ✓
          </div>

          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.14em] text-success">
              Strengths
            </p>

            <h2 className="mt-1 text-lg font-semibold tracking-tight text-text-primary">
              What you're doing well
            </h2>
          </div>
        </div>

        <ul className="mt-6 space-y-4">
          {recommendation.strengths.map((strength) => (
            <li
              key={strength}
              className="flex gap-3 text-sm leading-6 text-text-secondary"
            >
              <span
                className="mt-2 h-1.5 w-1.5 shrink-0 rounded-full bg-success"
                aria-hidden="true"
              />

              <span>{strength}</span>
            </li>
          ))}
        </ul>
      </Card>

      <Card className="p-6">
        <div className="flex items-start gap-3">
          <div
            className="mt-0.5 flex h-8 w-8 shrink-0 items-center justify-center rounded-full border border-warning/20 bg-warning/5 text-warning"
            aria-hidden="true"
          >
            !
          </div>

          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.14em] text-warning">
              Critical gaps
            </p>

            <h2 className="mt-1 text-lg font-semibold tracking-tight text-text-primary">
              What's holding your match back
            </h2>
          </div>
        </div>

        <ul className="mt-6 space-y-4">
          {recommendation.criticalGaps.map((gap) => (
            <li
              key={gap}
              className="flex gap-3 text-sm leading-6 text-text-secondary"
            >
              <span
                className="mt-2 h-1.5 w-1.5 shrink-0 rounded-full bg-warning"
                aria-hidden="true"
              />

              <span>{gap}</span>
            </li>
          ))}
        </ul>
      </Card>
    </div>
  );
}
