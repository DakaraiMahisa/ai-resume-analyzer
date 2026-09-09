import type { LabelHTMLAttributes } from "react";

export type LabelProps = LabelHTMLAttributes<HTMLLabelElement>;

export function Label({ className, ...props }: LabelProps) {
  return (
    <label
      className={["block text-sm font-medium text-text-primary", className]
        .filter(Boolean)
        .join(" ")}
      {...props}
    />
  );
}
