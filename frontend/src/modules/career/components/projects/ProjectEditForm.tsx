import { useState } from "react";

import { Button, Input, Label, Textarea } from "@/components/ui";
import { useUpdateProject } from "../../hooks/use-project-mutations";
import type {
  CandidateProject,
  UpdateCandidateProjectRequest,
} from "../../types/project.types";

type ProjectEditFormProps = {
  project: CandidateProject;
  onUpdated: () => void;
  onCancel: () => void;
};

export const ProjectEditForm = ({
  project,
  onUpdated,
  onCancel,
}: ProjectEditFormProps) => {
  const updateProject = useUpdateProject();

  const [projectName, setProjectName] = useState(project.projectName);
  const [role, setRole] = useState(project.role ?? "");
  const [description, setDescription] = useState(project.description ?? "");
  const [technologies, setTechnologies] = useState(
    project.technologies.join(", "),
  );
  const [projectUrl, setProjectUrl] = useState(project.projectUrl ?? "");
  const [startDate, setStartDate] = useState(project.startDate ?? "");
  const [endDate, setEndDate] = useState(project.endDate ?? "");
  const [currentlyWorking, setCurrentlyWorking] = useState(
    project.currentlyWorking,
  );

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const request: UpdateCandidateProjectRequest = {};

    if (projectName.trim() !== project.projectName) {
      request.projectName = projectName.trim();
    }

    if (role !== (project.role ?? "")) {
      request.role = role.trim();
    }

    if (description !== (project.description ?? "")) {
      request.description = description.trim();
    }

    const parsedTechnologies = technologies
      .split(",")
      .map((technology) => technology.trim())
      .filter(Boolean);

    if (
      JSON.stringify(parsedTechnologies) !==
      JSON.stringify(project.technologies)
    ) {
      request.technologies = parsedTechnologies;
    }

    if (projectUrl !== (project.projectUrl ?? "")) {
      request.projectUrl = projectUrl.trim();
    }

    if (startDate !== (project.startDate ?? "")) {
      request.startDate = startDate || null;
    }

    if (currentlyWorking !== project.currentlyWorking) {
      request.currentlyWorking = currentlyWorking;

      if (currentlyWorking) {
        // Backend clears the end date when a project becomes ongoing.
      } else {
        request.endDate = endDate || null;
      }
    } else if (!currentlyWorking && endDate !== (project.endDate ?? "")) {
      request.endDate = endDate || null;
    }

    if (Object.keys(request).length === 0) {
      onCancel();
      return;
    }

    updateProject.mutate(
      { projectId: project.id, request },
      { onSuccess: onUpdated },
    );
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="editProjectName">Project name *</Label>
        <Input
          id="editProjectName"
          value={projectName}
          onChange={(event) => setProjectName(event.target.value)}
          required
          maxLength={255}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="editProjectRole">Your role</Label>
        <Input
          id="editProjectRole"
          value={role}
          onChange={(event) => setRole(event.target.value)}
          maxLength={150}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="editProjectDescription">Description</Label>
        <Textarea
          id="editProjectDescription"
          value={description}
          onChange={(event) => setDescription(event.target.value)}
          rows={4}
          maxLength={10000}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="editProjectTechnologies">Technologies</Label>
        <Input
          id="editProjectTechnologies"
          value={technologies}
          onChange={(event) => setTechnologies(event.target.value)}
          placeholder="Java, Spring Boot, React, MySQL"
        />
        <p className="text-xs text-muted-foreground">
          Separate technologies with commas. Clear the field to remove all.
        </p>
      </div>

      <div className="space-y-2">
        <Label htmlFor="editProjectUrl">Project URL</Label>
        <Input
          id="editProjectUrl"
          type="url"
          value={projectUrl}
          onChange={(event) => setProjectUrl(event.target.value)}
          maxLength={500}
        />
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        <div className="space-y-2">
          <Label htmlFor="editProjectStartDate">Start date</Label>
          <Input
            id="editProjectStartDate"
            type="date"
            value={startDate}
            onChange={(event) => setStartDate(event.target.value)}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="editProjectEndDate">End date</Label>
          <Input
            id="editProjectEndDate"
            type="date"
            value={endDate}
            onChange={(event) => setEndDate(event.target.value)}
            disabled={currentlyWorking}
            min={startDate || undefined}
            required={!currentlyWorking}
          />
        </div>
      </div>

      <label className="flex items-center gap-2 text-sm">
        <input
          type="checkbox"
          checked={currentlyWorking}
          onChange={(event) => {
            setCurrentlyWorking(event.target.checked);
            if (event.target.checked) setEndDate("");
          }}
          className="h-4 w-4 rounded border"
        />
        I am currently working on this project
      </label>

      {updateProject.isError && (
        <p className="text-sm text-destructive">
          {updateProject.error.message ||
            "Failed to update project. Please try again."}
        </p>
      )}

      <div className="flex justify-end gap-3">
        <Button
          type="button"
          variant="secondary"
          onClick={onCancel}
          disabled={updateProject.isPending}
        >
          Cancel
        </Button>

        <Button type="submit" disabled={updateProject.isPending}>
          {updateProject.isPending ? "Saving..." : "Save changes"}
        </Button>
      </div>
    </form>
  );
};
