import { useState, type FormEvent } from "react";

import { Button, Input, Label, Textarea } from "@/components/ui";
import { useUpdateProfessionalExperience } from "@/modules/career/hooks/use-experience-mutations";

import type {
  EmploymentType,
  ProfessionalExperience,
  UpdateProfessionalExperienceRequest,
} from "@/modules/career/types/experience.types";

type ProfessionalExperienceEditFormProps = {
  experience: ProfessionalExperience;
  onCancel: () => void;
  onUpdated: () => void;
};

type FormValues = {
  companyName: string;
  jobTitle: string;
  employmentType: EmploymentType | "";
  location: string;
  startDate: string;
  endDate: string;
  currentlyWorking: boolean;
  description: string;
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

const toFormValues = (experience: ProfessionalExperience): FormValues => ({
  companyName: experience.companyName,
  jobTitle: experience.jobTitle,
  employmentType: experience.employmentType ?? "",
  location: experience.location ?? "",
  startDate: experience.startDate,
  endDate: experience.endDate ?? "",
  currentlyWorking: experience.currentlyWorking,
  description: experience.description ?? "",
});

export function ProfessionalExperienceEditForm({
  experience,
  onCancel,
  onUpdated,
}: ProfessionalExperienceEditFormProps) {
  const updateMutation = useUpdateProfessionalExperience();

  const [values, setValues] = useState<FormValues>(() =>
    toFormValues(experience),
  );

  const handleChange = <K extends keyof FormValues>(
    field: K,
    value: FormValues[K],
  ) => {
    setValues((current) => ({
      ...current,
      [field]: value,
    }));
  };

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const original = toFormValues(experience);
    const request: UpdateProfessionalExperienceRequest = {};

    if (values.companyName !== original.companyName) {
      request.companyName = values.companyName.trim();
    }

    if (values.jobTitle !== original.jobTitle) {
      request.jobTitle = values.jobTitle.trim();
    }

    if (values.employmentType !== original.employmentType) {
      request.employmentType = values.employmentType || null;
    }

    if (values.location !== original.location) {
      request.location = values.location;
    }

    if (values.startDate !== original.startDate) {
      request.startDate = values.startDate;
    }

    if (values.currentlyWorking !== original.currentlyWorking) {
      request.currentlyWorking = values.currentlyWorking;
    }

    if (values.currentlyWorking) {
      if (!original.currentlyWorking || values.endDate !== original.endDate) {
        request.currentlyWorking = true;
      }
    } else if (values.endDate !== original.endDate) {
      request.endDate = values.endDate;
    }

    if (values.description !== original.description) {
      request.description = values.description;
    }

    if (Object.keys(request).length === 0) {
      onCancel();
      return;
    }

    updateMutation.mutate(
      {
        experienceId: experience.id,
        request,
      },
      {
        onSuccess: onUpdated,
      },
    );
  };

  const isSaving = updateMutation.isPending;

  return (
    <form onSubmit={handleSubmit} className="space-y-5">
      <div className="grid gap-5 md:grid-cols-2">
        <div className="space-y-2">
          <Label htmlFor="editCompanyName">Company name *</Label>
          <Input
            id="editCompanyName"
            value={values.companyName}
            onChange={(event) =>
              handleChange("companyName", event.target.value)
            }
            maxLength={255}
            required
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="editJobTitle">Job title *</Label>
          <Input
            id="editJobTitle"
            value={values.jobTitle}
            onChange={(event) => handleChange("jobTitle", event.target.value)}
            maxLength={255}
            required
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="editEmploymentType">Employment type</Label>
          <select
            id="editEmploymentType"
            value={values.employmentType}
            onChange={(event) =>
              handleChange(
                "employmentType",
                event.target.value as EmploymentType | "",
              )
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
          <Label htmlFor="editExperienceLocation">Location</Label>
          <Input
            id="editExperienceLocation"
            value={values.location}
            onChange={(event) => handleChange("location", event.target.value)}
            maxLength={255}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="editStartDate">Start date *</Label>
          <Input
            id="editStartDate"
            type="date"
            value={values.startDate}
            onChange={(event) => handleChange("startDate", event.target.value)}
            max={
              !values.currentlyWorking && values.endDate
                ? values.endDate
                : undefined
            }
            required
          />
        </div>

        {!values.currentlyWorking && (
          <div className="space-y-2">
            <Label htmlFor="editEndDate">End date *</Label>
            <Input
              id="editEndDate"
              type="date"
              value={values.endDate}
              onChange={(event) => handleChange("endDate", event.target.value)}
              min={values.startDate || undefined}
              required
            />
          </div>
        )}
      </div>

      <label className="flex items-center gap-2 text-sm text-text-primary">
        <input
          type="checkbox"
          checked={values.currentlyWorking}
          onChange={(event) => {
            const checked = event.target.checked;

            handleChange("currentlyWorking", checked);

            if (checked) {
              handleChange("endDate", "");
            }
          }}
          className="h-4 w-4 accent-brand"
        />
        I currently work here
      </label>

      <div className="space-y-2">
        <Label htmlFor="editExperienceDescription">
          Responsibilities and achievements
        </Label>
        <Textarea
          id="editExperienceDescription"
          value={values.description}
          onChange={(event) => handleChange("description", event.target.value)}
          maxLength={10000}
          rows={5}
        />
      </div>

      {updateMutation.isError && (
        <p role="alert" className="text-sm text-danger">
          {updateMutation.error.message ||
            "Unable to update professional experience."}
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
          Save changes
        </Button>
      </div>
    </form>
  );
}
