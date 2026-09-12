import { Button, Card } from "@/components/ui";

type DeleteJobDescriptionDialogProps = {
  fileName: string;
  loading?: boolean;
  onConfirm: () => void;
  onCancel: () => void;
};

export function DeleteJobDescriptionDialog({
  fileName,
  loading = false,
  onConfirm,
  onCancel,
}: DeleteJobDescriptionDialogProps) {
  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-background/80 px-4 backdrop-blur-sm"
      role="presentation"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget && !loading) {
          onCancel();
        }
      }}
    >
      <Card
        role="alertdialog"
        aria-modal="true"
        aria-labelledby="delete-job-description-title"
        aria-describedby="delete-job-description-description"
        className="w-full max-w-md p-6"
      >
        <h2
          id="delete-job-description-title"
          className="text-lg font-semibold text-text-primary"
        >
          Delete job description?
        </h2>

        <p
          id="delete-job-description-description"
          className="mt-2 text-sm leading-6 text-text-secondary"
        >
          This will permanently delete{" "}
          <span className="font-medium text-text-primary">{fileName}</span>.
          This action cannot be undone.
        </p>

        <div className="mt-6 flex justify-end gap-3">
          <Button
            type="button"
            variant="secondary"
            onClick={onCancel}
            disabled={loading}
          >
            Cancel
          </Button>

          <Button
            type="button"
            variant="danger"
            loading={loading}
            onClick={onConfirm}
          >
            Delete
          </Button>
        </div>
      </Card>
    </div>
  );
}
