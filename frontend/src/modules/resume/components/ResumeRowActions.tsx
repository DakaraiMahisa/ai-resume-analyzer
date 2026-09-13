import { useDeleteResume } from "@/modules/resume";

type ResumeRowActionsProps = {
  resumeId: string;
  fileName: string;
};

export function ResumeRowActions({
  resumeId,
  fileName,
}: ResumeRowActionsProps) {
  const deleteMutation = useDeleteResume();

  const handleDelete = () => {
    const confirmed = window.confirm(
      `Are you sure you want to delete "${fileName}"? This action cannot be undone.`,
    );

    if (!confirmed) {
      return;
    }

    deleteMutation.mutate(resumeId);
  };

  return (
    <button
      type="button"
      onClick={handleDelete}
      disabled={deleteMutation.isPending}
      className={[
        "shrink-0 rounded-md px-2.5 py-1.5 text-sm",
        "text-danger transition-colors",
        "hover:bg-danger/5",
        "focus-visible:outline-none",
        "focus-visible:ring-2 focus-visible:ring-danger/30",
        "disabled:cursor-not-allowed disabled:opacity-60",
      ].join(" ")}
    >
      {deleteMutation.isPending ? "Deleting..." : "Delete"}
    </button>
  );
}
