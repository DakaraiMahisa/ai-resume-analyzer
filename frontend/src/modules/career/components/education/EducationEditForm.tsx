import { useState, type FormEvent } from "react";

import { Button, Input, Label, Textarea } from "@/components/ui";
import { useUpdateEducation } from "@/modules/career/hooks/use-education-mutations";

import type {
  Education,
  UpdateEducationRequest,
} from "@/modules/career/types/education.types";

type EducationEditFormProps = {
  education: Education;
  onCancel: () => void;
  onUpdated: () => void;
};

type FormValues = {
  institution: string;
  degree: string;
  fieldOfStudy: string;
  location: string;
  startDate: string;
  endDate: string;
  currentlyStudying: boolean;
  grade: string;
  description: string;
};

const toFormValues = (education: Education): FormValues => ({
  institution: education.institution,
  degree: education.degree,
  fieldOfStudy: education.fieldOfStudy ?? "",
  location: education.location ?? "",
  startDate: education.startDate,
  endDate: education.endDate ?? "",
  currentlyStudying: education.currentlyStudying,
  grade: education.grade ?? "",
  description: education.description ?? "",
});

export function EducationEditForm({
  education,
  onCancel,
  onUpdated,
}: EducationEditFormProps) {
  const updateMutation = useUpdateEducation();

  const [values, setValues] = useState<FormValues>(() =>
    toFormValues(education),
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

    const original = toFormValues(education);
    const request: UpdateEducationRequest = {};

    if (values.institution !== original.institution) {
      request.institution = values.institution.trim();
    }

    if (values.degree !== original.degree) {
      request.degree = values.degree.trim();
    }

    if (values.fieldOfStudy !== original.fieldOfStudy) {
      request.fieldOfStudy = values.fieldOfStudy;
    }

    if (values.location !== original.location) {
      request.location = values.location;
    }

    if (values.startDate !== original.startDate) {
      request.startDate = values.startDate;
    }

    if (values.currentlyStudying !== original.currentlyStudying) {
      request.currentlyStudying = values.currentlyStudying;
    }

    if (values.currentlyStudying) {
      if (!original.currentlyStudying) {
        request.currentlyStudying = true;
      }
    } else if (
      values.endDate !== original.endDate ||
      original.currentlyStudying
    ) {
      request.endDate = values.endDate;
    }

    if (values.grade !== original.grade) {
      request.grade = values.grade;
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
        educationId: education.id,
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
          <Label htmlFor="editEducationInstitution">Institution *</Label>
          <Input
            id="editEducationInstitution"
            value={values.institution}
            onChange={(event) =>
              handleChange("institution", event.target.value)
            }
            maxLength={255}
            required
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="editEducationDegree">Degree *</Label>
          <Input
            id="editEducationDegree"
            value={values.degree}
            onChange={(event) => handleChange("degree", event.target.value)}
            maxLength={150}
            required
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="editEducationFieldOfStudy">Field of study</Label>
          <Input
            id="editEducationFieldOfStudy"
            value={values.fieldOfStudy}
            onChange={(event) =>
              handleChange("fieldOfStudy", event.target.value)
            }
            maxLength={255}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="editEducationLocation">Location</Label>
          <Input
            id="editEducationLocation"
            value={values.location}
            onChange={(event) => handleChange("location", event.target.value)}
            maxLength={255}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="editEducationStartDate">Start date *</Label>
          <Input
            id="editEducationStartDate"
            type="date"
            value={values.startDate}
            onChange={(event) => handleChange("startDate", event.target.value)}
            max={
              !values.currentlyStudying && values.endDate
                ? values.endDate
                : undefined
            }
            required
          />
        </div>

        {!values.currentlyStudying && (
          <div className="space-y-2">
            <Label htmlFor="editEducationEndDate">End date *</Label>
            <Input
              id="editEducationEndDate"
              type="date"
              value={values.endDate}
              onChange={(event) => handleChange("endDate", event.target.value)}
              min={values.startDate || undefined}
              required
            />
          </div>
        )}

        <div className="space-y-2">
          <Label htmlFor="editEducationGrade">Grade</Label>
          <Input
            id="editEducationGrade"
            value={values.grade}
            onChange={(event) => handleChange("grade", event.target.value)}
            maxLength={100}
          />
        </div>
      </div>

      <label className="flex items-center gap-2 text-sm text-text-primary">
        <input
          type="checkbox"
          checked={values.currentlyStudying}
          onChange={(event) => {
            const checked = event.target.checked;
            handleChange("currentlyStudying", checked);

            if (checked) {
              handleChange("endDate", "");
            }
          }}
          className="h-4 w-4 accent-brand"
        />
        I am currently studying here
      </label>

      <div className="space-y-2">
        <Label htmlFor="editEducationDescription">Description</Label>
        <Textarea
          id="editEducationDescription"
          value={values.description}
          onChange={(event) => handleChange("description", event.target.value)}
          maxLength={10000}
          rows={4}
        />
      </div>

      {updateMutation.isError && (
        <p role="alert" className="text-sm text-danger">
          {updateMutation.error.message || "Unable to update education record."}
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
