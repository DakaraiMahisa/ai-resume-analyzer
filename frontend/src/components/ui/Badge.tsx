import type { HTMLAttributes, ReactNode } from "react";

export type BadgeVariant =
  | "default"
  | "success"
  | "warning"
  | "danger"
  | "info";

export type BadgeProps = HTMLAttributes<HTMLSpanElement> & {
  variant?: BadgeVariant;
  children: ReactNode;
};

const variantClasses: Record<BadgeVariant, string> = {
  default: "border-border bg-surface-muted text-text-secondary",

  success: "border-success/20 bg-success/5 text-success",

  warning: "border-warning/20 bg-warning/5 text-warning",

  danger: "border-danger/20 bg-danger/5 text-danger",

  info: "border-info/20 bg-info/5 text-info",
};

export function Badge({
  variant = "default",
  className,
  children,
  ...props
}: BadgeProps) {
  return (
    <span
      className={[
        "inline-flex items-center rounded-full border px-2.5 py-1",
        "text-xs font-medium leading-none",
        variantClasses[variant],
        className,
      ]
        .filter(Boolean)
        .join(" ")}
      {...props}
    >
      {children}
    </span>
  );
}
