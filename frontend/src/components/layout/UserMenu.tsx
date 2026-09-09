import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import { useLogout } from "@/modules/auth";

export function UserMenu() {
  const [isOpen, setIsOpen] = useState(false);
  const navigate = useNavigate();

  const logoutMutation = useLogout();

  const handleLogout = () => {
    logoutMutation.mutate(undefined, {
      onSettled: () => {
        navigate("/login", { replace: true });
      },
    });
  };

  return (
    <div className="relative">
      <button
        type="button"
        onClick={() => setIsOpen((open) => !open)}
        aria-expanded={isOpen}
        aria-haspopup="menu"
        className={[
          "flex items-center gap-2 rounded-md px-2 py-1.5",
          "text-text-secondary transition-colors",
          "hover:bg-surface-muted hover:text-text-primary",
          "focus-visible:outline-none",
          "focus-visible:ring-2 focus-visible:ring-brand/30",
        ].join(" ")}
      >
        <span className="hidden text-sm font-medium sm:inline">Account</span>

        <span
          aria-hidden="true"
          className="flex h-8 w-8 items-center justify-center rounded-full border border-border bg-surface text-xs font-semibold text-text-secondary"
        >
          A
        </span>

        <svg
          viewBox="0 0 20 20"
          fill="currentColor"
          className={[
            "h-4 w-4 transition-transform",
            isOpen ? "rotate-180" : "",
          ].join(" ")}
          aria-hidden="true"
        >
          <path
            fillRule="evenodd"
            d="M5.23 7.21a.75.75 0 0 1 1.06.02L10 11.168l3.71-3.938a.75.75 0 1 1 1.08 1.04l-4.25 4.5a.75.75 0 0 1-1.08 0l-4.25-4.5a.75.75 0 0 1 .02-1.06Z"
            clipRule="evenodd"
          />
        </svg>
      </button>

      {isOpen && (
        <div
          role="menu"
          className="absolute right-0 top-full z-50 mt-2 w-52 rounded-lg border border-border bg-surface p-1 shadow-lg"
        >
          <div className="px-3 py-2.5">
            <p className="text-sm font-medium text-text-primary">Account</p>

            <p className="mt-0.5 text-xs text-text-tertiary">
              Manage your workspace
            </p>
          </div>

          <div className="my-1 h-px bg-border" />

          <Link
            to="/app/settings"
            role="menuitem"
            onClick={() => setIsOpen(false)}
            className={[
              "block rounded-md px-3 py-2 text-sm",
              "text-text-secondary transition-colors",
              "hover:bg-surface-muted hover:text-text-primary",
            ].join(" ")}
          >
            Settings
          </Link>

          <button
            type="button"
            role="menuitem"
            onClick={handleLogout}
            disabled={logoutMutation.isPending}
            className={[
              "mt-0.5 flex w-full items-center justify-between",
              "rounded-md px-3 py-2 text-sm",
              "text-danger transition-colors",
              "hover:bg-danger/5",
              "disabled:cursor-not-allowed disabled:opacity-60",
            ].join(" ")}
          >
            <span>Sign out</span>

            {logoutMutation.isPending && (
              <span
                aria-hidden="true"
                className="h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
              />
            )}
          </button>
        </div>
      )}
    </div>
  );
}
