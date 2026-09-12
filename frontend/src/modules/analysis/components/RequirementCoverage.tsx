import { Badge, Card } from "@/components/ui";
import type {
  ClaimPriority,
  MatchRelationship,
  RequirementMatchResult,
  ResumeAnalysisResponse,
} from "@/modules/analysis";

type RequirementCoverageProps = {
  analysis: ResumeAnalysisResponse;
};

function getPriorityLabel(priority: ClaimPriority): string {
  switch (priority) {
    case "REQUIRED":
      return "Required";
    case "PREFERRED":
      return "Preferred";
    default:
      return "Other";
  }
}

function getPriorityVariant(
  priority: ClaimPriority,
): "danger" | "info" | "default" {
  switch (priority) {
    case "REQUIRED":
      return "danger";
    case "PREFERRED":
      return "info";
    default:
      return "default";
  }
}

function getRelationshipLabel(relationship: MatchRelationship): string {
  switch (relationship) {
    case "EXACT":
      return "Exact match";
    case "EQUIVALENT":
      return "Equivalent";
    case "SATISFIES":
      return "Satisfies";
    case "RELATED":
      return "Related";
    case "UNRELATED":
      return "Not related";
  }
}

function getRequirementLabel(result: RequirementMatchResult): string {
  return result.requirement.value || result.requirement.originalText;
}

export function RequirementCoverageSection({
  analysis,
}: RequirementCoverageProps) {
  const results = analysis.requirementResults;

  const requiredResults = results.filter(
    (result) => result.priority === "REQUIRED",
  );

  const preferredResults = results.filter(
    (result) => result.priority === "PREFERRED",
  );

  return (
    <Card className="overflow-hidden">
      <div className="border-b border-border px-6 py-5">
        <p className="text-xs font-semibold uppercase tracking-[0.14em] text-brand">
          Requirement coverage
        </p>

        <h2 className="mt-1 text-lg font-semibold tracking-tight text-text-primary">
          How your resume matches the job requirements
        </h2>

        <p className="mt-1 max-w-2xl text-sm leading-6 text-text-secondary">
          See which requirements your resume demonstrates and where evidence is
          missing or incomplete.
        </p>
      </div>

      <div>
        {requiredResults.length > 0 && (
          <RequirementGroup
            title="Required requirements"
            results={requiredResults}
          />
        )}

        {preferredResults.length > 0 && (
          <RequirementGroup
            title="Preferred requirements"
            results={preferredResults}
          />
        )}

        {results.length === 0 && (
          <div className="px-6 py-8 text-sm text-text-secondary">
            No requirements were available for this analysis.
          </div>
        )}
      </div>
    </Card>
  );
}

type RequirementGroupProps = {
  title: string;
  results: RequirementMatchResult[];
};

function RequirementGroup({ title, results }: RequirementGroupProps) {
  return (
    <section>
      <div className="border-b border-border bg-surface-muted/40 px-6 py-3">
        <h3 className="text-sm font-semibold text-text-primary">{title}</h3>
      </div>

      <div className="divide-y divide-border">
        {results.map((result) => (
          <RequirementRow
            key={`${result.priority}-${result.requirement.originalText}`}
            result={result}
          />
        ))}
      </div>
    </section>
  );
}

type RequirementRowProps = {
  result: RequirementMatchResult;
};

function RequirementRow({ result }: RequirementRowProps) {
  const { evaluation } = result;
  const { coverage, matches } = evaluation;

  const isSatisfied =
    coverage.totalComponents > 0 &&
    coverage.matchedComponents === coverage.totalComponents;

  const hasPartialCoverage =
    coverage.matchedComponents > 0 &&
    coverage.matchedComponents < coverage.totalComponents;

  const relationship = matches[0]?.relationship;

  return (
    <div className="px-6 py-5">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
        <div className="min-w-0">
          <div className="flex flex-wrap items-center gap-2">
            <Badge variant={getPriorityVariant(result.priority)}>
              {getPriorityLabel(result.priority)}
            </Badge>

            <span
              className={[
                "text-xs font-medium",
                isSatisfied
                  ? "text-success"
                  : hasPartialCoverage
                    ? "text-warning"
                    : "text-text-tertiary",
              ].join(" ")}
            >
              {isSatisfied
                ? "Satisfied"
                : hasPartialCoverage
                  ? "Partially matched"
                  : "Not covered"}
            </span>
          </div>

          <p className="mt-3 text-sm font-medium leading-6 text-text-primary">
            {getRequirementLabel(result)}
          </p>

          {result.requirement.originalText !== result.requirement.value && (
            <p className="mt-1 text-xs leading-5 text-text-tertiary">
              From: {result.requirement.originalText}
            </p>
          )}
        </div>

        <div className="shrink-0 sm:text-right">
          <p className="font-mono text-sm font-semibold text-text-primary">
            {coverage.matchedComponents} / {coverage.totalComponents}
          </p>

          <p className="mt-1 text-xs text-text-tertiary">components covered</p>
        </div>
      </div>

      {relationship && (
        <div className="mt-4 flex items-center gap-2 border-t border-border pt-3">
          <span className="text-xs text-text-tertiary">Match evidence</span>

          <span className="text-xs font-medium text-text-secondary">
            {getRelationshipLabel(relationship)}
          </span>
        </div>
      )}
    </div>
  );
}
