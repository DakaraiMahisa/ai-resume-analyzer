import { useState, type FormEvent } from "react";

import { Button, Input, Label, Textarea } from "@/components/ui";

import type {
  AchievementType,
  CreateCandidateAchievementRequest,
} from "../../types/achievement.types";

type AchievementFormProps = {
  onSubmit: (request: CreateCandidateAchievementRequest) => void;
  onCancel: () => void;
  loading?: boolean;
};

const achievementTypes: { value: AchievementType; label: string }[] = [
  { value: "AWARD", label: "Award" },
  { value: "HONOR", label: "Honor" },
  { value: "COMPETITION", label: "Competition" },
  { value: "SCHOLARSHIP", label: "Scholarship" },
  { value: "RECOGNITION", label: "Recognition" },
  { value: "OTHER", label: "Other" },
];

export const AchievementForm = ({
  onSubmit,
  onCancel,
  loading = false,
}: AchievementFormProps) => {
  const [achievementName, setAchievementName] = useState("");
  const [issuingOrganization, setIssuingOrganization] = useState("");
  const [achievementType, setAchievementType] =
    useState<AchievementType>("AWARD");
  const [issueDate, setIssueDate] = useState("");
  const [description, setDescription] = useState("");
  const [achievementUrl, setAchievementUrl] = useState("");

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    onSubmit({
      achievementName: achievementName.trim(),
      issuingOrganization: issuingOrganization.trim() || null,
      achievementType,
      issueDate: issueDate || null,
      description: description.trim() || null,
      achievementUrl: achievementUrl.trim() || null,
    });
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="achievementName">Achievement name</Label>
        <Input
          id="achievementName"
          value={achievementName}
          onChange={(event) => setAchievementName(event.target.value)}
          placeholder="e.g. National Academic Excellence Award"
          maxLength={255}
          required
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="achievementType">Achievement type</Label>
        <select
          id="achievementType"
          value={achievementType}
          onChange={(event) =>
            setAchievementType(event.target.value as AchievementType)
          }
          className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
          required
        >
          {achievementTypes.map((type) => (
            <option key={type.value} value={type.value}>
              {type.label}
            </option>
          ))}
        </select>
      </div>

      <div className="space-y-2">
        <Label htmlFor="issuingOrganization">
          Issuing organization (optional)
        </Label>
        <Input
          id="issuingOrganization"
          value={issuingOrganization}
          onChange={(event) => setIssuingOrganization(event.target.value)}
          placeholder="e.g. Ministry of Education"
          maxLength={255}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="issueDate">Issue date (optional)</Label>
        <Input
          id="issueDate"
          type="date"
          value={issueDate}
          onChange={(event) => setIssueDate(event.target.value)}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="achievementUrl">Achievement URL (optional)</Label>
        <Input
          id="achievementUrl"
          type="url"
          value={achievementUrl}
          onChange={(event) => setAchievementUrl(event.target.value)}
          placeholder="https://..."
          maxLength={500}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="description">Description (optional)</Label>
        <Textarea
          id="description"
          value={description}
          onChange={(event) => setDescription(event.target.value)}
          placeholder="Describe this achievement..."
        />
      </div>

      <div className="flex justify-end gap-2">
        <Button type="button" variant="secondary" onClick={onCancel}>
          Cancel
        </Button>

        <Button type="submit" loading={loading}>
          Add achievement
        </Button>
      </div>
    </form>
  );
};
