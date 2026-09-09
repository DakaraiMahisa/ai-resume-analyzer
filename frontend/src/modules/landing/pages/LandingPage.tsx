import { Link } from "react-router-dom";

import { Badge, Button, Card } from "@/components/ui";

export function LandingPage() {
  return (
    <div className="overflow-hidden">
      {/* Hero */}
      <section className="relative">
        <div className="mx-auto grid max-w-7xl gap-12 px-6 py-20 sm:py-24 lg:grid-cols-[1.05fr_0.95fr] lg:items-center lg:gap-16 lg:px-8 lg:py-28">
          <div>
            <Badge variant="info">
              Resume intelligence, built for the real job
            </Badge>

            <h1 className="mt-6 max-w-3xl text-4xl font-semibold tracking-tight text-text-primary sm:text-5xl lg:text-6xl">
              Stop guessing whether your resume matches the job.
            </h1>

            <p className="mt-6 max-w-2xl text-base leading-7 text-text-secondary sm:text-lg">
              AI Resume Analyzer compares your resume with a specific job
              description to uncover relevant skills, supporting evidence,
              missing requirements, and actionable opportunities to improve.
            </p>

            <div className="mt-8 flex flex-col gap-3 sm:flex-row">
              <Link to="/register">
                <Button size="lg" className="w-full sm:w-auto">
                  Analyze your resume
                </Button>
              </Link>

              <a href="#how-it-works">
                <Button
                  variant="secondary"
                  size="lg"
                  className="w-full sm:w-auto"
                >
                  See how it works
                </Button>
              </a>
            </div>

            <div className="mt-8 flex flex-wrap gap-x-6 gap-y-2 text-sm text-text-tertiary">
              <span>No guesswork</span>
              <span>Job-specific insights</span>
              <span>Evidence-backed analysis</span>
            </div>
          </div>

          {/* Product Preview */}
          <div className="relative">
            <div className="absolute -inset-4 -z-10 rounded-3xl bg-brand/5 blur-2xl" />

            <Card className="overflow-hidden shadow-lg">
              <div className="border-b border-border px-5 py-4">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm font-semibold text-text-primary">
                      Resume analysis
                    </p>
                    <p className="mt-1 text-xs text-text-tertiary">
                      Software Engineer · 2 minutes ago
                    </p>
                  </div>

                  <Badge variant="success">Strong match</Badge>
                </div>
              </div>

              <div className="grid gap-6 p-5 sm:p-6">
                <div className="flex items-end justify-between">
                  <div>
                    <p className="text-sm text-text-secondary">Overall match</p>

                    <p className="mt-1 font-mono text-4xl font-semibold tracking-tight text-text-primary">
                      87%
                    </p>
                  </div>

                  <p className="text-sm font-medium text-success">
                    Above average
                  </p>
                </div>

                <div className="h-2 overflow-hidden rounded-full bg-surface-muted">
                  <div className="h-full w-[87%] rounded-full bg-brand" />
                </div>

                <div className="grid grid-cols-3 gap-3">
                  <div className="rounded-md border border-border bg-surface-muted p-3">
                    <p className="text-xs text-text-tertiary">Skills matched</p>
                    <p className="mt-1 font-mono text-lg font-semibold text-text-primary">
                      18
                    </p>
                  </div>

                  <div className="rounded-md border border-border bg-surface-muted p-3">
                    <p className="text-xs text-text-tertiary">Requirements</p>
                    <p className="mt-1 font-mono text-lg font-semibold text-text-primary">
                      21
                    </p>
                  </div>

                  <div className="rounded-md border border-border bg-surface-muted p-3">
                    <p className="text-xs text-text-tertiary">Gaps found</p>
                    <p className="mt-1 font-mono text-lg font-semibold text-text-primary">
                      3
                    </p>
                  </div>
                </div>

                <div>
                  <div className="flex items-center justify-between">
                    <p className="text-sm font-semibold text-text-primary">
                      Key findings
                    </p>

                    <span className="text-xs text-text-tertiary">
                      Evidence-backed
                    </span>
                  </div>

                  <div className="mt-3 space-y-2">
                    <div className="rounded-md border border-border p-3">
                      <p className="text-sm font-medium text-text-primary">
                        Strong Java experience
                      </p>
                      <p className="mt-1 text-xs leading-5 text-text-secondary">
                        Supported by projects and professional experience in the
                        submitted resume.
                      </p>
                    </div>

                    <div className="rounded-md border border-border p-3">
                      <p className="text-sm font-medium text-text-primary">
                        Docker requirement partially supported
                      </p>
                      <p className="mt-1 text-xs leading-5 text-text-secondary">
                        Your resume mentions Docker, but provides limited
                        evidence of production usage.
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </Card>
          </div>
        </div>
      </section>

      {/* Value proposition */}
      <section className="border-y border-border bg-surface-muted">
        <div className="mx-auto max-w-7xl px-6 py-16 sm:py-20 lg:px-8">
          <div className="max-w-2xl">
            <p className="text-sm font-medium text-brand">
              More than an ATS score
            </p>

            <h2 className="mt-2 text-2xl font-semibold tracking-tight text-text-primary sm:text-3xl">
              Understand why your resume matches — and where it falls short.
            </h2>

            <p className="mt-4 text-sm leading-6 text-text-secondary sm:text-base">
              A single score tells you very little. AI Resume Analyzer turns
              your resume and the target job into structured, explainable
              intelligence you can actually act on.
            </p>
          </div>

          <div className="mt-10 grid gap-4 md:grid-cols-3">
            <Card className="p-6">
              <p className="font-mono text-sm text-brand">01</p>

              <h3 className="mt-4 text-base font-semibold text-text-primary">
                Extract what matters
              </h3>

              <p className="mt-2 text-sm leading-6 text-text-secondary">
                Identify skills, experience, education, technologies, and other
                claims contained in your resume.
              </p>
            </Card>

            <Card className="p-6">
              <p className="font-mono text-sm text-brand">02</p>

              <h3 className="mt-4 text-base font-semibold text-text-primary">
                Compare against the job
              </h3>

              <p className="mt-2 text-sm leading-6 text-text-secondary">
                Break the job description into meaningful requirements and
                determine how well your resume supports each one.
              </p>
            </Card>

            <Card className="p-6">
              <p className="font-mono text-sm text-brand">03</p>

              <h3 className="mt-4 text-base font-semibold text-text-primary">
                Act on the gaps
              </h3>

              <p className="mt-2 text-sm leading-6 text-text-secondary">
                See where your resume is strong, where evidence is weak, and
                what deserves attention before you apply.
              </p>
            </Card>
          </div>
        </div>
      </section>

      {/* How it works */}
      <section id="how-it-works">
        <div className="mx-auto max-w-7xl px-6 py-20 sm:py-24 lg:px-8">
          <div className="text-center">
            <p className="text-sm font-medium text-brand">How it works</p>

            <h2 className="mt-2 text-2xl font-semibold tracking-tight text-text-primary sm:text-3xl">
              From documents to useful decisions
            </h2>

            <p className="mx-auto mt-4 max-w-2xl text-sm leading-6 text-text-secondary sm:text-base">
              The analysis pipeline turns unstructured documents into structured
              resume intelligence.
            </p>
          </div>

          <div className="mt-12 grid gap-8 md:grid-cols-4">
            {[
              {
                number: "01",
                title: "Upload",
                description:
                  "Submit your resume and the job description you want to target.",
              },
              {
                number: "02",
                title: "Understand",
                description:
                  "The system extracts structured claims and requirements from both documents.",
              },
              {
                number: "03",
                title: "Compare",
                description:
                  "Resume evidence is evaluated against the requirements of the target role.",
              },
              {
                number: "04",
                title: "Improve",
                description:
                  "Review the resulting insights and focus your application where it matters.",
              },
            ].map((step) => (
              <div key={step.number} className="relative">
                <p className="font-mono text-sm text-brand">{step.number}</p>

                <h3 className="mt-4 text-base font-semibold text-text-primary">
                  {step.title}
                </h3>

                <p className="mt-2 text-sm leading-6 text-text-secondary">
                  {step.description}
                </p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="border-t border-border bg-surface-muted">
        <div className="mx-auto max-w-4xl px-6 py-20 text-center sm:py-24">
          <p className="text-sm font-medium text-brand">
            Your next application starts here
          </p>

          <h2 className="mt-3 text-3xl font-semibold tracking-tight text-text-primary sm:text-4xl">
            Know what your resume says before the recruiter does.
          </h2>

          <p className="mx-auto mt-4 max-w-2xl text-sm leading-6 text-text-secondary sm:text-base">
            Analyze your resume against the role you actually want and replace
            guesswork with evidence.
          </p>

          <div className="mt-8">
            <Link to="/register">
              <Button size="lg">Analyze your resume</Button>
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
}
