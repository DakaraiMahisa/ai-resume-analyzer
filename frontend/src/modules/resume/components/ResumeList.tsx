import { Link } from "react-router-dom";

import { Card, Spinner } from "@/components/ui";

import { EmptyState, ErrorState } from "@/components/feedback";
import { useResumes } from "@/modules/resume";
import { ResumeStatusBadge } from "@/modules/resume/components/ResumeStatusBadge";
import { ResumeRowActions } from "@/modules/resume/components/ResumeRowActions";
export function ResumeList() {
  const { data: resumes, isLoading, isError, refetch } = useResumes();

  if (isLoading) {
    return (
      <Card className="flex min-h-48 items-center justify-center p-6">
        <Spinner />
      </Card>
    );
  }

  if (isError) {
    return (
      <ErrorState
        title="Unable to load resumes"
        description="We couldn't retrieve your resumes. Please try again."
        actionLabel="Try again"
        onAction={() => void refetch()}
      />
    );
  }

  if (!resumes || resumes.length === 0) {
    return (
      <EmptyState
        title="No resumes yet"
        description="Upload your first resume to start analyzing your experience."
      />
    );
  }

  return (
    <Card className="overflow-hidden">
      <div className="border-b border-border px-5 py-4 sm:px-6">
        <h2 className="text-base font-semibold text-text-primary">
          Your resumes
        </h2>

        <p className="mt-1 text-sm text-text-secondary">
          {resumes.length} {resumes.length === 1 ? "resume" : "resumes"}{" "}
          uploaded
        </p>
      </div>

      <div className="divide-y divide-border">
        {resumes.map((resume) => (
          <div
            key={resume.id}
            className="flex items-center justify-between gap-4 px-5 py-4 sm:px-6"
          >
            <div className="min-w-0">
              <Link
                to={`/app/resumes/${resume.id}`}
                className="block truncate text-sm font-medium text-text-primary hover:text-brand"
              >
                {resume.fileName}
              </Link>

              <p className="mt-1 text-xs text-text-tertiary">
                Uploaded {new Date(resume.uploadedAt).toLocaleDateString()}
              </p>
            </div>

            <div className="flex shrink-0 items-center gap-3">
              <ResumeStatusBadge status={resume.status} />

              <ResumeRowActions
                resumeId={resume.id}
                fileName={resume.fileName}
              />
            </div>
          </div>
        ))}
      </div>
    </Card>
  );
}
