import type { ReactNode } from "react";

import { Button } from "@/components/ui";

export type EmptyStateProps = {
  title: string;
  description: string;
  actionLabel?: string;
  onAction?: () => void;
  icon?: ReactNode;
};

export function EmptyState({
  title,
  description,
  actionLabel,
  onAction,
  icon,
}: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center px-6 py-16 text-center">
      {icon && (
        <div
          aria-hidden="true"
          className="mb-4 flex h-10 w-10 items-center justify-center rounded-md border border-border bg-surface-muted text-text-tertiary"
        >
          {icon}
        </div>
      )}

      <h3 className="text-sm font-semibold text-text-primary">{title}</h3>

      <p className="mt-1 max-w-md text-sm leading-6 text-text-secondary">
        {description}
      </p>

      {actionLabel && onAction && (
        <Button type="button" size="sm" className="mt-5" onClick={onAction}>
          {actionLabel}
        </Button>
      )}
    </div>
  );
}
