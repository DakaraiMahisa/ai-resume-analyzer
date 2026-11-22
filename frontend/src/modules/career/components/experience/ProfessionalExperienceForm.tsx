import { useState, type FormEvent } from "react";

import { Button, Input, Label, Textarea } from "@/components/ui";
import { useCreateProfessionalExperience } from "@/modules/career/hooks/use-experience-mutations";

import type {
  CreateProfessionalExperienceRequest,
  EmploymentType,
} from "@/modules/career/types/experience.types";

type ProfessionalExperienceFormProps = {
  onCancel: () => void;
  onCreated: () => void;
};

const employmentTypes: { value: EmploymentType; label: string }[] = [
  { value: "FULL_TIME", label: "Full-time" },
  { value: "PART_TIME", label: "Part-time" },
  { value: "CONTRACT", label: "Contract" },
  { value: "INTERNSHIP", label: "Internship" },
  { value: "FREELANCE", label: "Freelance" },
  { value: "SELF_EMPLOYED", label: "Self-employed" },
  { value: "TEMPORARY", label: "Temporary" },
  { value: "OTHER", label: "Other" },
];

export function ProfessionalExperienceForm({
  onCancel,
  onCreated,
}: ProfessionalExperienceFormProps) {
  const createMutation = useCreateProfessionalExperience();

  const [companyName, setCompanyName] = useState("");
  const [jobTitle, setJobTitle] = useState("");
  const [employmentType, setEmploymentType] = useState<EmploymentType | "">("");
  const [location, setLocation] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [currentlyWorking, setCurrentlyWorking] = useState(false);
  const [description, setDescription] = useState("");

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const request: CreateProfessionalExperienceRequest = {
      companyName: companyName.trim(),
      jobTitle: jobTitle.trim(),
      employmentType: employmentType || undefined,
      location: location.trim() || undefined,
      startDate,
      endDate: currentlyWorking ? undefined : endDate || undefined,
      currentlyWorking,
      description: description.trim() || undefined,
    };

    createMutation.mutate(request, {
      onSuccess: onCreated,
    });
  };

  const isSaving = createMutation.isPending;

  return (
    <form onSubmit={handleSubmit} className="space-y-5">
      <div className="grid gap-5 md:grid-cols-2">
        <div className="space-y-2">
          <Label htmlFor="companyName">Company name *</Label>
          <Input
            id="companyName"
            value={companyName}
            onChange={(event) => setCompanyName(event.target.value)}
            maxLength={255}
            required
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="jobTitle">Job title *</Label>
          <Input
            id="jobTitle"
            value={jobTitle}
            onChange={(event) => setJobTitle(event.target.value)}
            maxLength={255}
            required
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="employmentType">Employment type</Label>
          <select
            id="employmentType"
            value={employmentType}
            onChange={(event) =>
              setEmploymentType(event.target.value as EmploymentType | "")
            }
            className="h-10 w-full rounded-md border border-border bg-surface px-3 text-sm text-text-primary outline-none focus:border-brand focus:ring-2 focus:ring-brand/20"
          >
            <option value="">Select employment type</option>
            {employmentTypes.map((type) => (
              <option key={type.value} value={type.value}>
                {type.label}
              </option>
            ))}
          </select>
        </div>

        <div className="space-y-2">
          <Label htmlFor="experienceLocation">Location</Label>
          <Input
            id="experienceLocation"
            value={location}
            onChange={(event) => setLocation(event.target.value)}
            maxLength={255}
            placeholder="e.g. Chennai, India"
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="startDate">Start date *</Label>
          <Input
            id="startDate"
            type="date"
            value={startDate}
            onChange={(event) => setStartDate(event.target.value)}
            max={endDate && !currentlyWorking ? endDate : undefined}
            required
          />
        </div>

        {!currentlyWorking && (
          <div className="space-y-2">
            <Label htmlFor="endDate">End date</Label>
            <Input
              id="endDate"
              type="date"
              value={endDate}
              onChange={(event) => setEndDate(event.target.value)}
              min={startDate || undefined}
            />
          </div>
        )}
      </div>

      <label className="flex items-center gap-2 text-sm text-text-primary">
        <input
          type="checkbox"
          checked={currentlyWorking}
          onChange={(event) => {
            setCurrentlyWorking(event.target.checked);
            if (event.target.checked) {
              setEndDate("");
            }
          }}
          className="h-4 w-4 accent-brand"
        />
        I currently work here
      </label>

      <div className="space-y-2">
        <Label htmlFor="experienceDescription">
          Responsibilities and achievements
        </Label>
        <Textarea
          id="experienceDescription"
          value={description}
          onChange={(event) => setDescription(event.target.value)}
          maxLength={10000}
          rows={5}
          placeholder="Describe your responsibilities, contributions and achievements..."
        />
      </div>

      {createMutation.isError && (
        <p role="alert" className="text-sm text-danger">
          {createMutation.error.message ||
            "Unable to create professional experience."}
        </p>
      )}

      <div className="flex justify-end gap-3">
        <Button
          type="button"
          variant="secondary"
          onClick={onCancel}
          disabled={isSaving}
        >
          Cancel
        </Button>

        <Button type="submit" loading={isSaving} disabled={isSaving}>
          Add experience
        </Button>
      </div>
    </form>
  );
}
