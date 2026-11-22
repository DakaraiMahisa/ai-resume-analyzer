import { useState, type FormEvent } from "react";

import { Button, Input, Label } from "@/components/ui";
import { useCreateSkill } from "@/modules/career/hooks/use-skill-mutations";

import type {
  CreateCandidateSkillRequest,
  ProficiencyLevel,
  SkillCategory,
} from "@/modules/career/types/skill.types";

type SkillsFormProps = {
  onCancel: () => void;
  onCreated: () => void;
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

export function SkillsForm({ onCancel, onCreated }: SkillsFormProps) {
  const createMutation = useCreateSkill();

  const [skillName, setSkillName] = useState("");
  const [category, setCategory] = useState<SkillCategory>("TECHNICAL");
  const [proficiencyLevel, setProficiencyLevel] =
    useState<ProficiencyLevel>("BEGINNER");
  const [yearsOfExperience, setYearsOfExperience] = useState("");

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const request: CreateCandidateSkillRequest = {
      skillName: skillName.trim(),
      category,
      proficiencyLevel,
      ...(yearsOfExperience.trim() !== "" && {
        yearsOfExperience: Number(yearsOfExperience),
      }),
    };

    createMutation.mutate(request, {
      onSuccess: onCreated,
    });
  };

  const isSaving = createMutation.isPending;

  return (
    <form onSubmit={handleSubmit} className="space-y-5">
      <div className="space-y-2">
        <Label htmlFor="skillName">Skill name *</Label>
        <Input
          id="skillName"
          value={skillName}
          onChange={(event) => setSkillName(event.target.value)}
          placeholder="e.g. Java, Spring Boot, Communication"
          maxLength={150}
          required
        />
      </div>

      <div className="grid gap-5 md:grid-cols-2">
        <div className="space-y-2">
          <Label htmlFor="skillCategory">Category *</Label>
          <select
            id="skillCategory"
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
          <Label htmlFor="skillProficiency">Proficiency level *</Label>
          <select
            id="skillProficiency"
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
          <Label htmlFor="yearsOfExperience">Years of experience</Label>
          <Input
            id="yearsOfExperience"
            type="number"
            value={yearsOfExperience}
            onChange={(event) => setYearsOfExperience(event.target.value)}
            min="0"
            max="99.9"
            step="0.1"
            placeholder="e.g. 2.5"
          />
        </div>
      </div>

      {createMutation.isError && (
        <p role="alert" className="text-sm text-danger">
          {createMutation.error.message || "Unable to add skill."}
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
          Add skill
        </Button>
      </div>
    </form>
  );
}
