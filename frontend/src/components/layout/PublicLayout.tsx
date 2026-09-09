import { Link, Outlet } from "react-router-dom";

import { ThemeToggle } from "@/components/ui";

export function PublicLayout() {
  return (
    <div className="min-h-screen bg-background text-text-primary">
      <header className="border-b border-border bg-surface">
        <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-6">
          <Link
            to="/"
            className="text-lg font-semibold tracking-tight text-text-primary"
          >
            AI Resume Analyzer
          </Link>

          <nav
            aria-label="Primary navigation"
            className="flex items-center gap-4"
          >
            <ThemeToggle />

            <Link
              to="/login"
              className="text-sm font-medium text-text-secondary transition-colors hover:text-text-primary"
            >
              Sign in
            </Link>

            <Link
              to="/register"
              className="rounded-md bg-brand px-4 py-2 text-sm font-medium text-text-inverse transition-colors hover:bg-brand-hover"
            >
              Get started
            </Link>
          </nav>
        </div>
      </header>

      <main className="min-h-[calc(100vh-4rem)]">
        <Outlet />
      </main>
    </div>
  );
}
