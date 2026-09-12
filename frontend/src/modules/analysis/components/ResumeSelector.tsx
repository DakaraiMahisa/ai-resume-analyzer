import { LoadingState } from "@/components/feedback";
import { Card } from "@/components/ui";
import { ResumeStatusBadge, useResumes } from "@/modules/resume";
import type { ResumeSummary } from "@/modules/resume/types/resume.types";

type ResumeSelectorProps = {
  selectedResumeId: string | null;
  onSelect: (resume: ResumeSummary) => void;
};

export function ResumeSelector({
  selectedResumeId,
  onSelect,
}: ResumeSelectorProps) {
  const { data: resumes, isLoading, isError } = useResumes();

  if (isLoading) {
    return <LoadingState message="Loading resumes..." />;
  }

  if (isError) {
    return (
      <Card className="p-5">
        <p className="text-sm text-danger">Unable to load your resumes.</p>
      </Card>
    );
  }

  const readyResumes =
    resumes?.filter((resume) => resume.status === "COMPLETED") ?? [];

  if (readyResumes.length === 0) {
    return (
      <Card className="border-dashed p-5">
        <h3 className="text-sm font-semibold text-text-primary">
          No ready resumes
        </h3>

        <p className="mt-1 text-sm leading-6 text-text-secondary">
          Upload and process a resume before starting an analysis.
        </p>
      </Card>
    );
  }

  return (
    <div className="space-y-3">
      {readyResumes.map((resume) => {
        const selected = resume.id === selectedResumeId;

        return (
          <button
            key={resume.id}
            type="button"
            onClick={() => onSelect(resume)}
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
                  {resume.fileName}
                </p>

                <p className="mt-1 text-xs text-text-tertiary">
                  Uploaded {new Date(resume.uploadedAt).toLocaleDateString()}
                </p>
              </div>

              <ResumeStatusBadge status={resume.status} />
            </div>
          </button>
        );
      })}
    </div>
  );
}
