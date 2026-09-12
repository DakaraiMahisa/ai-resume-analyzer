import { Link, useParams } from "react-router-dom";

import { PageHeader } from "@/components/common";
import { ErrorState, LoadingState } from "@/components/feedback";
import { Button, Card } from "@/components/ui";
import { JobDescriptionStatusBadge, useJobDescription } from "@/modules/job";

export function JobDescriptionDetailPage() {
  const { id } = useParams<{ id: string }>();

  const {
    data: jobDescription,
    isLoading,
    isError,
    refetch,
  } = useJobDescription(id ?? "");

  if (isLoading) {
    return <LoadingState message="Loading job description..." />;
  }

  if (isError || !jobDescription) {
    return (
      <ErrorState
        title="Unable to load job description"
        description="We couldn't retrieve this job description. It may have been deleted or is no longer available."
        actionLabel="Try again"
        onAction={() => void refetch()}
      />
    );
  }

  return (
    <div className="space-y-8">
      <div>
        <Link
          to="/app/jobs"
          className="text-sm font-medium text-text-secondary transition-colors hover:text-text-primary"
        >
          ← Back to job descriptions
        </Link>
      </div>

      <PageHeader
        eyebrow="Job description"
        title={jobDescription.fileName}
        description="Review this target job before using it for resume analysis."
      />

      <Card>
        <div className="border-b border-border px-5 py-4 sm:px-6">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h2 className="text-base font-semibold text-text-primary">
                Job description
              </h2>

              <p className="mt-1 text-sm text-text-secondary">
                Processing and availability information.
              </p>
            </div>

            <JobDescriptionStatusBadge status={jobDescription.status} />
          </div>
        </div>

        <dl className="divide-y divide-border">
          <div className="flex items-center justify-between gap-6 px-5 py-4 sm:px-6">
            <dt className="text-sm text-text-secondary">File name</dt>

            <dd className="max-w-[60%] truncate text-right text-sm font-medium text-text-primary">
              {jobDescription.fileName}
            </dd>
          </div>

          <div className="flex items-center justify-between gap-6 px-5 py-4 sm:px-6">
            <dt className="text-sm text-text-secondary">Status</dt>

            <dd className="text-sm font-medium text-text-primary">
              {jobDescription.status}
            </dd>
          </div>

          <div className="flex items-center justify-between gap-6 px-5 py-4 sm:px-6">
            <dt className="text-sm text-text-secondary">Uploaded</dt>

            <dd className="text-sm font-medium text-text-primary">
              {new Date(jobDescription.uploadedAt).toLocaleString()}
            </dd>
          </div>

          <div className="flex items-center justify-between gap-6 px-5 py-4 sm:px-6">
            <dt className="text-sm text-text-secondary">Last updated</dt>

            <dd className="text-sm font-medium text-text-primary">
              {new Date(jobDescription.updatedAt).toLocaleString()}
            </dd>
          </div>
        </dl>
      </Card>

      {jobDescription.status === "COMPLETED" && (
        <Card className="p-5 sm:p-6">
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <p className="text-sm font-medium text-success">
                Ready for analysis
              </p>

              <h2 className="mt-1 text-base font-semibold text-text-primary">
                Use this job description
              </h2>

              <p className="mt-1 text-sm leading-6 text-text-secondary">
                Select a resume to analyze its fit against this target job.
              </p>
            </div>

            <Button type="button" variant="secondary" size="sm" disabled>
              Analyze resume
            </Button>
          </div>
        </Card>
      )}
    </div>
  );
}
