import { isAxiosError } from "axios";

import { Button } from "@/components/ui";
import { useCandidateProfile } from "@/modules/career/hooks/use-profile";
import { ProfessionalInformationForm } from "@/modules/career/components/profile/ProfessionalInformationForm";
import { ProfileCompletionCard } from "@/modules/career/components/profile/ProfileCompletionCard";
import { ProfessionalExperienceSection } from "@/modules/career/components/experience/ProfessionalExperienceSection";
import { EducationSection } from "@/modules/career/components/education/EducationSection";

import { SkillsSection } from "@/modules/career/components/skills/SkillsSection";

import { ProjectsSection } from "@/modules/career/components/projects/ProjectsSection";
import { CertificationsSection } from "../components/certifications/CertificationsSection";
import { LanguagesSection } from "../components/languages/LanguagesSection";
import { AchievementsSection } from "../components/achievements/AchievementsSection";
import { CareerPreferencesSection } from "../components/preferences/CareerPreferencesSection";

export function CareerProfilePage() {
  const profileQuery = useCandidateProfile();

  const isProfileNotFound =
    isAxiosError(profileQuery.error) &&
    profileQuery.error.response?.status === 404;

  if (profileQuery.isLoading) {
    return (
      <div className="flex min-h-64 items-center justify-center">
        <p className="text-sm text-text-secondary">
          Loading your career profile...
        </p>
      </div>
    );
  }

  if (profileQuery.isError && !isProfileNotFound) {
    return (
      <section className="space-y-4">
        <h1 className="text-xl font-semibold text-text-primary">
          Career Profile
        </h1>

        <div
          role="alert"
          className="rounded-lg border border-border bg-surface p-5"
        >
          <p className="font-medium text-text-primary">
            Unable to load your career profile.
          </p>

          <p className="mt-1 text-sm text-text-secondary">
            {profileQuery.error.message ||
              "Something went wrong while retrieving your profile."}
          </p>

          <Button
            className="mt-4"
            variant="secondary"
            onClick={() => profileQuery.refetch()}
          >
            Try again
          </Button>
        </div>
      </section>
    );
  }

  const profile = profileQuery.data;

  return (
    <section className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold text-text-primary">
          Career Profile
        </h1>

        <p className="mt-1 text-sm text-text-secondary">
          Maintain your professional information, experience, skills and career
          details in one place.
        </p>
      </div>

      <ProfileCompletionCard />
      <div className="rounded-lg border border-border bg-surface p-5 sm:p-6">
        <div className="mb-6">
          <h2 className="text-lg font-semibold text-text-primary">
            Professional Information
          </h2>

          <p className="mt-1 text-sm text-text-secondary">
            {profile
              ? "Keep your professional details up to date."
              : "Start by creating your professional profile."}
          </p>
        </div>
        <ProfessionalInformationForm
          key={profile?.id ?? "new-profile"}
          profile={profile}
        />
      </div>
      {profile && (
        <>
          <ProfessionalExperienceSection />
          <EducationSection />
          <SkillsSection />
          <ProjectsSection />
          <CertificationsSection />
          <LanguagesSection />
          <AchievementsSection />
          <CareerPreferencesSection />
        </>
      )}
    </section>
  );
}
