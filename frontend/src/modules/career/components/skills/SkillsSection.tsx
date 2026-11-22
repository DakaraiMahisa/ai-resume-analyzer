import { useState } from "react";

import { Button } from "@/components/ui";
import { useSkills } from "@/modules/career/hooks/use-skills";
import { useDeleteSkill } from "@/modules/career/hooks/use-skill-mutations";

import { SkillsForm } from "@/modules/career/components/skills/SkillsForm";
import { SkillsEditForm } from "@/modules/career/components/skills/SkillsEditForm";

import type { SkillCategory } from "@/modules/career/types/skill.types";

const categoryLabels: Record<SkillCategory, string> = {
  TECHNICAL: "Technical",
  SOFT: "Soft skill",
  LANGUAGE: "Language",
  TOOL: "Tool",
  DOMAIN: "Domain",
};

const proficiencyLabels = {
  BEGINNER: "Beginner",
  INTERMEDIATE: "Intermediate",
  ADVANCED: "Advanced",
  EXPERT: "Expert",
};

export function SkillsSection() {
  const [isCreating, setIsCreating] = useState(false);
  const [editingSkillId, setEditingSkillId] = useState<string | null>(null);

  const skillsQuery = useSkills();
  const deleteMutation = useDeleteSkill();

  const handleDelete = (skillId: string) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this skill?",
    );

    if (!confirmed) return;

    deleteMutation.mutate(skillId);
  };

  if (skillsQuery.isLoading) {
    return (
      <section className="rounded-lg border border-border bg-surface p-5">
        <p className="text-sm text-text-secondary">Loading skills...</p>
      </section>
    );
  }

  if (skillsQuery.isError) {
    return (
      <section className="rounded-lg border border-border bg-surface p-5">
        <p role="alert" className="text-sm text-danger">
          Unable to load skills.
        </p>

        <Button
          variant="secondary"
          size="sm"
          className="mt-3"
          onClick={() => skillsQuery.refetch()}
        >
          Try again
        </Button>
      </section>
    );
  }

  const skills = skillsQuery.data ?? [];

  return (
    <section className="rounded-lg border border-border bg-surface p-5 sm:p-6">
      <div className="mb-6 flex flex-col justify-between gap-3 sm:flex-row sm:items-center">
        <div>
          <h2 className="text-lg font-semibold text-text-primary">Skills</h2>

          <p className="mt-1 text-sm text-text-secondary">
            Showcase your technical abilities, tools, languages, and
            professional strengths.
          </p>
        </div>

        {!isCreating && !editingSkillId && (
          <Button onClick={() => setIsCreating(true)}>Add skill</Button>
        )}
      </div>

      {isCreating && (
        <div className="mb-6 rounded-lg border border-border p-4 sm:p-5">
          <h3 className="mb-5 font-semibold text-text-primary">Add skill</h3>

          <SkillsForm
            onCancel={() => setIsCreating(false)}
            onCreated={() => setIsCreating(false)}
          />
        </div>
      )}

      {deleteMutation.isError && (
        <p role="alert" className="mb-4 text-sm text-danger">
          {deleteMutation.error.message ||
            "Unable to delete skill. Please try again."}
        </p>
      )}

      {skills.length === 0 ? (
        <p className="rounded-md border border-dashed border-border p-5 text-center text-sm text-text-secondary">
          No skills added yet.
        </p>
      ) : (
        <div className="space-y-5">
          {skills.map((skill) => (
            <article
              key={skill.id}
              className="border-b border-border pb-5 last:border-b-0 last:pb-0"
            >
              {editingSkillId === skill.id ? (
                <div className="rounded-lg border border-border p-4 sm:p-5">
                  <h3 className="mb-5 font-semibold text-text-primary">
                    Edit skill
                  </h3>

                  <SkillsEditForm
                    skill={skill}
                    onCancel={() => setEditingSkillId(null)}
                    onUpdated={() => setEditingSkillId(null)}
                  />
                </div>
              ) : (
                <>
                  <div className="flex flex-col justify-between gap-3 sm:flex-row sm:items-start">
                    <div>
                      <h3 className="font-semibold text-text-primary">
                        {skill.skillName}
                      </h3>

                      <p className="mt-1 text-sm text-text-secondary">
                        {categoryLabels[skill.category]} ·{" "}
                        {proficiencyLabels[skill.proficiencyLevel]}
                      </p>

                      {skill.yearsOfExperience !== null && (
                        <p className="mt-1 text-xs text-text-secondary">
                          {skill.yearsOfExperience} years of experience
                        </p>
                      )}
                    </div>

                    <div className="flex gap-2">
                      <Button
                        variant="secondary"
                        size="sm"
                        onClick={() => setEditingSkillId(skill.id)}
                        disabled={deleteMutation.isPending}
                      >
                        Edit
                      </Button>

                      <Button
                        variant="danger"
                        size="sm"
                        onClick={() => handleDelete(skill.id)}
                        disabled={deleteMutation.isPending}
                      >
                        {deleteMutation.isPending ? "Deleting..." : "Delete"}
                      </Button>
                    </div>
                  </div>
                </>
              )}
            </article>
          ))}
        </div>
      )}
    </section>
  );
}
