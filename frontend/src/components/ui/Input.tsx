import type { InputHTMLAttributes } from "react";

export type InputProps = InputHTMLAttributes<HTMLInputElement>;

export function Input({ className, ...props }: InputProps) {
  return (
    <input
      className={[
        "h-10 w-full rounded-md border border-border bg-surface px-3",
        "text-sm text-text-primary placeholder:text-text-tertiary",
        "outline-none transition-colors",
        "focus:border-brand focus:ring-2 focus:ring-brand/20",
        "disabled:cursor-not-allowed disabled:opacity-60",
        className,
      ]
        .filter(Boolean)
        .join(" ")}
      {...props}
    />
  );
}
