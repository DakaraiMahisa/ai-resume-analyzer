import type { ReactNode } from "react";

export type PageHeaderProps = {
  eyebrow?: string;
  title: string;
  description?: string;
  actions?: ReactNode;
};

export function PageHeader({
  eyebrow,
  title,
  description,
  actions,
}: PageHeaderProps) {
  return (
    <header className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
      <div className="min-w-0">
        {eyebrow && <p className="text-sm font-medium text-brand">{eyebrow}</p>}

        <h1 className="mt-1 text-2xl font-semibold tracking-tight text-text-primary sm:text-3xl">
          {title}
        </h1>

        {description && (
          <p className="mt-2 max-w-2xl text-sm leading-6 text-text-secondary">
            {description}
          </p>
        )}
      </div>

      {actions && (
        <div className="flex shrink-0 items-center gap-3">{actions}</div>
      )}
    </header>
  );
}
