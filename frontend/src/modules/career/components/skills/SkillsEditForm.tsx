import { useState, type FormEvent } from "react";

import { Button, Input, Label } from "@/components/ui";
import { useUpdateSkill } from "@/modules/career/hooks/use-skill-mutations";

import type {
  CandidateSkill,
  ProficiencyLevel,
  SkillCategory,
  UpdateCandidateSkillRequest,
} from "@/modules/career/types/skill.types";

type SkillsEditFormProps = {
  skill: CandidateSkill;
  onCancel: () => void;
  onUpdated: () => void;
};

const categories: { value: SkillCategory; label: string }[] = [
  { value: "TECHNICAL", label: "Technical" },
  { value: "SOFT", label: "Soft skill" },
  { value: "LANGUAGE", label: "Language" },
  { value: "TOOL", label: "Tool" },
  { value: "DOMAIN", label: "Domain" },
];

const proficiencyLevels: { value: ProficiencyLevel; label: string }[] = [
  { value: "BEGINNER", label: "Beginner" },
  { value: "INTERMEDIATE", label: "Intermediate" },
  { value: "ADVANCED", label: "Advanced" },
  { value: "EXPERT", label: "Expert" },
];

export function SkillsEditForm({
  skill,
  onCancel,
  onUpdated,
}: SkillsEditFormProps) {
  const updateMutation = useUpdateSkill();

  const [skillName, setSkillName] = useState(skill.skillName);
  const [category, setCategory] = useState(skill.category);
  const [proficiencyLevel, setProficiencyLevel] = useState(
    skill.proficiencyLevel,
  );
  const [yearsOfExperience, setYearsOfExperience] = useState(
    skill.yearsOfExperience?.toString() ?? "",
  );

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const request: UpdateCandidateSkillRequest = {};

    if (skillName.trim() !== skill.skillName) {
      request.skillName = skillName.trim();
    }

    if (category !== skill.category) {
      request.category = category;
    }

    if (proficiencyLevel !== skill.proficiencyLevel) {
      request.proficiencyLevel = proficiencyLevel;
    }

    const originalExperience = skill.yearsOfExperience?.toString() ?? "";

    if (yearsOfExperience.trim() !== originalExperience) {
      if (yearsOfExperience.trim() === "") {
        request.clearYearsOfExperience = true;
      } else {
        request.yearsOfExperience = Number(yearsOfExperience);
        request.clearYearsOfExperience = false;
      }
    }

    if (Object.keys(request).length === 0) {
      onCancel();
      return;
    }

    updateMutation.mutate(
      {
        skillId: skill.id,
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
      <div className="space-y-2">
        <Label htmlFor={`editSkillName-${skill.id}`}>Skill name *</Label>
        <Input
          id={`editSkillName-${skill.id}`}
          value={skillName}
          onChange={(event) => setSkillName(event.target.value)}
          maxLength={150}
          required
        />
      </div>

      <div className="grid gap-5 md:grid-cols-2">
        <div className="space-y-2">
          <Label htmlFor={`editSkillCategory-${skill.id}`}>Category *</Label>
          <select
            id={`editSkillCategory-${skill.id}`}
            value={category}
            onChange={(event) =>
              setCategory(event.target.value as SkillCategory)
            }
            className="w-full rounded-md border border-border bg-background px-3 py-2 text-sm text-text-primary"
          >
            {categories.map((item) => (
              <option key={item.value} value={item.value}>
                {item.label}
              </option>
            ))}
          </select>
        </div>

        <div className="space-y-2">
          <Label htmlFor={`editSkillProficiency-${skill.id}`}>
            Proficiency level *
          </Label>
          <select
            id={`editSkillProficiency-${skill.id}`}
            value={proficiencyLevel}
            onChange={(event) =>
              setProficiencyLevel(event.target.value as ProficiencyLevel)
            }
            className="w-full rounded-md border border-border bg-background px-3 py-2 text-sm text-text-primary"
          >
            {proficiencyLevels.map((item) => (
              <option key={item.value} value={item.value}>
                {item.label}
              </option>
            ))}
          </select>
        </div>

        <div className="space-y-2">
          <Label htmlFor={`editSkillExperience-${skill.id}`}>
            Years of experience
          </Label>
          <Input
            id={`editSkillExperience-${skill.id}`}
            type="number"
            value={yearsOfExperience}
            onChange={(event) => setYearsOfExperience(event.target.value)}
            min="0"
            max="99.9"
            step="0.1"
          />
        </div>
      </div>

      {updateMutation.isError && (
        <p role="alert" className="text-sm text-danger">
          {updateMutation.error.message || "Unable to update skill."}
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
