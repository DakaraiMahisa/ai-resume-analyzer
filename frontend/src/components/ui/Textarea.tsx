import { forwardRef, type TextareaHTMLAttributes } from "react";

export type TextareaProps = TextareaHTMLAttributes<HTMLTextAreaElement>;

export const Textarea = forwardRef<HTMLTextAreaElement, TextareaProps>(
  function Textarea({ className, ...props }, ref) {
    return (
      <textarea
        ref={ref}
        className={[
          "min-h-28 w-full resize-y rounded-md border border-border bg-surface px-3 py-2.5",
          "text-sm leading-6 text-text-primary placeholder:text-text-tertiary",
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
  },
);
