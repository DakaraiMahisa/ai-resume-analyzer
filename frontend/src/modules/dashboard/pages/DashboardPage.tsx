import { Link } from "react-router-dom";

import { ErrorState, LoadingState } from "@/components/feedback";
import { Button, Card } from "@/components/ui";
import {
  useAnalysisDashboardSummary,
  useRecentAnalyses,
} from "@/modules/analysis";
import { useJobDescriptions } from "@/modules/job";
import { useResumes } from "@/modules/resume";

export function DashboardPage() {
  const {
    data: resumes,
    isLoading: isResumesLoading,
    isError: isResumesError,
    refetch: refetchResumes,
  } = useResumes();

  const {
    data: jobDescriptions,
    isLoading: isJobsLoading,
    isError: isJobsError,
    refetch: refetchJobs,
  } = useJobDescriptions();

  const {
    data: analysisSummary,
    isLoading: isSummaryLoading,
    isError: isSummaryError,
    refetch: refetchSummary,
  } = useAnalysisDashboardSummary();

  const {
    data: recentAnalyses,
    isLoading: isRecentLoading,
    isError: isRecentError,
    refetch: refetchRecent,
  } = useRecentAnalyses(0, 5);

  const isLoading =
    isResumesLoading || isJobsLoading || isSummaryLoading || isRecentLoading;

  const isError =
    isResumesError || isJobsError || isSummaryError || isRecentError;

  if (isLoading) {
    return <LoadingState message="Loading your workspace..." />;
  }

  if (isError) {
    return (
      <ErrorState
        title="Unable to load your workspace"
        description="We couldn't retrieve your dashboard data."
        actionLabel="Try again"
        onAction={() => {
          void refetchResumes();
          void refetchJobs();
          void refetchSummary();
          void refetchRecent();
        }}
      />
    );
  }

  const resumeCount = resumes?.length ?? 0;
  const jobDescriptionCount = jobDescriptions?.length ?? 0;

  const totalAnalyses = analysisSummary?.totalAnalyses ?? 0;

  const averageMatch = analysisSummary?.averageOverallScore ?? 0;

  return (
    <div className="space-y-8">
      {/* Hero */}
      <section className="relative overflow-hidden rounded-xl border border-border bg-surface">
        <div className="max-w-3xl px-6 py-8 sm:px-8 sm:py-10">
          <p className="text-xs font-semibold uppercase tracking-[0.14em] text-brand">
            Resume intelligence
          </p>

          <h1 className="mt-3 text-3xl font-semibold tracking-tight text-text-primary sm:text-4xl">
            Understand how your resume fits the job.
          </h1>

          <p className="mt-3 max-w-2xl text-sm leading-6 text-text-secondary sm:text-base">
            Compare your resume with a target job, understand your strengths and
            gaps, and get practical recommendations for improving your
            application.
          </p>

          <div className="mt-6 flex flex-wrap gap-3">
            <Link to="/app/analyses">
              <Button size="lg">Analyze a resume</Button>
            </Link>

            <Link to="/app/resumes">
              <Button variant="secondary" size="lg">
                Manage resumes
              </Button>
            </Link>
          </div>
        </div>
      </section>

      {/* Workspace metrics */}
      <section
        aria-label="Workspace overview"
        className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4"
      >
        <MetricCard
          label="Resumes"
          value={resumeCount}
          description={
            resumeCount === 1
              ? "1 resume in your workspace"
              : `${resumeCount} resumes in your workspace`
          }
        />

        <MetricCard
          label="Job descriptions"
          value={jobDescriptionCount}
          description={
            jobDescriptionCount === 1
              ? "1 job available for matching"
              : `${jobDescriptionCount} jobs available for matching`
          }
        />

        <MetricCard
          label="Analyses"
          value={totalAnalyses}
          description={
            totalAnalyses === 1
              ? "1 analysis completed or in progress"
              : `${totalAnalyses} analyses in your workspace`
          }
        />

        <MetricCard
          label="Average match"
          value={
            analysisSummary?.completedAnalyses ? formatScore(averageMatch) : "—"
          }
          description={
            analysisSummary?.completedAnalyses
              ? `Across ${analysisSummary.completedAnalyses} completed ${
                  analysisSummary.completedAnalyses === 1
                    ? "analysis"
                    : "analyses"
                }`
              : "Complete an analysis to see your average"
          }
        />
      </section>

      {/* Workspace */}
      <section className="grid gap-6 lg:grid-cols-[1.15fr_0.85fr]">
        <Card className="p-6 sm:p-7">
          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.14em] text-brand">
              Analysis workspace
            </p>

            <h2 className="mt-2 text-xl font-semibold tracking-tight text-text-primary">
              Start with a resume and a target job.
            </h2>

            <p className="mt-2 max-w-2xl text-sm leading-6 text-text-secondary">
              Choose a completed resume and job description to see how closely
              they match and where your application can improve.
            </p>
          </div>

          <div className="mt-6 grid gap-3 sm:grid-cols-3">
            <WorkflowStep
              number="01"
              title="Choose a resume"
              description="Use one of your completed resumes."
            />

            <WorkflowStep
              number="02"
              title="Choose a job"
              description="Select the role you want to target."
            />

            <WorkflowStep
              number="03"
              title="Analyze the match"
              description="Review strengths, gaps, and recommendations."
            />
          </div>

          <div className="mt-6 border-t border-border pt-5">
            <Link
              to="/app/analyses"
              className="text-sm font-medium text-brand transition-colors hover:text-brand-hover"
            >
              Go to analysis workspace →
            </Link>
          </div>
        </Card>

        {/* Recent analyses */}
        <Card className="overflow-hidden">
          <div className="border-b border-border px-6 py-6 sm:px-7">
            <p className="text-xs font-semibold uppercase tracking-[0.14em] text-text-tertiary">
              Recent activity
            </p>

            <h2 className="mt-2 text-xl font-semibold tracking-tight text-text-primary">
              Recent analyses
            </h2>

            <p className="mt-2 text-sm leading-6 text-text-secondary">
              Your latest resume-to-job comparisons.
            </p>
          </div>

          {recentAnalyses?.analyses.length ? (
            <div className="divide-y divide-border">
              {recentAnalyses.analyses.map((analysis) => (
                <RecentAnalysisRow
                  key={analysis.analysisId}
                  analysis={analysis}
                />
              ))}
            </div>
          ) : (
            <div className="px-6 py-10 text-center sm:px-7">
              <p className="text-sm font-medium text-text-primary">
                No analyses yet
              </p>

              <p className="mt-1 text-sm leading-6 text-text-secondary">
                Complete your first analysis and it will appear here.
              </p>

              <Link
                to="/app/analyze"
                className="mt-4 inline-block text-sm font-medium text-brand hover:text-brand-hover"
              >
                Start your first analysis →
              </Link>
            </div>
          )}
        </Card>
      </section>
    </div>
  );
}

type MetricCardProps = {
  label: string;
  value: number | string;
  description: string;
};

function MetricCard({ label, value, description }: MetricCardProps) {
  return (
    <Card className="p-5">
      <p className="text-sm font-medium text-text-secondary">{label}</p>

      <p className="mt-2 font-mono text-3xl font-semibold tracking-tight text-text-primary">
        {value}
      </p>

      <p className="mt-2 text-xs leading-5 text-text-tertiary">{description}</p>
    </Card>
  );
}

type WorkflowStepProps = {
  number: string;
  title: string;
  description: string;
};

function WorkflowStep({ number, title, description }: WorkflowStepProps) {
  return (
    <div className="rounded-md border border-border bg-surface-muted/40 p-4">
      <p className="font-mono text-xs font-semibold text-brand">{number}</p>

      <p className="mt-3 text-sm font-semibold text-text-primary">{title}</p>

      <p className="mt-1 text-xs leading-5 text-text-secondary">
        {description}
      </p>
    </div>
  );
}

type RecentAnalysisRowProps = {
  analysis: {
    analysisId: string;
    resumeDisplayName: string;
    jobDescriptionDisplayName: string;
    status: "RUNNING" | "COMPLETED" | "FAILED";
    overallScore: number;
  };
};

function RecentAnalysisRow({ analysis }: RecentAnalysisRowProps) {
  const isCompleted = analysis.status === "COMPLETED";
  const isFailed = analysis.status === "FAILED";

  return (
    <Link
      to={isCompleted ? `/app/analyses/${analysis.analysisId}` : "/app/analyze"}
      className="block px-6 py-5 transition-colors hover:bg-surface-muted/40 sm:px-7"
    >
      <div className="flex items-start justify-between gap-4">
        <div className="min-w-0">
          <p className="truncate text-sm font-medium text-text-primary">
            {analysis.resumeDisplayName}
          </p>

          <p className="mt-1 truncate text-xs text-text-tertiary">
            {analysis.jobDescriptionDisplayName}
          </p>
        </div>

        <div className="shrink-0 text-right">
          {isCompleted ? (
            <p className="font-mono text-sm font-semibold text-text-primary">
              {formatScore(analysis.overallScore)}
            </p>
          ) : (
            <p
              className={[
                "text-xs font-medium",
                isFailed ? "text-danger" : "text-warning",
              ].join(" ")}
            >
              {isFailed ? "Failed" : "Running"}
            </p>
          )}
        </div>
      </div>
    </Link>
  );
}

function formatScore(score: number): string {
  return `${Math.round(score * 100)}%`;
}
