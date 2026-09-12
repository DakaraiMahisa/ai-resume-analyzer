import { Button, Card } from "@/components/ui";

type AnalyzeMatchActionProps = {
  canAnalyze: boolean;
  loading?: boolean;
  onAnalyze: () => void;
};

export function AnalyzeMatchAction({
  canAnalyze,
  loading = false,
  onAnalyze,
}: AnalyzeMatchActionProps) {
  return (
    <Card className="border-brand/20 bg-brand/5 p-5 sm:p-6">
      <div className="flex flex-col gap-5 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-sm font-medium text-brand">
            {canAnalyze ? "Ready to analyze" : "Complete both selections"}
          </p>

          <h2 className="mt-1 text-base font-semibold text-text-primary">
            See how well your resume fits the job
          </h2>

          <p className="mt-1 max-w-2xl text-sm leading-6 text-text-secondary">
            We'll compare the requirements against your resume and explain your
            strengths, gaps, and areas to improve.
          </p>
        </div>

        <Button
          type="button"
          size="lg"
          onClick={onAnalyze}
          disabled={!canAnalyze || loading}
          loading={loading}
        >
          Analyze match
        </Button>
      </div>
    </Card>
  );
}
