import { useRef, useState } from "react";

import { Button, Card, Input } from "@/components/ui";
import {
  useCreateJobDescriptionFromText,
  useUploadJobDescription,
} from "@/modules/job";

type InputMode = "upload" | "paste";

type JobDescriptionInputProps = {
  onCreated?: (jobDescriptionId: string) => void;
};

export function JobDescriptionInput({ onCreated }: JobDescriptionInputProps) {
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [mode, setMode] = useState<InputMode>("upload");
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [error, setError] = useState<string | null>(null);

  const uploadMutation = useUploadJobDescription();
  const textMutation = useCreateJobDescriptionFromText();

  const isSubmitting = uploadMutation.isPending || textMutation.isPending;

  const resetFileInput = () => {
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };

  const handleModeChange = (nextMode: InputMode) => {
    setMode(nextMode);
    setError(null);

    setSelectedFile(null);
    resetFileInput();

    uploadMutation.reset();
    textMutation.reset();
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];

    setError(null);
    uploadMutation.reset();

    if (!file) {
      setSelectedFile(null);
      return;
    }

    setSelectedFile(file);
  };

  const handleUpload = async () => {
    if (!selectedFile) {
      setError("Please select a job description file.");
      return;
    }

    setError(null);

    try {
      const result = await uploadMutation.mutateAsync(selectedFile);

      setSelectedFile(null);
      resetFileInput();

      onCreated?.(result.jobDescriptionId);
    } catch {
      setError("Unable to upload the job description. Please try again.");
    }
  };

  const handleCreateFromText = async () => {
    const trimmedContent = content.trim();

    if (!trimmedContent) {
      setError("Please paste the job description.");
      return;
    }

    setError(null);

    try {
      const result = await textMutation.mutateAsync({
        title: title.trim() || undefined,
        content: trimmedContent,
      });

      setTitle("");
      setContent("");

      onCreated?.(result.jobDescriptionId);
    } catch {
      setError("Unable to add the job description. Please try again.");
    }
  };

  return (
    <Card className="p-5 sm:p-6">
      <div>
        <h2 className="text-base font-semibold text-text-primary">
          Target job
        </h2>

        <p className="mt-1 text-sm leading-6 text-text-secondary">
          Add the job description you want to compare this resume against.
        </p>
      </div>

      <div
        className="mt-5 inline-flex rounded-md border border-border bg-surface-muted p-1"
        role="tablist"
        aria-label="Job description input method"
      >
        <button
          type="button"
          role="tab"
          aria-selected={mode === "upload"}
          onClick={() => handleModeChange("upload")}
          className={[
            "rounded px-3 py-1.5 text-sm font-medium transition-colors",
            mode === "upload"
              ? "bg-surface text-text-primary shadow-sm"
              : "text-text-secondary hover:text-text-primary",
          ].join(" ")}
        >
          Upload file
        </button>

        <button
          type="button"
          role="tab"
          aria-selected={mode === "paste"}
          onClick={() => handleModeChange("paste")}
          className={[
            "rounded px-3 py-1.5 text-sm font-medium transition-colors",
            mode === "paste"
              ? "bg-surface text-text-primary shadow-sm"
              : "text-text-secondary hover:text-text-primary",
          ].join(" ")}
        >
          Paste text
        </button>
      </div>

      {mode === "upload" ? (
        <div className="mt-5">
          <Input
            ref={fileInputRef}
            type="file"
            accept=".pdf,.docx,.txt"
            onChange={handleFileChange}
            disabled={isSubmitting}
            className="h-auto cursor-pointer py-2"
          />

          {selectedFile && (
            <div className="mt-4 flex items-center justify-between gap-4 rounded-md border border-border bg-surface-muted px-4 py-3">
              <div className="min-w-0">
                <p className="truncate text-sm font-medium text-text-primary">
                  {selectedFile.name}
                </p>

                <p className="mt-0.5 text-xs text-text-tertiary">
                  {(selectedFile.size / (1024 * 1024)).toFixed(2)} MB
                </p>
              </div>

              <Button
                type="button"
                variant="ghost"
                size="sm"
                onClick={() => {
                  setSelectedFile(null);
                  resetFileInput();
                }}
                disabled={isSubmitting}
              >
                Remove
              </Button>
            </div>
          )}

          <div className="mt-5 flex justify-end">
            <Button
              type="button"
              onClick={() => void handleUpload()}
              loading={uploadMutation.isPending}
              disabled={!selectedFile || isSubmitting}
            >
              Add job description
            </Button>
          </div>
        </div>
      ) : (
        <div className="mt-5">
          <Input
            value={title}
            onChange={(event) => setTitle(event.target.value)}
            placeholder="Job title (optional)"
            maxLength={255}
            disabled={isSubmitting}
          />

          <textarea
            value={content}
            onChange={(event) => setContent(event.target.value)}
            placeholder="Paste the job description here..."
            disabled={isSubmitting}
            rows={10}
            className={[
              "mt-3 w-full resize-y rounded-md border border-border",
              "bg-surface px-3 py-2.5 text-sm text-text-primary",
              "placeholder:text-text-tertiary outline-none transition-colors",
              "focus:border-brand focus:ring-2 focus:ring-brand/20",
              "disabled:cursor-not-allowed disabled:opacity-60",
            ].join(" ")}
          />

          <div className="mt-5 flex justify-end">
            <Button
              type="button"
              onClick={() => void handleCreateFromText()}
              loading={textMutation.isPending}
              disabled={!content.trim() || isSubmitting}
            >
              Add job description
            </Button>
          </div>
        </div>
      )}

      {error && (
        <p role="alert" className="mt-3 text-sm text-danger">
          {error}
        </p>
      )}
    </Card>
  );
}
