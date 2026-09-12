import { Button } from "@/components/ui";

export type ErrorStateProps = {
  title?: string;
  description?: string;
  actionLabel?: string;
  onAction?: () => void;
};

export function ErrorState({
  title = "Something went wrong",
  description = "We couldn't complete your request. Please try again.",
  actionLabel = "Try again",
  onAction,
}: ErrorStateProps) {
  return (
    <div className="flex flex-col items-center justify-center px-6 py-16 text-center">
      <div
        aria-hidden="true"
        className="flex h-10 w-10 items-center justify-center rounded-md border border-danger/20 bg-danger/5 text-danger"
      >
        <svg
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="1.8"
          className="h-5 w-5"
        >
          <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v4" />
          <path strokeLinecap="round" strokeLinejoin="round" d="M12 17h.01" />
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M10.3 3.8 2.9 17a2 2 0 0 0 1.75 3h14.7a2 2 0 0 0 1.75-3L13.7 3.8a2 2 0 0 0-3.4 0Z"
          />
        </svg>
      </div>

      <h3 className="mt-4 text-sm font-semibold text-text-primary">{title}</h3>

      <p className="mt-1 max-w-md text-sm leading-6 text-text-secondary">
        {description}
      </p>

      {onAction && (
        <Button
          type="button"
          variant="secondary"
          size="sm"
          className="mt-5"
          onClick={onAction}
        >
          {actionLabel}
        </Button>
      )}
    </div>
  );
}
