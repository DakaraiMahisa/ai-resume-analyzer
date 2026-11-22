import { useState } from "react";

import { Button, Card } from "@/components/ui";
import { LoadingState, ErrorState, EmptyState } from "@/components/feedback";

import { useProjects } from "../../hooks/use-projects";
import { useDeleteProject } from "../../hooks/use-project-mutations";

import { ProjectForm } from "./ProjectForm";
import { ProjectEditForm } from "./ProjectEditForm";

import type { CandidateProject } from "../../types/project.types";

export const ProjectsSection = () => {
  const projectsQuery = useProjects();
  const deleteProject = useDeleteProject();

  const [showCreateForm, setShowCreateForm] = useState(false);
  const [editingProjectId, setEditingProjectId] = useState<string | null>(null);

  const handleDelete = (project: CandidateProject) => {
    const confirmed = window.confirm(
      `Are you sure you want to delete "${project.projectName}"?`,
    );

    if (confirmed) {
      deleteProject.mutate(project.id);
    }
  };

  if (projectsQuery.isLoading) {
    return <LoadingState />;
  }

  if (projectsQuery.isError) {
    return (
      <ErrorState
        title="Unable to load projects"
        description="We couldn't retrieve your projects. Please try again."
        actionLabel="Retry"
        onAction={() => projectsQuery.refetch()}
      />
    );
  }

  const projects = projectsQuery.data ?? [];

  return (
    <Card className="space-y-6 p-6">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h2 className="text-lg font-semibold">Projects</h2>
          <p className="text-sm text-muted-foreground">
            Showcase projects that demonstrate your skills and experience.
          </p>
        </div>

        {!showCreateForm && (
          <Button
            onClick={() => setShowCreateForm(true)}
            disabled={Boolean(editingProjectId)}
          >
            Add project
          </Button>
        )}
      </div>

      {showCreateForm && (
        <div className="rounded-lg border p-4">
          <ProjectForm
            onCreated={() => setShowCreateForm(false)}
            onCancel={() => setShowCreateForm(false)}
          />
        </div>
      )}

      {deleteProject.isError && (
        <p className="text-sm text-destructive">
          {deleteProject.error.message || "Failed to delete project."}
        </p>
      )}

      {projects.length === 0 && !showCreateForm ? (
        <EmptyState
          title="No projects added yet"
          description="Add projects to highlight your practical experience."
        />
      ) : (
        <div className="space-y-4">
          {projects.map((project) => (
            <div key={project.id} className="rounded-lg border p-4">
              {editingProjectId === project.id ? (
                <ProjectEditForm
                  project={project}
                  onUpdated={() => setEditingProjectId(null)}
                  onCancel={() => setEditingProjectId(null)}
                />
              ) : (
                <>
                  <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                    <div className="min-w-0 space-y-1">
                      <h3 className="font-semibold">{project.projectName}</h3>

                      {project.role && (
                        <p className="text-sm text-muted-foreground">
                          {project.role}
                        </p>
                      )}

                      {(project.startDate ||
                        project.endDate ||
                        project.currentlyWorking) && (
                        <p className="text-xs text-muted-foreground">
                          {project.startDate ?? "Start date not specified"}
                          {" — "}
                          {project.currentlyWorking
                            ? "Present"
                            : (project.endDate ?? "End date not specified")}
                        </p>
                      )}
                    </div>

                    <div className="flex gap-2">
                      <Button
                        variant="secondary"
                        size="sm"
                        onClick={() => setEditingProjectId(project.id)}
                        disabled={
                          Boolean(editingProjectId) || deleteProject.isPending
                        }
                      >
                        Edit
                      </Button>

                      <Button
                        variant="danger"
                        size="sm"
                        onClick={() => handleDelete(project)}
                        disabled={
                          deleteProject.isPending || Boolean(editingProjectId)
                        }
                      >
                        Delete
                      </Button>
                    </div>
                  </div>

                  {project.description && (
                    <p className="mt-3 whitespace-pre-wrap text-sm">
                      {project.description}
                    </p>
                  )}

                  {project.technologies.length > 0 && (
                    <div className="mt-3 flex flex-wrap gap-2">
                      {project.technologies.map((technology) => (
                        <span
                          key={technology}
                          className="rounded-full bg-muted px-3 py-1 text-xs"
                        >
                          {technology}
                        </span>
                      ))}
                    </div>
                  )}

                  {project.projectUrl && (
                    <a
                      href={project.projectUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="mt-3 inline-block text-sm text-primary underline"
                    >
                      View project
                    </a>
                  )}
                </>
              )}
            </div>
          ))}
        </div>
      )}
    </Card>
  );
};
