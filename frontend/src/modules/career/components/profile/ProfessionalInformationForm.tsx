import { useState } from "react";

import { Button, Input, Label, Textarea } from "@/components/ui";
import {
  useCreateCandidateProfile,
  useUpdateCandidateProfile,
} from "@/modules/career/hooks/use-profile-mutations";
import type {
  CandidateProfile,
  CreateCandidateProfileRequest,
  UpdateCandidateProfileRequest,
} from "@/modules/career/types/profile.types";

type ProfessionalInformationFormProps = {
  profile: CandidateProfile | undefined;
};

type ProfileFormValues = {
  professionalHeadline: string;
  professionalSummary: string;
  phone: string;
  location: string;
  linkedinUrl: string;
  githubUrl: string;
  portfolioUrl: string;
  careerObjective: string;
};

const toFormValues = (
  profile: CandidateProfile | undefined,
): ProfileFormValues => ({
  professionalHeadline: profile?.professionalHeadline ?? "",
  professionalSummary: profile?.professionalSummary ?? "",
  phone: profile?.phone ?? "",
  location: profile?.location ?? "",
  linkedinUrl: profile?.linkedinUrl ?? "",
  githubUrl: profile?.githubUrl ?? "",
  portfolioUrl: profile?.portfolioUrl ?? "",
  careerObjective: profile?.careerObjective ?? "",
});

export function ProfessionalInformationForm({
  profile,
}: ProfessionalInformationFormProps) {
  const [values, setValues] = useState<ProfileFormValues>(() =>
    toFormValues(profile),
  );

  const createMutation = useCreateCandidateProfile();
  const updateMutation = useUpdateCandidateProfile();

  const isSaving = createMutation.isPending || updateMutation.isPending;

  const handleChange = (field: keyof ProfileFormValues, value: string) => {
    setValues((current) => ({
      ...current,
      [field]: value,
    }));
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (profile) {
      const request: UpdateCandidateProfileRequest = {};

      (Object.keys(values) as (keyof ProfileFormValues)[]).forEach((field) => {
        if (values[field] !== (profile[field] ?? "")) {
          request[field] = values[field];
        }
      });

      if (Object.keys(request).length === 0) {
        return;
      }

      await updateMutation.mutateAsync(request);
      return;
    }

    const request: CreateCandidateProfileRequest = values;
    await createMutation.mutateAsync(request);
  };

  const mutationError = createMutation.error ?? updateMutation.error;

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div className="grid gap-5 md:grid-cols-2">
        <div className="space-y-2 md:col-span-2">
          <Label htmlFor="professionalHeadline">Professional headline</Label>
          <Input
            id="professionalHeadline"
            value={values.professionalHeadline}
            onChange={(event) =>
              handleChange("professionalHeadline", event.target.value)
            }
            placeholder="e.g. Java Backend Developer"
            maxLength={255}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="phone">Phone</Label>
          <Input
            id="phone"
            type="tel"
            value={values.phone}
            onChange={(event) => handleChange("phone", event.target.value)}
            placeholder="Your contact number"
            maxLength={30}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="location">Location</Label>
          <Input
            id="location"
            value={values.location}
            onChange={(event) => handleChange("location", event.target.value)}
            placeholder="e.g. Chennai, India"
            maxLength={255}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="linkedinUrl">LinkedIn URL</Label>
          <Input
            id="linkedinUrl"
            type="url"
            value={values.linkedinUrl}
            onChange={(event) =>
              handleChange("linkedinUrl", event.target.value)
            }
            placeholder="https://linkedin.com/in/your-profile"
            maxLength={500}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="githubUrl">GitHub URL</Label>
          <Input
            id="githubUrl"
            type="url"
            value={values.githubUrl}
            onChange={(event) => handleChange("githubUrl", event.target.value)}
            placeholder="https://github.com/your-username"
            maxLength={500}
          />
        </div>

        <div className="space-y-2 md:col-span-2">
          <Label htmlFor="portfolioUrl">Portfolio URL</Label>
          <Input
            id="portfolioUrl"
            type="url"
            value={values.portfolioUrl}
            onChange={(event) =>
              handleChange("portfolioUrl", event.target.value)
            }
            placeholder="https://your-portfolio.com"
            maxLength={500}
          />
        </div>

        <div className="space-y-2 md:col-span-2">
          <Label htmlFor="professionalSummary">Professional summary</Label>
          <Textarea
            id="professionalSummary"
            value={values.professionalSummary}
            onChange={(event) =>
              handleChange("professionalSummary", event.target.value)
            }
            placeholder="Describe your professional background, strengths and experience..."
            maxLength={10000}
            rows={5}
          />
        </div>

        <div className="space-y-2 md:col-span-2">
          <Label htmlFor="careerObjective">Career objective</Label>
          <Textarea
            id="careerObjective"
            value={values.careerObjective}
            onChange={(event) =>
              handleChange("careerObjective", event.target.value)
            }
            placeholder="Describe your career goals and the opportunities you are pursuing..."
            maxLength={10000}
            rows={5}
          />
        </div>
      </div>

      {mutationError && (
        <p role="alert" className="text-sm text-danger">
          {mutationError.message || "Unable to save professional information."}
        </p>
      )}

      <div className="flex justify-end">
        <Button
          type="submit"
          loading={isSaving}
          disabled={
            isSaving ||
            (profile !== undefined &&
              (Object.keys(values) as (keyof ProfileFormValues)[]).every(
                (field) => values[field] === (profile[field] ?? ""),
              ))
          }
        >
          {profile ? "Save changes" : "Create profile"}
        </Button>
      </div>
    </form>
  );
}
