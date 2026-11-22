import { useCandidateProfileCompletion } from "@/modules/career/hooks/use-profile";

export function ProfileCompletionCard() {
  const completionQuery = useCandidateProfileCompletion();

  if (completionQuery.isLoading) {
    return (
      <div className="rounded-lg border border-border bg-surface p-5">
        <p className="text-sm text-text-secondary">
          Calculating profile completion...
        </p>
      </div>
    );
  }

  if (completionQuery.isError || !completionQuery.data) {
    return (
      <div
        role="alert"
        className="rounded-lg border border-border bg-surface p-5"
      >
        <p className="text-sm text-text-secondary">
          Profile completion information is currently unavailable.
        </p>
      </div>
    );
  }

  const completion = completionQuery.data;

  return (
    <section className="rounded-lg border border-border bg-surface p-5">
      <div className="flex items-center justify-between gap-4">
        <div>
          <h2 className="font-semibold text-text-primary">
            Profile completion
          </h2>

          <p className="mt-1 text-sm text-text-secondary">
            {completion.completedFields} of {completion.totalFields} fields
            completed
          </p>
        </div>

        <span className="text-2xl font-semibold text-brand">
          {completion.completionPercentage}%
        </span>
      </div>

      <div
        className="mt-4 h-2 overflow-hidden rounded-full bg-border"
        role="progressbar"
        aria-label="Profile completion"
        aria-valuenow={completion.completionPercentage}
        aria-valuemin={0}
        aria-valuemax={100}
      >
        <div
          className="h-full rounded-full bg-brand transition-all duration-300"
          style={{ width: `${completion.completionPercentage}%` }}
        />
      </div>

      {completion.isComplete ? (
        <p className="mt-3 text-sm text-text-secondary">
          Your professional profile is complete.
        </p>
      ) : (
        <div className="mt-4">
          <p className="text-sm font-medium text-text-primary">
            Fields to complete
          </p>

          <ul className="mt-2 list-inside list-disc space-y-1 text-sm text-text-secondary">
            {completion.missingFields.map((field) => (
              <li key={field}>{field}</li>
            ))}
          </ul>
        </div>
      )}
    </section>
  );
}
