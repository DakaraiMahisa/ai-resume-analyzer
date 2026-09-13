import { useRef, useState } from "react";

import { Button, Card, Input } from "@/components/ui";
import { useProcessResume, useUploadResume } from "@/modules/resume";

const MAX_FILE_SIZE = 5 * 1024 * 1024;

const ALLOWED_FILE_TYPES = [
  "application/pdf",
  "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
  "text/plain",
] as const;

type ResumeUploadProps = {
  onSuccess?: () => void;
};

export function ResumeUpload({ onSuccess }: ResumeUploadProps) {
  const inputRef = useRef<HTMLInputElement>(null);

  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [validationError, setValidationError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const uploadMutation = useUploadResume();
  const processMutation = useProcessResume();

  const isProcessing = uploadMutation.isPending || processMutation.isPending;

  const resetInput = () => {
    if (inputRef.current) {
      inputRef.current.value = "";
    }
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];

    setValidationError(null);
    setSuccessMessage(null);

    if (!file) {
      setSelectedFile(null);
      return;
    }

    if (
      !ALLOWED_FILE_TYPES.includes(
        file.type as (typeof ALLOWED_FILE_TYPES)[number],
      )
    ) {
      setSelectedFile(null);
      resetInput();
      setValidationError(
        "Unsupported file type. Please select a PDF, DOCX, or TXT file.",
      );
      return;
    }

    if (file.size > MAX_FILE_SIZE) {
      setSelectedFile(null);
      resetInput();
      setValidationError(
        "File is too large. The maximum allowed size is 5 MB.",
      );
      return;
    }

    setSelectedFile(file);
  };

  const handleUpload = async () => {
    if (!selectedFile) {
      setValidationError("Please select a resume first.");
      return;
    }

    setValidationError(null);
    setSuccessMessage(null);

    let uploadResult;

    try {
      uploadResult = await uploadMutation.mutateAsync(selectedFile);
    } catch {
      setValidationError("Unable to upload the resume. Please try again.");
      return;
    }

    try {
      await processMutation.mutateAsync({
        processingJobId: uploadResult.processingJobId,
        resumeId: uploadResult.resumeId,
      });
    } catch {
      setValidationError(
        "Resume uploaded, but processing failed. Please try again.",
      );
      return;
    }

    setSelectedFile(null);
    resetInput();
    setSuccessMessage("Resume uploaded and processed successfully.");

    onSuccess?.();
  };

  const handleCancel = () => {
    setSelectedFile(null);
    setValidationError(null);
    setSuccessMessage(null);

    uploadMutation.reset();
    processMutation.reset();

    resetInput();
  };

  return (
    <Card className="p-5 sm:p-6">
      <div>
        <h2 className="text-base font-semibold text-text-primary">
          Upload resume
        </h2>

        <p className="mt-1 text-sm leading-6 text-text-secondary">
          Upload a PDF, DOCX, or TXT resume. Maximum file size is 5 MB.
        </p>
      </div>

      <div className="mt-5">
        <Input
          ref={inputRef}
          type="file"
          accept=".pdf,.docx,.txt"
          onChange={handleFileChange}
          disabled={isProcessing}
          className="h-auto cursor-pointer py-2"
        />
      </div>

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
            onClick={handleCancel}
            disabled={isProcessing}
          >
            Remove
          </Button>
        </div>
      )}

      {validationError && (
        <p role="alert" className="mt-3 text-sm text-danger">
          {validationError}
        </p>
      )}

      {successMessage && (
        <p role="status" className="mt-3 text-sm text-success">
          {successMessage}
        </p>
      )}

      <div className="mt-5 flex justify-end">
        <Button
          type="button"
          onClick={handleUpload}
          loading={isProcessing}
          disabled={!selectedFile || isProcessing}
        >
          Upload resume
        </Button>
      </div>
    </Card>
  );
}
