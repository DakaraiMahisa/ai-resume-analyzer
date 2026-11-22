import { useState } from "react";

import { Button, Input, Label, Textarea } from "@/components/ui";
import { useCreateProject } from "../../hooks/use-project-mutations";
import type { CreateCandidateProjectRequest } from "../../types/project.types";

type ProjectFormProps = {
  onCreated: () => void;
  onCancel: () => void;
};

export const ProjectForm = ({ onCreated, onCancel }: ProjectFormProps) => {
  const createProject = useCreateProject();

  const [projectName, setProjectName] = useState("");
  const [role, setRole] = useState("");
  const [description, setDescription] = useState("");
  const [technologies, setTechnologies] = useState("");
  const [projectUrl, setProjectUrl] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [currentlyWorking, setCurrentlyWorking] = useState(false);

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const request: CreateCandidateProjectRequest = {
      projectName: projectName.trim(),
      role: role.trim() || undefined,
      description: description.trim() || undefined,
      technologies: technologies
        .split(",")
        .map((technology) => technology.trim())
        .filter(Boolean),
      projectUrl: projectUrl.trim() || undefined,
      startDate: startDate || undefined,
      endDate: currentlyWorking ? undefined : endDate || undefined,
      currentlyWorking,
    };

    createProject.mutate(request, {
      onSuccess: onCreated,
    });
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="projectName">Project name *</Label>
        <Input
          id="projectName"
          value={projectName}
          onChange={(event) => setProjectName(event.target.value)}
          placeholder="e.g. Inventory Management System"
          required
          maxLength={255}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="projectRole">Your role</Label>
        <Input
          id="projectRole"
          value={role}
          onChange={(event) => setRole(event.target.value)}
          placeholder="e.g. Backend Developer"
          maxLength={150}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="projectDescription">Description</Label>
        <Textarea
          id="projectDescription"
          value={description}
          onChange={(event) => setDescription(event.target.value)}
          placeholder="Describe the project, your contributions and outcomes..."
          rows={4}
          maxLength={10000}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="projectTechnologies">Technologies</Label>
        <Input
          id="projectTechnologies"
          value={technologies}
          onChange={(event) => setTechnologies(event.target.value)}
          placeholder="Java, Spring Boot, React, MySQL"
        />
        <p className="text-xs text-muted-foreground">
          Separate technologies with commas.
        </p>
      </div>

      <div className="space-y-2">
        <Label htmlFor="projectUrl">Project URL</Label>
        <Input
          id="projectUrl"
          type="url"
          value={projectUrl}
          onChange={(event) => setProjectUrl(event.target.value)}
          placeholder="https://github.com/username/project"
          maxLength={500}
        />
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        <div className="space-y-2">
          <Label htmlFor="projectStartDate">Start date</Label>
          <Input
            id="projectStartDate"
            type="date"
            value={startDate}
            onChange={(event) => setStartDate(event.target.value)}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="projectEndDate">End date</Label>
          <Input
            id="projectEndDate"
            type="date"
            value={endDate}
            onChange={(event) => setEndDate(event.target.value)}
            disabled={currentlyWorking}
            required={!currentlyWorking && Boolean(startDate)}
            min={startDate || undefined}
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

      {createProject.isError && (
        <p className="text-sm text-destructive">
          {createProject.error.message ||
            "Failed to create project. Please try again."}
        </p>
      )}

      <div className="flex justify-end gap-3">
        <Button
          type="button"
          variant="secondary"
          onClick={onCancel}
          disabled={createProject.isPending}
        >
          Cancel
        </Button>

        <Button type="submit" disabled={createProject.isPending}>
          {createProject.isPending ? "Saving..." : "Add project"}
        </Button>
      </div>
    </form>
  );
};
