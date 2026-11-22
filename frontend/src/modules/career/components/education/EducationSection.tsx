import { useState } from "react";

import { Button } from "@/components/ui";
import { useEducations } from "@/modules/career/hooks/use-educations";
import { EducationForm } from "@/modules/career/components/education/EducationForm";
import { EducationEditForm } from "@/modules/career/components/education/EducationEditForm";
import { useDeleteEducation } from "@/modules/career/hooks/use-education-mutations";
export function EducationSection() {
  const [isCreating, setIsCreating] = useState(false);
  const [editingEducationId, setEditingEducationId] = useState<string | null>(
    null,
  );

  const educationsQuery = useEducations();
  const deleteMutation = useDeleteEducation();

  const handleDelete = (educationId: string) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this education?",
    );

    if (!confirmed) return;

    deleteMutation.mutate(educationId);
  };
  if (educationsQuery.isLoading) {
    return (
      <section className="rounded-lg border border-border bg-surface p-5">
        <p className="text-sm text-text-secondary">Loading education...</p>
      </section>
    );
  }

  if (educationsQuery.isError) {
    return (
      <section className="rounded-lg border border-border bg-surface p-5">
        <p role="alert" className="text-sm text-danger">
          Unable to load education records.
        </p>

        <Button
          variant="secondary"
          size="sm"
          className="mt-3"
          onClick={() => educationsQuery.refetch()}
        >
          Try again
        </Button>
      </section>
    );
  }

  const educations = educationsQuery.data ?? [];

  return (
    <section className="rounded-lg border border-border bg-surface p-5 sm:p-6">
      <div className="mb-6 flex flex-col justify-between gap-3 sm:flex-row sm:items-center">
        <div>
          <h2 className="text-lg font-semibold text-text-primary">Education</h2>

          <p className="mt-1 text-sm text-text-secondary">
            Add your academic qualifications and educational background.
          </p>
        </div>

        {deleteMutation.isError && (
          <p role="alert" className="mb-4 text-sm text-danger">
            {deleteMutation.error.message ||
              "Unable to delete education record. Please try again."}
          </p>
        )}

        {!isCreating && !editingEducationId && (
          <Button onClick={() => setIsCreating(true)}>Add education</Button>
        )}
      </div>

      {isCreating && (
        <div className="mb-6 rounded-lg border border-border p-4 sm:p-5">
          <h3 className="mb-5 font-semibold text-text-primary">
            Add education
          </h3>

          <EducationForm
            onCancel={() => setIsCreating(false)}
            onCreated={() => setIsCreating(false)}
          />
        </div>
      )}

      {educations.length === 0 ? (
        <p className="rounded-md border border-dashed border-border p-5 text-center text-sm text-text-secondary">
          No education records added yet.
        </p>
      ) : (
        <div className="space-y-5">
          {educations.map((education) => (
            <article
              key={education.id}
              className="border-b border-border pb-5 last:border-b-0 last:pb-0"
            >
              {editingEducationId === education.id ? (
                <div className="rounded-lg border border-border p-4 sm:p-5">
                  <h3 className="mb-5 font-semibold text-text-primary">
                    Edit education
                  </h3>

                  <EducationEditForm
                    education={education}
                    onCancel={() => setEditingEducationId(null)}
                    onUpdated={() => setEditingEducationId(null)}
                  />
                </div>
              ) : (
                <>
                  <div className="flex flex-col justify-between gap-3 sm:flex-row sm:items-start">
                    <div>
                      <h3 className="font-semibold text-text-primary">
                        {education.degree}
                      </h3>

                      <p className="text-sm text-text-secondary">
                        {education.institution}
                        {education.location && ` · ${education.location}`}
                      </p>
                    </div>

                    <div className="flex gap-2">
                      <Button
                        variant="secondary"
                        size="sm"
                        onClick={() => setEditingEducationId(education.id)}
                        disabled={deleteMutation.isPending}
                      >
                        Edit
                      </Button>

                      <Button
                        variant="danger"
                        size="sm"
                        onClick={() => handleDelete(education.id)}
                        disabled={deleteMutation.isPending}
                      >
                        {deleteMutation.isPending ? "Deleting..." : "Delete"}
                      </Button>
                    </div>
                  </div>

                  {education.fieldOfStudy && (
                    <p className="text-sm text-text-secondary">
                      {education.fieldOfStudy}
                    </p>
                  )}

                  <p className="mt-2 text-sm text-text-secondary">
                    {education.startDate} –{" "}
                    {education.currentlyStudying
                      ? "Present"
                      : (education.endDate ?? "End date not specified")}
                  </p>

                  {education.grade && (
                    <p className="mt-1 text-xs text-text-secondary">
                      Grade: {education.grade}
                    </p>
                  )}

                  {education.description && (
                    <p className="mt-3 whitespace-pre-line text-sm leading-6 text-text-secondary">
                      {education.description}
                    </p>
                  )}
                </>
              )}
            </article>
          ))}
        </div>
      )}
    </section>
  );
}
