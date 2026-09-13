import { Badge } from "@/components/ui";

import type { DocumentProcessingStatus } from "@/modules/resume/types/resume.types";

type ResumeStatusBadgeProps = {
  status: DocumentProcessingStatus;
};

const statusConfig: Record<
  DocumentProcessingStatus,
  {
    label: string;
    variant: "default" | "success" | "warning" | "danger" | "info";
  }
> = {
  UPLOADED: {
    label: "Uploaded",
    variant: "default",
  },
  PROCESSING: {
    label: "Processing",
    variant: "info",
  },
  COMPLETED: {
    label: "Completed",
    variant: "success",
  },
  FAILED: {
    label: "Failed",
    variant: "danger",
  },
};

export function ResumeStatusBadge({ status }: ResumeStatusBadgeProps) {
  const config = statusConfig[status];

  return <Badge variant={config.variant}>{config.label}</Badge>;
}
