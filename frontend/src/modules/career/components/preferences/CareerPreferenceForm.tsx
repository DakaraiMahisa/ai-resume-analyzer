import { useState, type FormEvent } from "react";

import { Button, Input, Label } from "@/components/ui";

import type {
  CandidateCareerPreference,
  CreateCandidateCareerPreferenceRequest,
  JobSearchStatus,
  PreferredEmploymentType,
  PreferredWorkMode,
  SalaryPeriod,
  UpdateCandidateCareerPreferenceRequest,
} from "../../types/preference.types";

type CareerPreferenceFormProps = {
  preference?: CandidateCareerPreference;
  onCreate?: (request: CreateCandidateCareerPreferenceRequest) => void;
  onUpdate: (request: UpdateCandidateCareerPreferenceRequest) => void;
  onCancel?: () => void;
  loading?: boolean;
};

const employmentTypes: { value: PreferredEmploymentType; label: string }[] = [
  { value: "FULL_TIME", label: "Full-time" },
  { value: "PART_TIME", label: "Part-time" },
  { value: "CONTRACT", label: "Contract" },
  { value: "INTERNSHIP", label: "Internship" },
  { value: "TEMPORARY", label: "Temporary" },
  { value: "FREELANCE", label: "Freelance" },
];

const workModes: { value: PreferredWorkMode; label: string }[] = [
  { value: "REMOTE", label: "Remote" },
  { value: "HYBRID", label: "Hybrid" },
  { value: "ONSITE", label: "On-site" },
];

const searchStatuses: { value: JobSearchStatus; label: string }[] = [
  { value: "ACTIVELY_LOOKING", label: "Actively looking" },
  { value: "OPEN_TO_OPPORTUNITIES", label: "Open to opportunities" },
  { value: "NOT_LOOKING", label: "Not looking" },
];

const parseList = (value: string) => [
  ...new Set(
    value
      .split(",")
      .map((item) => item.trim())
      .filter(Boolean),
  ),
];

const formatList = (values: string[]) => values.join(", ");

export const CareerPreferenceForm = ({
  preference,
  onCreate,
  onUpdate,
  onCancel,
  loading = false,
}: CareerPreferenceFormProps) => {
  const [jobTitles, setJobTitles] = useState(
    formatList(preference?.preferredJobTitles ?? []),
  );
  const [employmentTypesSelected, setEmploymentTypesSelected] = useState<
    PreferredEmploymentType[]
  >(preference?.preferredEmploymentTypes ?? []);
  const [workModesSelected, setWorkModesSelected] = useState<
    PreferredWorkMode[]
  >(preference?.preferredWorkModes ?? []);
  const [locations, setLocations] = useState(
    formatList(preference?.preferredLocations ?? []),
  );
  const [industries, setIndustries] = useState(
    formatList(preference?.preferredIndustries ?? []),
  );

  const [minimumSalary, setMinimumSalary] = useState(
    preference?.minimumSalary?.toString() ?? "",
  );
  const [maximumSalary, setMaximumSalary] = useState(
    preference?.maximumSalary?.toString() ?? "",
  );
  const [salaryCurrency, setSalaryCurrency] = useState(
    preference?.salaryCurrency ?? "",
  );
  const [salaryPeriod, setSalaryPeriod] = useState<SalaryPeriod | "">(
    preference?.salaryPeriod ?? "",
  );

  const [willingToRelocate, setWillingToRelocate] = useState<
    "true" | "false" | ""
  >(
    preference?.willingToRelocate == null
      ? ""
      : preference.willingToRelocate
        ? "true"
        : "false",
  );
  const [noticePeriodDays, setNoticePeriodDays] = useState(
    preference?.noticePeriodDays?.toString() ?? "",
  );
  const [jobSearchStatus, setJobSearchStatus] = useState<JobSearchStatus | "">(
    preference?.jobSearchStatus ?? "",
  );

  const toggleValue = <T extends string>(
    values: T[],
    value: T,
    setter: (next: T[]) => void,
  ) => {
    setter(
      values.includes(value)
        ? values.filter((item) => item !== value)
        : [...values, value],
    );
  };

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const commonFields = {
      preferredJobTitles: parseList(jobTitles),
      preferredEmploymentTypes: employmentTypesSelected,
      preferredWorkModes: workModesSelected,
      preferredLocations: parseList(locations),
      preferredIndustries: parseList(industries),
    };

    if (!preference) {
      onCreate?.({
        ...commonFields,
        minimumSalary: minimumSalary === "" ? null : Number(minimumSalary),
        maximumSalary: maximumSalary === "" ? null : Number(maximumSalary),
        salaryCurrency: salaryCurrency.trim() || null,
        salaryPeriod: salaryPeriod || null,
        willingToRelocate:
          willingToRelocate === "" ? null : willingToRelocate === "true",
        noticePeriodDays:
          noticePeriodDays === "" ? null : Number(noticePeriodDays),
        jobSearchStatus: jobSearchStatus || null,
      });

      return;
    }

    const request: UpdateCandidateCareerPreferenceRequest = {
      ...commonFields,
    };

    // Minimum salary
    const nextMinimumSalary =
      minimumSalary === "" ? null : Number(minimumSalary);

    if (nextMinimumSalary !== preference.minimumSalary) {
      if (nextMinimumSalary === null) {
        request.clearMinimumSalary = true;
      } else {
        request.minimumSalary = nextMinimumSalary;
      }
    }

    // Maximum salary
    const nextMaximumSalary =
      maximumSalary === "" ? null : Number(maximumSalary);

    if (nextMaximumSalary !== preference.maximumSalary) {
      if (nextMaximumSalary === null) {
        request.clearMaximumSalary = true;
      } else {
        request.maximumSalary = nextMaximumSalary;
      }
    }

    // Salary currency
    const nextSalaryCurrency = salaryCurrency.trim() || null;

    if (nextSalaryCurrency !== preference.salaryCurrency) {
      if (nextSalaryCurrency === null) {
        request.clearSalaryCurrency = true;
      } else {
        request.salaryCurrency = nextSalaryCurrency;
      }
    }

    // Salary period
    const nextSalaryPeriod = salaryPeriod || null;

    if (nextSalaryPeriod !== preference.salaryPeriod) {
      if (nextSalaryPeriod === null) {
        request.clearSalaryPeriod = true;
      } else {
        request.salaryPeriod = nextSalaryPeriod;
      }
    }

    // Relocation preference
    const nextWillingToRelocate =
      willingToRelocate === "" ? null : willingToRelocate === "true";

    if (nextWillingToRelocate !== preference.willingToRelocate) {
      if (nextWillingToRelocate === null) {
        request.clearWillingToRelocate = true;
      } else {
        request.willingToRelocate = nextWillingToRelocate;
      }
    }

    // Notice period
    const nextNoticePeriodDays =
      noticePeriodDays === "" ? null : Number(noticePeriodDays);

    if (nextNoticePeriodDays !== preference.noticePeriodDays) {
      if (nextNoticePeriodDays === null) {
        request.clearNoticePeriodDays = true;
      } else {
        request.noticePeriodDays = nextNoticePeriodDays;
      }
    }

    // Job search status
    const nextJobSearchStatus = jobSearchStatus || null;

    if (nextJobSearchStatus !== preference.jobSearchStatus) {
      if (nextJobSearchStatus === null) {
        request.clearJobSearchStatus = true;
      } else {
        request.jobSearchStatus = nextJobSearchStatus;
      }
    }

    onUpdate(request);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div className="space-y-2">
        <Label htmlFor="preferredJobTitles">Preferred job titles</Label>
        <Input
          id="preferredJobTitles"
          value={jobTitles}
          onChange={(event) => setJobTitles(event.target.value)}
          placeholder="Java Developer, Backend Engineer"
        />
        <p className="text-xs text-muted-foreground">
          Separate multiple titles with commas.
        </p>
      </div>

      <div className="space-y-3">
        <Label>Employment types</Label>
        <div className="grid grid-cols-2 gap-3 sm:grid-cols-3">
          {employmentTypes.map((type) => (
            <label key={type.value} className="flex items-center gap-2 text-sm">
              <input
                type="checkbox"
                checked={employmentTypesSelected.includes(type.value)}
                onChange={() =>
                  toggleValue(
                    employmentTypesSelected,
                    type.value,
                    setEmploymentTypesSelected,
                  )
                }
              />
              {type.label}
            </label>
          ))}
        </div>
      </div>

      <div className="space-y-3">
        <Label>Preferred work modes</Label>
        <div className="flex flex-wrap gap-4">
          {workModes.map((mode) => (
            <label key={mode.value} className="flex items-center gap-2 text-sm">
              <input
                type="checkbox"
                checked={workModesSelected.includes(mode.value)}
                onChange={() =>
                  toggleValue(
                    workModesSelected,
                    mode.value,
                    setWorkModesSelected,
                  )
                }
              />
              {mode.label}
            </label>
          ))}
        </div>
      </div>

      <div className="space-y-2">
        <Label htmlFor="preferredLocations">Preferred locations</Label>
        <Input
          id="preferredLocations"
          value={locations}
          onChange={(event) => setLocations(event.target.value)}
          placeholder="Bengaluru, Chennai, Remote"
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="preferredIndustries">Preferred industries</Label>
        <Input
          id="preferredIndustries"
          value={industries}
          onChange={(event) => setIndustries(event.target.value)}
          placeholder="Software, FinTech, E-commerce"
        />
      </div>

      <div className="space-y-3">
        <Label>Expected salary</Label>
        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
          <div className="space-y-2">
            <Label htmlFor="minimumSalary">Minimum salary</Label>
            <Input
              id="minimumSalary"
              type="number"
              min="0"
              step="0.01"
              value={minimumSalary}
              onChange={(event) => setMinimumSalary(event.target.value)}
              placeholder="Minimum"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="maximumSalary">Maximum salary</Label>
            <Input
              id="maximumSalary"
              type="number"
              min="0"
              step="0.01"
              value={maximumSalary}
              onChange={(event) => setMaximumSalary(event.target.value)}
              placeholder="Maximum"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="salaryCurrency">Currency</Label>
            <Input
              id="salaryCurrency"
              value={salaryCurrency}
              onChange={(event) =>
                setSalaryCurrency(event.target.value.toUpperCase())
              }
              maxLength={3}
              placeholder="INR"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="salaryPeriod">Salary period</Label>
            <select
              id="salaryPeriod"
              value={salaryPeriod}
              onChange={(event) =>
                setSalaryPeriod(event.target.value as SalaryPeriod | "")
              }
              className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
            >
              <option value="">Not specified</option>
              <option value="MONTHLY">Monthly</option>
              <option value="ANNUAL">Annual</option>
            </select>
          </div>
        </div>
      </div>

      <div className="space-y-2">
        <Label htmlFor="willingToRelocate">Willing to relocate?</Label>
        <select
          id="willingToRelocate"
          value={willingToRelocate}
          onChange={(event) =>
            setWillingToRelocate(event.target.value as "true" | "false" | "")
          }
          className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
        >
          <option value="">Not specified</option>
          <option value="true">Yes</option>
          <option value="false">No</option>
        </select>
      </div>

      <div className="space-y-2">
        <Label htmlFor="noticePeriodDays">Notice period (days)</Label>
        <Input
          id="noticePeriodDays"
          type="number"
          min="0"
          step="1"
          value={noticePeriodDays}
          onChange={(event) => setNoticePeriodDays(event.target.value)}
          placeholder="e.g. 30"
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="jobSearchStatus">Job search status</Label>
        <select
          id="jobSearchStatus"
          value={jobSearchStatus}
          onChange={(event) =>
            setJobSearchStatus(event.target.value as JobSearchStatus | "")
          }
          className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
        >
          <option value="">Not specified</option>
          {searchStatuses.map((status) => (
            <option key={status.value} value={status.value}>
              {status.label}
            </option>
          ))}
        </select>
      </div>

      <div className="flex justify-end gap-2">
        {onCancel && (
          <Button type="button" variant="secondary" onClick={onCancel}>
            Cancel
          </Button>
        )}

        <Button type="submit" loading={loading}>
          {preference ? "Save preferences" : "Create preferences"}
        </Button>
      </div>
    </form>
  );
};
