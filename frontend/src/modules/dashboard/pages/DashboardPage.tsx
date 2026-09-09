export function DashboardPage() {
  return (
    <div className="space-y-8">
      <section className="space-y-2">
        <p className="text-sm font-medium text-text-secondary">
          Resume Intelligence Workspace
        </p>

        <div>
          <h1 className="text-2xl font-semibold tracking-tight text-text-primary sm:text-3xl">
            Dashboard
          </h1>

          <p className="mt-2 max-w-2xl text-sm text-text-secondary sm:text-base">
            Analyze resumes against job descriptions and turn your documents
            into actionable hiring insights.
          </p>
        </div>
      </section>

      <section
        aria-label="Workspace overview"
        className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4"
      >
        <div className="rounded-lg border border-border bg-surface p-5">
          <p className="text-sm text-text-secondary">Resumes</p>
          <p className="mt-2 text-2xl font-semibold text-text-primary">—</p>
          <p className="mt-1 text-xs text-text-tertiary">
            No data available yet
          </p>
        </div>

        <div className="rounded-lg border border-border bg-surface p-5">
          <p className="text-sm text-text-secondary">Job descriptions</p>
          <p className="mt-2 text-2xl font-semibold text-text-primary">—</p>
          <p className="mt-1 text-xs text-text-tertiary">
            No data available yet
          </p>
        </div>

        <div className="rounded-lg border border-border bg-surface p-5">
          <p className="text-sm text-text-secondary">Analyses</p>
          <p className="mt-2 text-2xl font-semibold text-text-primary">—</p>
          <p className="mt-1 text-xs text-text-tertiary">
            No data available yet
          </p>
        </div>

        <div className="rounded-lg border border-border bg-surface p-5">
          <p className="text-sm text-text-secondary">Average ATS score</p>
          <p className="mt-2 text-2xl font-semibold text-text-primary">—</p>
          <p className="mt-1 text-xs text-text-tertiary">
            No analysis data yet
          </p>
        </div>
      </section>

      <section className="grid gap-6 lg:grid-cols-2">
        <div className="rounded-lg border border-border bg-surface p-6">
          <div>
            <h2 className="text-base font-semibold text-text-primary">
              Start an analysis
            </h2>

            <p className="mt-1 text-sm text-text-secondary">
              Upload a resume and compare it with a job description to begin
              extracting actionable insights.
            </p>
          </div>

          <div className="mt-6 rounded-md border border-border bg-surface-muted p-5">
            <p className="text-sm font-medium text-text-primary">
              Analysis workflow
            </p>

            <ol className="mt-4 space-y-3 text-sm text-text-secondary">
              <li>1. Upload your resume</li>
              <li>2. Add a job description</li>
              <li>3. Run the analysis pipeline</li>
              <li>4. Review your resume intelligence report</li>
            </ol>
          </div>
        </div>

        <div className="rounded-lg border border-border bg-surface p-6">
          <div>
            <h2 className="text-base font-semibold text-text-primary">
              Recent activity
            </h2>

            <p className="mt-1 text-sm text-text-secondary">
              Your latest resume analysis activity will appear here.
            </p>
          </div>

          <div className="mt-6 rounded-md border border-dashed border-border p-8 text-center">
            <p className="text-sm font-medium text-text-primary">
              No recent activity
            </p>

            <p className="mt-1 text-sm text-text-secondary">
              Complete your first analysis to see activity here.
            </p>
          </div>
        </div>
      </section>
    </div>
  );
}
