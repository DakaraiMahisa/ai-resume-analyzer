import { Link } from "react-router-dom";

import { Button, Card } from "@/components/ui";
import type { JobDescriptionSummaryResponse } from "@/modules/job/types/job-description.types";
import { JobDescriptionStatusBadge } from "@/modules/job/components/JobDescriptionStatusBadge";
type JobDescriptionCardProps = {
  jobDescription: JobDescriptionSummaryResponse;
  onDelete?: (jobDescriptionId: string) => void;
};

export function JobDescriptionCard({
  jobDescription,
  onDelete,
}: JobDescriptionCardProps) {
  return (
    <Card className="p-5">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
        <div className="min-w-0">
          <div className="flex flex-wrap items-center gap-2">
            <h3 className="truncate text-base font-semibold text-text-primary">
              {jobDescription.fileName}
            </h3>

            <JobDescriptionStatusBadge status={jobDescription.status} />
          </div>

          <p className="mt-2 text-sm text-text-secondary">
            Added {new Date(jobDescription.uploadedAt).toLocaleString()}
          </p>
        </div>

        <div className="flex shrink-0 items-center gap-2">
          <Link
            to={`/app/jobs/${jobDescription.id}`}
            className="inline-flex h-9 items-center justify-center rounded-md border border-border bg-surface px-3 text-sm font-medium text-text-primary transition-colors hover:bg-surface-muted focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand/30 focus-visible:ring-offset-2 focus-visible:ring-offset-background"
          >
            Open
          </Link>

          {onDelete && (
            <Button
              type="button"
              variant="ghost"
              size="sm"
              onClick={() => onDelete(jobDescription.id)}
            >
              Delete
            </Button>
          )}
        </div>
      </div>
    </Card>
  );
}
