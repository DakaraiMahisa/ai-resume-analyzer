import type { HTMLAttributes } from "react";

export type SeparatorProps = HTMLAttributes<HTMLDivElement> & {
  orientation?: "horizontal" | "vertical";
};

export function Separator({
  orientation = "horizontal",
  className,
  ...props
}: SeparatorProps) {
  const isHorizontal = orientation === "horizontal";

  return (
    <div
      role="separator"
      aria-orientation={orientation}
      className={[
        "shrink-0 bg-border",
        isHorizontal ? "h-px w-full" : "h-full w-px",
        className,
      ]
        .filter(Boolean)
        .join(" ")}
      {...props}
    />
  );
}
