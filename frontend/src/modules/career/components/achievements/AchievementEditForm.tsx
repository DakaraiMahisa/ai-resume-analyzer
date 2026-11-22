import { useState, type FormEvent } from "react";

import { Button, Input, Label, Textarea } from "@/components/ui";

import type {
  AchievementType,
  CandidateAchievement,
  UpdateCandidateAchievementRequest,
} from "../../types/achievement.types";

type AchievementEditFormProps = {
  achievement: CandidateAchievement;
  onSubmit: (request: UpdateCandidateAchievementRequest) => void;
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

export const AchievementEditForm = ({
  achievement,
  onSubmit,
  onCancel,
  loading = false,
}: AchievementEditFormProps) => {
  const [achievementName, setAchievementName] = useState(
    achievement.achievementName,
  );
  const [issuingOrganization, setIssuingOrganization] = useState(
    achievement.issuingOrganization ?? "",
  );
  const [achievementType, setAchievementType] = useState<AchievementType>(
    achievement.achievementType,
  );
  const [issueDate, setIssueDate] = useState(achievement.issueDate ?? "");
  const [description, setDescription] = useState(achievement.description ?? "");
  const [achievementUrl, setAchievementUrl] = useState(
    achievement.achievementUrl ?? "",
  );

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const request: UpdateCandidateAchievementRequest = {};

    if (achievementName.trim() !== achievement.achievementName) {
      request.achievementName = achievementName.trim();
    }

    if (issuingOrganization !== (achievement.issuingOrganization ?? "")) {
      request.issuingOrganization = issuingOrganization.trim();
    }

    if (achievementType !== achievement.achievementType) {
      request.achievementType = achievementType;
    }

    if (issueDate && issueDate !== (achievement.issueDate ?? "")) {
      request.issueDate = issueDate;
    }

    if (description !== (achievement.description ?? "")) {
      request.description = description.trim();
    }

    if (achievementUrl !== (achievement.achievementUrl ?? "")) {
      request.achievementUrl = achievementUrl.trim();
    }

    onSubmit(request);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="editAchievementName">Achievement name</Label>
        <Input
          id="editAchievementName"
          value={achievementName}
          onChange={(event) => setAchievementName(event.target.value)}
          maxLength={255}
          required
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="editAchievementType">Achievement type</Label>
        <select
          id="editAchievementType"
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
        <Label htmlFor="editIssuingOrganization">
          Issuing organization (optional)
        </Label>
        <Input
          id="editIssuingOrganization"
          value={issuingOrganization}
          onChange={(event) => setIssuingOrganization(event.target.value)}
          maxLength={255}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="editIssueDate">Issue date (optional)</Label>
        <Input
          id="editIssueDate"
          type="date"
          value={issueDate}
          onChange={(event) => setIssueDate(event.target.value)}
        />
        {achievement.issueDate && (
          <p className="text-xs text-muted-foreground">
            An existing issue date cannot currently be cleared.
          </p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="editAchievementUrl">Achievement URL (optional)</Label>
        <Input
          id="editAchievementUrl"
          type="url"
          value={achievementUrl}
          onChange={(event) => setAchievementUrl(event.target.value)}
          maxLength={500}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="editAchievementDescription">
          Description (optional)
        </Label>
        <Textarea
          id="editAchievementDescription"
          value={description}
          onChange={(event) => setDescription(event.target.value)}
        />
      </div>

      <div className="flex justify-end gap-2">
        <Button type="button" variant="secondary" onClick={onCancel}>
          Cancel
        </Button>

        <Button type="submit" loading={loading}>
          Save changes
        </Button>
      </div>
    </form>
  );
};
