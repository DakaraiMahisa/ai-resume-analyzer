import { PageHeader } from "@/components/common";
import { ResumeList, ResumeUpload } from "@/modules/resume";
export function ResumesPage() {
  return (
    <div className="space-y-8">
      <PageHeader
        eyebrow="Resume intelligence"
        title="Resumes"
        description="Upload and manage the resumes you want to analyze against job descriptions."
      />
      <ResumeUpload />
      <ResumeList />
    </div>
  );
}
