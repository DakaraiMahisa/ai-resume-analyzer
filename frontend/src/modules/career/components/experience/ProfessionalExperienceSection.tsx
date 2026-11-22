import { useState } from "react";

import { Button } from "@/components/ui";
import { useProfessionalExperiences } from "@/modules/career/hooks/use-experiences";
import { ProfessionalExperienceForm } from "@/modules/career/components/experience/ProfessionalExperienceForm";
import { ProfessionalExperienceEditForm } from "@/modules/career/components/experience/ProfessionalExperienceEditForm";
import { useDeleteProfessionalExperience } from "@/modules/career/hooks/use-experience-mutations";

export function ProfessionalExperienceSection() {
  const [isCreating, setIsCreating] = useState(false);
  const [editingExperienceId, setEditingExperienceId] = useState<string | null>(
    null,
  );

  const experiencesQuery = useProfessionalExperiences();
  const deleteMutation = useDeleteProfessionalExperience();

  const handleDelete = (experienceId: string) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this professional experience?",
    );

    if (!confirmed) return;

    deleteMutation.mutate(experienceId);
  };
  if (experiencesQuery.isLoading) {
    return (
      <section className="rounded-lg border border-border bg-surface p-5">
        <p className="text-sm text-text-secondary">
          Loading professional experience...
        </p>
      </section>
    );
  }

  if (experiencesQuery.isError) {
    return (
      <section className="rounded-lg border border-border bg-surface p-5">
        <p role="alert" className="text-sm text-danger">
          Unable to load professional experience.
        </p>

        <Button
          variant="secondary"
          size="sm"
          className="mt-3"
          onClick={() => experiencesQuery.refetch()}
        >
          Try again
        </Button>
      </section>
    );
  }

  const experiences = experiencesQuery.data ?? [];

  return (
    <section className="rounded-lg border border-border bg-surface p-5 sm:p-6">
      <div className="mb-6 flex flex-col justify-between gap-3 sm:flex-row sm:items-center">
        <div>
          <h2 className="text-lg font-semibold text-text-primary">
            Professional Experience
          </h2>

          <p className="mt-1 text-sm text-text-secondary">
            Add your employment history, responsibilities and achievements.
          </p>
        </div>
        {deleteMutation.isError && (
          <p role="alert" className="mb-4 text-sm text-danger">
            {deleteMutation.error.message ||
              "Unable to delete professional experience."}
          </p>
        )}
        {!isCreating && !editingExperienceId && (
          <Button
            onClick={() => setIsCreating(true)}
            disabled={deleteMutation.isPending}
          >
            Add experience
          </Button>
        )}
      </div>

      {isCreating && (
        <div className="mb-6 rounded-lg border border-border p-4 sm:p-5">
          <h3 className="mb-5 font-semibold text-text-primary">
            Add professional experience
          </h3>

          <ProfessionalExperienceForm
            onCancel={() => setIsCreating(false)}
            onCreated={() => setIsCreating(false)}
          />
        </div>
      )}

      {experiences.length === 0 ? (
        <p className="rounded-md border border-dashed border-border p-5 text-center text-sm text-text-secondary">
          No professional experience added yet.
        </p>
      ) : (
        <div className="space-y-5">
          {experiences.map((experience) => (
            <article
              key={experience.id}
              className="border-b border-border pb-5 last:border-b-0 last:pb-0"
            >
              {editingExperienceId === experience.id ? (
                <div className="rounded-lg border border-border p-4 sm:p-5">
                  <h3 className="mb-5 font-semibold text-text-primary">
                    Edit professional experience
                  </h3>

                  <ProfessionalExperienceEditForm
                    experience={experience}
                    onCancel={() => setEditingExperienceId(null)}
                    onUpdated={() => setEditingExperienceId(null)}
                  />
                </div>
              ) : (
                <>
                  <div className="flex flex-col justify-between gap-3 sm:flex-row sm:items-start">
                    <div>
                      <h3 className="font-semibold text-text-primary">
                        {experience.jobTitle}
                      </h3>

                      <p className="text-sm text-text-secondary">
                        {experience.companyName}
                        {experience.location && ` · ${experience.location}`}
                      </p>
                    </div>

                    <div className="flex gap-2">
                      <Button
                        variant="secondary"
                        size="sm"
                        disabled={
                          isCreating ||
                          editingExperienceId !== null ||
                          deleteMutation.isPending
                        }
                        onClick={() => setEditingExperienceId(experience.id)}
                      >
                        Edit
                      </Button>

                      <Button
                        variant="danger"
                        size="sm"
                        disabled={
                          isCreating ||
                          editingExperienceId !== null ||
                          deleteMutation.isPending
                        }
                        loading={
                          deleteMutation.isPending &&
                          deleteMutation.variables === experience.id
                        }
                        onClick={() => handleDelete(experience.id)}
                      >
                        Delete
                      </Button>
                    </div>
                  </div>

                  <p className="mt-2 text-sm text-text-secondary">
                    {experience.startDate} –{" "}
                    {experience.currentlyWorking
                      ? "Present"
                      : (experience.endDate ?? "End date not specified")}
                  </p>

                  {experience.employmentType && (
                    <p className="text-xs text-text-secondary">
                      {experience.employmentType.replaceAll("_", " ")}
                    </p>
                  )}

                  {experience.description && (
                    <p className="mt-3 whitespace-pre-line text-sm leading-6 text-text-secondary">
                      {experience.description}
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
