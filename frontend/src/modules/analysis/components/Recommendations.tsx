import { Card } from "@/components/ui";
import type { ResumeAnalysisResponse } from "@/modules/analysis/types/analysis.types";

type RecommendationsProps = {
  analysis: ResumeAnalysisResponse;
};

export function Recommendations({ analysis }: RecommendationsProps) {
  const { recommendation } = analysis;

  return (
    <Card className="overflow-hidden">
      <div className="border-b border-border px-6 py-5">
        <p className="text-xs font-semibold uppercase tracking-[0.14em] text-brand">
          Recommendations
        </p>

        <h2 className="mt-1 text-lg font-semibold tracking-tight text-text-primary">
          What you should improve
        </h2>

        <p className="mt-1 max-w-2xl text-sm leading-6 text-text-secondary">
          Practical changes you can make to strengthen your resume for this
          specific job.
        </p>
      </div>

      <div className="px-6 py-6">
        <div className="border-l-2 border-brand pl-4">
          <p className="text-sm font-medium text-text-primary">
            Overall assessment
          </p>

          <p className="mt-2 text-sm leading-6 text-text-secondary">
            {recommendation.overallAssessment}
          </p>
        </div>

        {recommendation.recommendations.length > 0 && (
          <div className="mt-8">
            <p className="text-sm font-semibold text-text-primary">
              Recommended improvements
            </p>

            <ol className="mt-4 space-y-4">
              {recommendation.recommendations.map(
                (recommendationText, index) => (
                  <li key={recommendationText} className="flex gap-4">
                    <span
                      className={[
                        "flex h-7 w-7 shrink-0 items-center justify-center",
                        "rounded-full border border-border",
                        "bg-surface-muted text-xs font-semibold",
                        "text-text-secondary",
                      ].join(" ")}
                      aria-hidden="true"
                    >
                      {index + 1}
                    </span>

                    <p className="pt-0.5 text-sm leading-6 text-text-secondary">
                      {recommendationText}
                    </p>
                  </li>
                ),
              )}
            </ol>
          </div>
        )}

        {recommendation.recommendations.length === 0 && (
          <div className="mt-8 rounded-md border border-border bg-surface-muted/40 px-4 py-4">
            <p className="text-sm text-text-secondary">
              No additional improvements were identified for this analysis.
            </p>
          </div>
        )}
      </div>
    </Card>
  );
}
