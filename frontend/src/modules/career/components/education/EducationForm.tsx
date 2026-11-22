import { useState, type FormEvent } from "react";

import { Button, Input, Label, Textarea } from "@/components/ui";
import { useCreateEducation } from "@/modules/career/hooks/use-education-mutations";

type EducationFormProps = {
  onCancel: () => void;
  onCreated: () => void;
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

const initialValues: FormValues = {
  institution: "",
  degree: "",
  fieldOfStudy: "",
  location: "",
  startDate: "",
  endDate: "",
  currentlyStudying: false,
  grade: "",
  description: "",
};

export function EducationForm({ onCancel, onCreated }: EducationFormProps) {
  const createMutation = useCreateEducation();
  const [values, setValues] = useState<FormValues>(initialValues);

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

    createMutation.mutate(
      {
        institution: values.institution.trim(),
        degree: values.degree.trim(),
        fieldOfStudy: values.fieldOfStudy.trim() || undefined,
        location: values.location.trim() || undefined,
        startDate: values.startDate,
        endDate: values.currentlyStudying ? undefined : values.endDate,
        currentlyStudying: values.currentlyStudying,
        grade: values.grade.trim() || undefined,
        description: values.description.trim() || undefined,
      },
      {
        onSuccess: onCreated,
      },
    );
  };

  const isSaving = createMutation.isPending;

  return (
    <form onSubmit={handleSubmit} className="space-y-5">
      <div className="grid gap-5 md:grid-cols-2">
        <div className="space-y-2">
          <Label htmlFor="educationInstitution">Institution *</Label>
          <Input
            id="educationInstitution"
            value={values.institution}
            onChange={(event) =>
              handleChange("institution", event.target.value)
            }
            maxLength={255}
            required
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="educationDegree">Degree *</Label>
          <Input
            id="educationDegree"
            value={values.degree}
            onChange={(event) => handleChange("degree", event.target.value)}
            maxLength={150}
            required
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="educationFieldOfStudy">Field of study</Label>
          <Input
            id="educationFieldOfStudy"
            value={values.fieldOfStudy}
            onChange={(event) =>
              handleChange("fieldOfStudy", event.target.value)
            }
            maxLength={255}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="educationLocation">Location</Label>
          <Input
            id="educationLocation"
            value={values.location}
            onChange={(event) => handleChange("location", event.target.value)}
            maxLength={255}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="educationStartDate">Start date *</Label>
          <Input
            id="educationStartDate"
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
            <Label htmlFor="educationEndDate">End date *</Label>
            <Input
              id="educationEndDate"
              type="date"
              value={values.endDate}
              onChange={(event) => handleChange("endDate", event.target.value)}
              min={values.startDate || undefined}
              required
            />
          </div>
        )}

        <div className="space-y-2">
          <Label htmlFor="educationGrade">Grade</Label>
          <Input
            id="educationGrade"
            value={values.grade}
            onChange={(event) => handleChange("grade", event.target.value)}
            maxLength={100}
            placeholder="e.g. 8.5 CGPA, First Class"
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
        <Label htmlFor="educationDescription">Description</Label>
        <Textarea
          id="educationDescription"
          value={values.description}
          onChange={(event) => handleChange("description", event.target.value)}
          maxLength={10000}
          rows={4}
        />
      </div>

      {createMutation.isError && (
        <p role="alert" className="text-sm text-danger">
          {createMutation.error.message || "Unable to create education record."}
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
          Save education
        </Button>
      </div>
    </form>
  );
}
