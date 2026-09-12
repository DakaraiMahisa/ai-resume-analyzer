import { Spinner } from "@/components/ui";

export type LoadingStateProps = {
  message?: string;
};

export function LoadingState({ message = "Loading..." }: LoadingStateProps) {
  return (
    <div className="flex flex-col items-center justify-center px-6 py-16 text-center">
      <Spinner size="md" />

      <p className="mt-4 text-sm text-text-secondary">{message}</p>
    </div>
  );
}
