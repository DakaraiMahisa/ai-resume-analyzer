import { JobDescriptionCard } from "@/modules/job/components/JobDescriptionCard";
import type { JobDescriptionSummaryResponse } from "@/modules/job/types/job-description.types";

type JobDescriptionListProps = {
  jobDescriptions: JobDescriptionSummaryResponse[];
  onDelete?: (jobDescriptionId: string) => void;
};

export function JobDescriptionList({
  jobDescriptions,
  onDelete,
}: JobDescriptionListProps) {
  if (jobDescriptions.length === 0) {
    return (
      <div className="rounded-md border border-dashed border-border px-6 py-10 text-center">
        <p className="text-sm font-medium text-text-primary">
          No job descriptions yet
        </p>

        <p className="mt-1 text-sm text-text-secondary">
          Add a job description to start analyzing your resume against a role.
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-3">
      {jobDescriptions.map((jobDescription) => (
        <JobDescriptionCard
          key={jobDescription.id}
          jobDescription={jobDescription}
          onDelete={onDelete}
        />
      ))}
    </div>
  );
}
