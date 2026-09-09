import { useEffect, useState } from "react";

import { themeManager } from "@/core/theme/theme-manager";
import type { Theme } from "@/core/theme/theme-types";

export function ThemeToggle() {
  const [theme, setTheme] = useState<Theme>(() => themeManager.getTheme());

  useEffect(() => {
    themeManager.applyTheme(theme);
  }, [theme]);

  const handleToggle = () => {
    const nextTheme = themeManager.toggle();

    setTheme(nextTheme);
  };

  const isDark = theme === "dark";

  return (
    <button
      type="button"
      onClick={handleToggle}
      aria-label={isDark ? "Switch to light theme" : "Switch to dark theme"}
      title={isDark ? "Switch to light theme" : "Switch to dark theme"}
      className={[
        "inline-flex h-9 w-9 items-center justify-center rounded-md",
        "border border-border bg-surface text-text-secondary",
        "transition-colors",
        "hover:bg-surface-muted hover:text-text-primary",
        "focus-visible:outline-none",
        "focus-visible:ring-2 focus-visible:ring-brand/30",
        "focus-visible:ring-offset-2",
        "focus-visible:ring-offset-background",
      ].join(" ")}
    >
      {isDark ? (
        <svg
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="1.8"
          className="h-4 w-4"
          aria-hidden="true"
        >
          <circle cx="12" cy="12" r="4" />
          <path d="M12 2v2" />
          <path d="M12 20v2" />
          <path d="m4.93 4.93 1.41 1.41" />
          <path d="m17.66 17.66 1.41 1.41" />
          <path d="M2 12h2" />
          <path d="M20 12h2" />
          <path d="m6.34 17.66-1.41 1.41" />
          <path d="m19.07 4.93-1.41 1.41" />
        </svg>
      ) : (
        <svg
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="1.8"
          className="h-4 w-4"
          aria-hidden="true"
        >
          <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79Z" />
        </svg>
      )}
    </button>
  );
}
