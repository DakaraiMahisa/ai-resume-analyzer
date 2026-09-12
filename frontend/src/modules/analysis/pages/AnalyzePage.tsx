import { useState } from "react";
import { PageHeader } from "@/components/common";
import { Card } from "@/components/ui";

import { useNavigate } from "react-router-dom";

import { useAnalyzeResume } from "@/modules/analysis";
import { ResumeSelector } from "@/modules/analysis/components/ResumeSelector";
import type { ResumeSummary } from "@/modules/resume/types/resume.types";
import { JobDescriptionSelector } from "@/modules/analysis/components/JobDescriptionSelector";
import type { JobDescriptionSummaryResponse } from "@/modules/job/types/job-description.types";
import { AnalyzeMatchAction } from "@/modules/analysis/components/AnalyzeMatchAction";

export function AnalyzePage() {
  const [selectedResume, setSelectedResume] = useState<ResumeSummary | null>(
    null,
  );
  const [selectedJobDescription, setSelectedJobDescription] =
    useState<JobDescriptionSummaryResponse | null>(null);

  const navigate = useNavigate();

  const analyzeResumeMutation = useAnalyzeResume();
  return (
    <div className="space-y-8">
      <PageHeader
        eyebrow="Resume analysis"
        title="Analyze your resume"
        description="See how well your resume matches a specific job and understand what you can improve."
      />

      <Card className="p-6">
        <div className="max-w-5xl">
          <h2 className="text-base font-semibold text-text-primary">
            Build your analysis
          </h2>

          <p className="mt-1 text-sm leading-6 text-text-secondary">
            Choose a resume and a target job to evaluate your fit.
          </p>

          <div className="mt-8 grid gap-6 lg:grid-cols-2">
            <div className="rounded-md border border-border p-5">
              <p className="text-xs font-medium uppercase tracking-wide text-text-tertiary">
                Step 1
              </p>

              <h3 className="mt-2 text-base font-semibold text-text-primary">
                Choose a resume
              </h3>

              <p className="mt-1 text-sm leading-6 text-text-secondary">
                Select one of your processed resumes.
              </p>

              <div className="mt-5">
                <ResumeSelector
                  selectedResumeId={selectedResume?.id ?? null}
                  onSelect={setSelectedResume}
                />
              </div>
            </div>

            <div className="rounded-md border border-border p-5">
              <p className="text-xs font-medium uppercase tracking-wide text-text-tertiary">
                Step 2
              </p>

              <h3 className="mt-2 text-base font-semibold text-text-primary">
                Choose a target job
              </h3>

              <p className="mt-1 text-sm leading-6 text-text-secondary">
                Select one of your processed job descriptions.
              </p>

              <div className="mt-5">
                <JobDescriptionSelector
                  selectedJobDescriptionId={selectedJobDescription?.id ?? null}
                  onSelect={setSelectedJobDescription}
                />
              </div>
            </div>
          </div>
          <AnalyzeMatchAction
            canAnalyze={
              selectedResume !== null && selectedJobDescription !== null
            }
            loading={analyzeResumeMutation.isPending}
            onAnalyze={() => {
              if (!selectedResume || !selectedJobDescription) {
                return;
              }

              analyzeResumeMutation.mutate(
                {
                  resumeId: selectedResume.id,
                  jobDescriptionId: selectedJobDescription.id,
                },
                {
                  onSuccess: (analysis) => {
                    navigate(`/app/analyses/${analysis.analysisId}`);
                  },
                },
              );
            }}
          />
        </div>
      </Card>
    </div>
  );
}
