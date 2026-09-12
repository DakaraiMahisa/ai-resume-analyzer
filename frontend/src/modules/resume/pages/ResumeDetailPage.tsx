import { useState } from "react";
import { Link, useParams } from "react-router-dom";

import { PageHeader } from "@/components/common";
import { ErrorState, LoadingState } from "@/components/feedback";
import { Button, Card } from "@/components/ui";
import { JobDescriptionInput } from "@/modules/job";
import { ResumeStatusBadge, useResume } from "@/modules/resume";

export function ResumeDetailPage() {
  const { id } = useParams<{ id: string }>();

  const [jobDescriptionId, setJobDescriptionId] = useState<string | null>(null);

  const { data: resume, isLoading, isError, refetch } = useResume(id ?? "");

  if (isLoading) {
    return <LoadingState message="Loading resume..." />;
  }

  if (isError || !resume) {
    return (
      <ErrorState
        title="Unable to load resume"
        description="We couldn't retrieve this resume. It may have been deleted or is no longer available."
        actionLabel="Try again"
        onAction={() => void refetch()}
      />
    );
  }

  const handleJobDescriptionCreated = (createdJobDescriptionId: string) => {
    setJobDescriptionId(createdJobDescriptionId);
  };

  return (
    <div className="space-y-8">
      <div>
        <Link
          to="/app/resumes"
          className="text-sm font-medium text-text-secondary transition-colors hover:text-text-primary"
        >
          ← Back to resumes
        </Link>
      </div>

      <PageHeader
        eyebrow="Resume workspace"
        title={resume.fileName}
        description="Choose a target job to understand how well this resume matches the role."
      />

      <Card>
        <div className="border-b border-border px-5 py-4 sm:px-6">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h2 className="text-base font-semibold text-text-primary">
                Your resume
              </h2>

              <p className="mt-1 text-sm text-text-secondary">
                This resume is ready to use for job matching.
              </p>
            </div>

            <ResumeStatusBadge status={resume.status} />
          </div>
        </div>

        <dl className="divide-y divide-border">
          <div className="flex items-center justify-between gap-6 px-5 py-4 sm:px-6">
            <dt className="text-sm text-text-secondary">File name</dt>

            <dd className="max-w-[60%] truncate text-right text-sm font-medium text-text-primary">
              {resume.fileName}
            </dd>
          </div>

          <div className="flex items-center justify-between gap-6 px-5 py-4 sm:px-6">
            <dt className="text-sm text-text-secondary">Uploaded</dt>

            <dd className="text-sm font-medium text-text-primary">
              {new Date(resume.uploadedAt).toLocaleString()}
            </dd>
          </div>

          <div className="flex items-center justify-between gap-6 px-5 py-4 sm:px-6">
            <dt className="text-sm text-text-secondary">Last updated</dt>

            <dd className="text-sm font-medium text-text-primary">
              {new Date(resume.updatedAt).toLocaleString()}
            </dd>
          </div>
        </dl>
      </Card>

      {resume.status === "COMPLETED" && !jobDescriptionId && (
        <JobDescriptionInput onCreated={handleJobDescriptionCreated} />
      )}

      {resume.status === "COMPLETED" && jobDescriptionId && (
        <Card className="p-5 sm:p-6">
          <div className="flex flex-col gap-5 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <p className="text-sm font-medium text-success">
                Target job ready
              </p>

              <h2 className="mt-1 text-base font-semibold text-text-primary">
                Resume and target job are ready
              </h2>

              <p className="mt-1 text-sm leading-6 text-text-secondary">
                Your resume can now be analyzed against the selected job.
              </p>
            </div>

            <div className="flex shrink-0 items-center gap-3">
              <span className="text-sm font-medium text-success">✓ Ready</span>

              <Button
                type="button"
                variant="secondary"
                size="sm"
                onClick={() => setJobDescriptionId(null)}
              >
                Change job
              </Button>
            </div>
          </div>
        </Card>
      )}
    </div>
  );
}
