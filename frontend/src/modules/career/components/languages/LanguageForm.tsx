import { useState, type FormEvent } from "react";

import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";

import type {
  CreateCandidateLanguageRequest,
  LanguageProficiency,
} from "../../types/language.types";

type LanguageFormProps = {
  onSubmit: (request: CreateCandidateLanguageRequest) => void;
  onCancel: () => void;
  isSubmitting: boolean;
};

const proficiencyLevels: LanguageProficiency[] = [
  "A1",
  "A2",
  "B1",
  "B2",
  "C1",
  "C2",
];

export function LanguageForm({
  onSubmit,
  onCancel,
  isSubmitting,
}: LanguageFormProps) {
  const [languageName, setLanguageName] = useState("");
  const [speakingLevel, setSpeakingLevel] = useState<LanguageProficiency>("B1");
  const [listeningLevel, setListeningLevel] =
    useState<LanguageProficiency>("B1");
  const [readingLevel, setReadingLevel] = useState<LanguageProficiency>("B1");
  const [writingLevel, setWritingLevel] = useState<LanguageProficiency>("B1");
  const [nativeLanguage, setNativeLanguage] = useState(false);

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    onSubmit({
      languageName: languageName.trim(),
      speakingLevel,
      listeningLevel,
      readingLevel,
      writingLevel,
      nativeLanguage,
    });
  }

  function renderProficiencySelect(
    id: string,
    label: string,
    value: LanguageProficiency,
    onChange: (value: LanguageProficiency) => void,
  ) {
    return (
      <div className="space-y-2">
        <Label htmlFor={id}>{label}</Label>
        <select
          id={id}
          value={value}
          onChange={(event) =>
            onChange(event.target.value as LanguageProficiency)
          }
          className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
        >
          {proficiencyLevels.map((level) => (
            <option key={level} value={level}>
              {level}
            </option>
          ))}
        </select>
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="languageName">Language</Label>
        <Input
          id="languageName"
          value={languageName}
          onChange={(event) => setLanguageName(event.target.value)}
          placeholder="e.g. English"
          required
          maxLength={100}
        />
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        {renderProficiencySelect(
          "speakingLevel",
          "Speaking",
          speakingLevel,
          setSpeakingLevel,
        )}

        {renderProficiencySelect(
          "listeningLevel",
          "Listening",
          listeningLevel,
          setListeningLevel,
        )}

        {renderProficiencySelect(
          "readingLevel",
          "Reading",
          readingLevel,
          setReadingLevel,
        )}

        {renderProficiencySelect(
          "writingLevel",
          "Writing",
          writingLevel,
          setWritingLevel,
        )}
      </div>

      <div className="flex items-center gap-2">
        <input
          id="nativeLanguage"
          type="checkbox"
          checked={nativeLanguage}
          onChange={(event) => setNativeLanguage(event.target.checked)}
          className="h-4 w-4"
        />
        <Label htmlFor="nativeLanguage">This is my native language</Label>
      </div>

      <div className="flex justify-end gap-2">
        <Button type="button" variant="secondary" onClick={onCancel}>
          Cancel
        </Button>

        <Button type="submit" loading={isSubmitting}>
          Add language
        </Button>
      </div>
    </form>
  );
}
