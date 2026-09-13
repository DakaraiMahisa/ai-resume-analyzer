import { useState } from "react";

import { PageHeader } from "@/components/common";
import { ErrorState, LoadingState } from "@/components/feedback";
import { Card } from "@/components/ui";
import {
  DeleteJobDescriptionDialog,
  JobDescriptionInput,
  JobDescriptionList,
  useDeleteJobDescription,
  useJobDescriptions,
} from "@/modules/job";

export function JobDescriptionsPage() {
  const [deleteJobDescriptionId, setDeleteJobDescriptionId] = useState<
    string | null
  >(null);

  const {
    data: jobDescriptions,
    isLoading,
    isError,
    refetch,
  } = useJobDescriptions();

  const deleteMutation = useDeleteJobDescription();

  if (isLoading) {
    return <LoadingState message="Loading job descriptions..." />;
  }

  if (isError) {
    return (
      <ErrorState
        title="Unable to load job descriptions"
        description="We couldn't retrieve your saved job descriptions. Please try again."
        actionLabel="Try again"
        onAction={() => void refetch()}
      />
    );
  }

  const selectedJobDescription = jobDescriptions?.find(
    (jobDescription) => jobDescription.id === deleteJobDescriptionId,
  );

  const handleDelete = () => {
    if (!deleteJobDescriptionId) {
      return;
    }

    deleteMutation.mutate(deleteJobDescriptionId, {
      onSuccess: () => {
        setDeleteJobDescriptionId(null);
      },
    });
  };

  return (
    <div className="space-y-8">
      <PageHeader
        eyebrow="Job descriptions"
        title="Target jobs"
        description="Manage the job descriptions you can use to analyze your resumes."
      />

      <JobDescriptionInput />

      <section className="space-y-4">
        <div>
          <h2 className="text-base font-semibold text-text-primary">
            Saved job descriptions
          </h2>

          <p className="mt-1 text-sm text-text-secondary">
            Select a job description when you're ready to analyze a resume.
          </p>
        </div>

        {jobDescriptions && jobDescriptions.length > 0 ? (
          <JobDescriptionList
            jobDescriptions={jobDescriptions}
            onDelete={setDeleteJobDescriptionId}
          />
        ) : (
          <Card className="p-6">
            <p className="text-sm text-text-secondary">
              You haven't added any job descriptions yet.
            </p>
          </Card>
        )}
      </section>

      {selectedJobDescription && (
        <DeleteJobDescriptionDialog
          fileName={selectedJobDescription.fileName}
          loading={deleteMutation.isPending}
          onConfirm={handleDelete}
          onCancel={() => {
            if (!deleteMutation.isPending) {
              setDeleteJobDescriptionId(null);
            }
          }}
        />
      )}
    </div>
  );
}
