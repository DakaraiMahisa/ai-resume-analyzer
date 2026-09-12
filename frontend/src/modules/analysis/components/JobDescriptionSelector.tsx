import { LoadingState } from "@/components/feedback";
import { Card } from "@/components/ui";
import { JobDescriptionStatusBadge, useJobDescriptions } from "@/modules/job";
import type { JobDescriptionSummaryResponse } from "@/modules/job/types/job-description.types";

type JobDescriptionSelectorProps = {
  selectedJobDescriptionId: string | null;
  onSelect: (jobDescription: JobDescriptionSummaryResponse) => void;
};

export function JobDescriptionSelector({
  selectedJobDescriptionId,
  onSelect,
}: JobDescriptionSelectorProps) {
  const { data: jobDescriptions, isLoading, isError } = useJobDescriptions();

  if (isLoading) {
    return <LoadingState message="Loading job descriptions..." />;
  }

  if (isError) {
    return (
      <Card className="p-5">
        <p className="text-sm text-danger">
          Unable to load your job descriptions.
        </p>
      </Card>
    );
  }

  const readyJobDescriptions =
    jobDescriptions?.filter(
      (jobDescription) => jobDescription.status === "COMPLETED",
    ) ?? [];

  if (readyJobDescriptions.length === 0) {
    return (
      <Card className="border-dashed p-5">
        <h3 className="text-sm font-semibold text-text-primary">
          No ready job descriptions
        </h3>

        <p className="mt-1 text-sm leading-6 text-text-secondary">
          Add and process a job description before starting an analysis.
        </p>
      </Card>
    );
  }

  return (
    <div className="space-y-3">
      {readyJobDescriptions.map((jobDescription) => {
        const selected = jobDescription.id === selectedJobDescriptionId;

        return (
          <button
            key={jobDescription.id}
            type="button"
            onClick={() => onSelect(jobDescription)}
            className={[
              "w-full rounded-md border p-4 text-left transition-colors",
              "focus-visible:outline-none focus-visible:ring-2",
              "focus-visible:ring-brand/30",
              selected
                ? "border-brand bg-brand/5"
                : "border-border bg-surface hover:bg-surface-muted",
            ].join(" ")}
            aria-pressed={selected}
          >
            <div className="flex items-start justify-between gap-4">
              <div className="min-w-0">
                <p className="truncate text-sm font-semibold text-text-primary">
                  {jobDescription.fileName}
                </p>

                <p className="mt-1 text-xs text-text-tertiary">
                  Added{" "}
                  {new Date(jobDescription.uploadedAt).toLocaleDateString()}
                </p>
              </div>

              <JobDescriptionStatusBadge status={jobDescription.status} />
            </div>
          </button>
        );
      })}
    </div>
  );
}
