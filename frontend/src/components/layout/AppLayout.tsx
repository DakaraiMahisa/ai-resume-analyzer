import { NavLink, Outlet } from "react-router-dom";
import { ThemeToggle } from "@/components/ui";
import { UserMenu } from "@/components/layout/UserMenu";

const primaryNavigation = [
  {
    label: "Dashboard",
    to: "/app/dashboard",
  },
  {
    label: "Resumes",
    to: "/app/resumes",
  },
  {
    label: "Jobs",
    to: "/app/jobs",
  },
  {
    label: "Analyses",
    to: "/app/analyses",
  },
];

const secondaryNavigation = [
  {
    label: "Settings",
    to: "/app/settings",
  },
];

export function AppLayout() {
  return (
    <div className="min-h-screen bg-background text-text-primary">
      <aside className="fixed inset-y-0 left-0 hidden w-64 flex-col border-r border-border bg-surface lg:flex">
        <div className="flex h-16 items-center border-b border-border px-6">
          <NavLink
            to="/app/dashboard"
            className="text-lg font-semibold tracking-tight text-text-primary"
          >
            AI Resume Analyzer
          </NavLink>
        </div>

        <div className="flex flex-1 flex-col px-3 py-6">
          <nav aria-label="Workspace navigation" className="space-y-1">
            <p className="px-3 pb-2 text-xs font-medium uppercase tracking-wider text-text-tertiary">
              Workspace
            </p>

            {primaryNavigation.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  [
                    "flex items-center rounded-md px-3 py-2.5 text-sm font-medium transition-colors",
                    isActive
                      ? "bg-surface-muted text-text-primary"
                      : "text-text-secondary hover:bg-surface-muted hover:text-text-primary",
                  ].join(" ")
                }
              >
                {item.label}
              </NavLink>
            ))}
          </nav>

          <div className="mt-auto space-y-1">
            {secondaryNavigation.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  [
                    "flex items-center rounded-md px-3 py-2.5 text-sm font-medium transition-colors",
                    isActive
                      ? "bg-surface-muted text-text-primary"
                      : "text-text-secondary hover:bg-surface-muted hover:text-text-primary",
                  ].join(" ")
                }
              >
                {item.label}
              </NavLink>
            ))}

            <div className="mt-4 border-t border-border pt-4">
              <div className="px-3 py-2">
                <p className="text-sm font-medium text-text-primary">Account</p>
                <p className="mt-0.5 text-xs text-text-tertiary">
                  Manage your workspace
                </p>
              </div>
            </div>
          </div>
        </div>
      </aside>

      <div className="lg:pl-64">
        <header className="sticky top-0 z-30 border-b border-border bg-background/95 backdrop-blur">
          <div className="flex h-16 items-center justify-between px-4 sm:px-6 lg:px-8">
            <div className="min-w-0">
              <p className="truncate text-sm font-medium text-text-secondary">
                Resume Intelligence Workspace
              </p>
            </div>

            <div className="flex items-center gap-3">
              <ThemeToggle />
              <UserMenu />
            </div>
          </div>

          <nav
            aria-label="Mobile workspace navigation"
            className="flex overflow-x-auto border-t border-border px-4 py-2 lg:hidden"
          >
            <div className="flex min-w-max gap-1">
              {primaryNavigation.map((item) => (
                <NavLink
                  key={item.to}
                  to={item.to}
                  className={({ isActive }) =>
                    [
                      "rounded-md px-3 py-2 text-sm font-medium transition-colors",
                      isActive
                        ? "bg-surface-muted text-text-primary"
                        : "text-text-secondary hover:bg-surface-muted hover:text-text-primary",
                    ].join(" ")
                  }
                >
                  {item.label}
                </NavLink>
              ))}

              {secondaryNavigation.map((item) => (
                <NavLink
                  key={item.to}
                  to={item.to}
                  className={({ isActive }) =>
                    [
                      "rounded-md px-3 py-2 text-sm font-medium transition-colors",
                      isActive
                        ? "bg-surface-muted text-text-primary"
                        : "text-text-secondary hover:bg-surface-muted hover:text-text-primary",
                    ].join(" ")
                  }
                >
                  {item.label}
                </NavLink>
              ))}
            </div>
          </nav>
        </header>

        <main className="min-h-[calc(100vh-4rem)]">
          <div className="mx-auto w-full max-w-360 px-4 py-6 sm:px-6 lg:px-8 lg:py-8">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
}
