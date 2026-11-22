import { useState, type FormEvent } from "react";

import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";

import type {
  CandidateLanguage,
  LanguageProficiency,
  UpdateCandidateLanguageRequest,
} from "../../types/language.types";

type LanguageEditFormProps = {
  language: CandidateLanguage;
  onSubmit: (request: UpdateCandidateLanguageRequest) => void;
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

export function LanguageEditForm({
  language,
  onSubmit,
  onCancel,
  isSubmitting,
}: LanguageEditFormProps) {
  const [languageName, setLanguageName] = useState(language.languageName);
  const [speakingLevel, setSpeakingLevel] = useState(language.speakingLevel);
  const [listeningLevel, setListeningLevel] = useState(language.listeningLevel);
  const [readingLevel, setReadingLevel] = useState(language.readingLevel);
  const [writingLevel, setWritingLevel] = useState(language.writingLevel);
  const [nativeLanguage, setNativeLanguage] = useState(language.nativeLanguage);

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const request: UpdateCandidateLanguageRequest = {};

    if (languageName.trim() !== language.languageName) {
      request.languageName = languageName.trim();
    }

    if (speakingLevel !== language.speakingLevel) {
      request.speakingLevel = speakingLevel;
    }

    if (listeningLevel !== language.listeningLevel) {
      request.listeningLevel = listeningLevel;
    }

    if (readingLevel !== language.readingLevel) {
      request.readingLevel = readingLevel;
    }

    if (writingLevel !== language.writingLevel) {
      request.writingLevel = writingLevel;
    }

    if (nativeLanguage !== language.nativeLanguage) {
      request.nativeLanguage = nativeLanguage;
    }

    onSubmit(request);
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
        <Label htmlFor="edit-languageName">Language</Label>
        <Input
          id="edit-languageName"
          value={languageName}
          onChange={(event) => setLanguageName(event.target.value)}
          required
          maxLength={100}
        />
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        {renderProficiencySelect(
          "edit-speakingLevel",
          "Speaking",
          speakingLevel,
          setSpeakingLevel,
        )}

        {renderProficiencySelect(
          "edit-listeningLevel",
          "Listening",
          listeningLevel,
          setListeningLevel,
        )}

        {renderProficiencySelect(
          "edit-readingLevel",
          "Reading",
          readingLevel,
          setReadingLevel,
        )}

        {renderProficiencySelect(
          "edit-writingLevel",
          "Writing",
          writingLevel,
          setWritingLevel,
        )}
      </div>

      <div className="flex items-center gap-2">
        <input
          id="edit-nativeLanguage"
          type="checkbox"
          checked={nativeLanguage}
          onChange={(event) => setNativeLanguage(event.target.checked)}
          className="h-4 w-4"
        />
        <Label htmlFor="edit-nativeLanguage">This is my native language</Label>
      </div>

      <div className="flex justify-end gap-2">
        <Button type="button" variant="secondary" onClick={onCancel}>
          Cancel
        </Button>

        <Button type="submit" loading={isSubmitting}>
          Save changes
        </Button>
      </div>
    </form>
  );
}
