import { useState } from "react";
import axios from "axios";

import { Button, Card } from "@/components/ui";
import { EmptyState } from "@/components/feedback/EmptyState";
import { ErrorState } from "@/components/feedback/ErrorState";
import { LoadingState } from "@/components/feedback/LoadingState";

import { useCareerPreferences } from "../../hooks/use-career-preferences";
import { useCareerPreferenceMutations } from "../../hooks/use-career-preference-mutations";

import { CareerPreferenceForm } from "./CareerPreferenceForm";
import { CareerPreferenceEditForm } from "./CareerPreferenceEditForm";

import type {
  CreateCandidateCareerPreferenceRequest,
  UpdateCandidateCareerPreferenceRequest,
} from "../../types/preference.types";

const formatEnumLabel = (value: string) =>
  value
    .toLowerCase()
    .split("_")
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join(" ");

export const CareerPreferencesSection = () => {
  const [isEditing, setIsEditing] = useState(false);
  const [isCreating, setIsCreating] = useState(false);

  const {
    data: preference,
    isLoading,
    isError,
    error,
    refetch,
  } = useCareerPreferences();

  const { createPreferences, updatePreferences } =
    useCareerPreferenceMutations();

  const isNotFound =
    axios.isAxiosError(error) && error.response?.status === 404;

  const handleCreate = (request: CreateCandidateCareerPreferenceRequest) => {
    createPreferences.mutate(request, {
      onSuccess: () => setIsCreating(false),
    });
  };

  const handleUpdate = (request: UpdateCandidateCareerPreferenceRequest) => {
    updatePreferences.mutate(request, {
      onSuccess: () => setIsEditing(false),
    });
  };

  if (isLoading) {
    return <LoadingState />;
  }

  if (isError && !isNotFound) {
    return (
      <ErrorState
        title="Unable to load career preferences"
        description="Something went wrong while retrieving your career preferences."
        actionLabel="Try again"
        onAction={() => refetch()}
      />
    );
  }

  if (isNotFound || !preference) {
    if (!isCreating) {
      return (
        <Card className="space-y-4 p-6">
          <EmptyState
            title="No career preferences yet"
            description="Add your preferred job titles, employment types, work modes, salary expectations, and job search status."
          />

          <div className="flex justify-center">
            <Button onClick={() => setIsCreating(true)}>
              Add career preferences
            </Button>
          </div>
        </Card>
      );
    }

    return (
      <Card className="p-6">
        <CareerPreferenceForm
          onCreate={handleCreate}
          onUpdate={() => {}}
          onCancel={() => setIsCreating(false)}
          loading={createPreferences.isPending}
        />
      </Card>
    );
  }

  if (isEditing) {
    return (
      <Card className="p-6">
        <CareerPreferenceEditForm
          preference={preference}
          onUpdate={handleUpdate}
          onCancel={() => setIsEditing(false)}
          loading={updatePreferences.isPending}
        />
      </Card>
    );
  }

  return (
    <Card className="space-y-6 p-6">
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-semibold">Career preferences</h3>

        <Button variant="secondary" onClick={() => setIsEditing(true)}>
          Edit preferences
        </Button>
      </div>

      <div className="grid grid-cols-1 gap-x-8 gap-y-5 md:grid-cols-2">
        <div className="space-y-1">
          <p className="text-sm font-medium">Preferred job titles</p>
          <p className="text-sm text-muted-foreground">
            {preference.preferredJobTitles.length
              ? preference.preferredJobTitles.join(", ")
              : "Not specified"}
          </p>
        </div>

        <div className="space-y-1">
          <p className="text-sm font-medium">Employment types</p>
          <p className="text-sm text-muted-foreground">
            {preference.preferredEmploymentTypes.length
              ? preference.preferredEmploymentTypes
                  .map(formatEnumLabel)
                  .join(", ")
              : "Not specified"}
          </p>
        </div>

        <div className="space-y-1">
          <p className="text-sm font-medium">Work modes</p>
          <p className="text-sm text-muted-foreground">
            {preference.preferredWorkModes.length
              ? preference.preferredWorkModes.map(formatEnumLabel).join(", ")
              : "Not specified"}
          </p>
        </div>

        <div className="space-y-1">
          <p className="text-sm font-medium">Preferred locations</p>
          <p className="text-sm text-muted-foreground">
            {preference.preferredLocations.length
              ? preference.preferredLocations.join(", ")
              : "Not specified"}
          </p>
        </div>

        <div className="space-y-1">
          <p className="text-sm font-medium">Preferred industries</p>
          <p className="text-sm text-muted-foreground">
            {preference.preferredIndustries.length
              ? preference.preferredIndustries.join(", ")
              : "Not specified"}
          </p>
        </div>

        <div className="space-y-1">
          <p className="text-sm font-medium">Expected salary</p>
          <p className="text-sm text-muted-foreground">
            {preference.minimumSalary != null ||
            preference.maximumSalary != null
              ? `${preference.minimumSalary ?? "Any"} - ${
                  preference.maximumSalary ?? "Any"
                } ${preference.salaryCurrency ?? ""} ${
                  preference.salaryPeriod
                    ? formatEnumLabel(preference.salaryPeriod)
                    : ""
                }`
              : "Not specified"}
          </p>
        </div>

        <div className="space-y-1">
          <p className="text-sm font-medium">Willing to relocate</p>
          <p className="text-sm text-muted-foreground">
            {preference.willingToRelocate == null
              ? "Not specified"
              : preference.willingToRelocate
                ? "Yes"
                : "No"}
          </p>
        </div>

        <div className="space-y-1">
          <p className="text-sm font-medium">Notice period</p>
          <p className="text-sm text-muted-foreground">
            {preference.noticePeriodDays != null
              ? `${preference.noticePeriodDays} days`
              : "Not specified"}
          </p>
        </div>

        <div className="space-y-1">
          <p className="text-sm font-medium">Job search status</p>
          <p className="text-sm text-muted-foreground">
            {preference.jobSearchStatus
              ? formatEnumLabel(preference.jobSearchStatus)
              : "Not specified"}
          </p>
        </div>
      </div>
    </Card>
  );
};
