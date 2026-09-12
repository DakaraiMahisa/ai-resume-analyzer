import { Badge } from "@/components/ui";
import type { DocumentProcessingStatus } from "@/modules/resume/types/resume.types";

type JobDescriptionStatusBadgeProps = {
  status: DocumentProcessingStatus;
};

const statusConfig = {
  UPLOADED: {
    label: "Uploaded",
    variant: "default",
  },
  PROCESSING: {
    label: "Processing",
    variant: "info",
  },
  COMPLETED: {
    label: "Ready",
    variant: "success",
  },
  FAILED: {
    label: "Failed",
    variant: "danger",
  },
} as const;

export function JobDescriptionStatusBadge({
  status,
}: JobDescriptionStatusBadgeProps) {
  const config = statusConfig[status];

  return <Badge variant={config.variant}>{config.label}</Badge>;
}
