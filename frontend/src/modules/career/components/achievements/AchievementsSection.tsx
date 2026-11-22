import { useState } from "react";

import { Button, Card } from "@/components/ui";
import { EmptyState, ErrorState, LoadingState } from "@/components/feedback";

import { useAchievements } from "../../hooks/use-achievements";
import { useAchievementMutations } from "../../hooks/use-achievement-mutations";

import type {
  CandidateAchievement,
  CreateCandidateAchievementRequest,
  UpdateCandidateAchievementRequest,
} from "../../types/achievement.types";

import { AchievementForm } from "./AchievementForm";
import { AchievementEditForm } from "./AchievementEditForm";

const formatAchievementType = (type: string) =>
  type
    .toLowerCase()
    .replaceAll("_", " ")
    .replace(/\b\w/g, (character) => character.toUpperCase());

export const AchievementsSection = () => {
  const achievementsQuery = useAchievements();

  const { createAchievement, updateAchievement, deleteAchievement } =
    useAchievementMutations();

  const [showForm, setShowForm] = useState(false);
  const [editingAchievementId, setEditingAchievementId] = useState<
    string | null
  >(null);

  const handleCreate = (request: CreateCandidateAchievementRequest) => {
    createAchievement.mutate(request, {
      onSuccess: () => setShowForm(false),
    });
  };

  const handleUpdate = (
    achievementId: string,
    request: UpdateCandidateAchievementRequest,
  ) => {
    updateAchievement.mutate(
      { achievementId, request },
      {
        onSuccess: () => setEditingAchievementId(null),
      },
    );
  };

  const handleDelete = (achievementId: string) => {
    if (!window.confirm("Are you sure you want to delete this achievement?")) {
      return;
    }

    deleteAchievement.mutate(achievementId);
  };

  if (achievementsQuery.isLoading) {
    return <LoadingState />;
  }

  if (achievementsQuery.isError) {
    return (
      <ErrorState
        title="Unable to load achievements"
        description="We couldn't retrieve your achievements. Please try again."
        actionLabel="Retry"
        onAction={() => achievementsQuery.refetch()}
      />
    );
  }

  const achievements = achievementsQuery.data ?? [];

  return (
    <section className="space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h2 className="text-lg font-semibold">Achievements</h2>
          <p className="text-sm text-muted-foreground">
            Showcase awards, scholarships, honours, and other recognition.
          </p>
        </div>

        {!showForm && (
          <Button onClick={() => setShowForm(true)}>Add achievement</Button>
        )}
      </div>

      {showForm && (
        <Card className="p-4">
          <AchievementForm
            onSubmit={handleCreate}
            onCancel={() => setShowForm(false)}
            loading={createAchievement.isPending}
          />
        </Card>
      )}

      {achievements.length === 0 && !showForm ? (
        <EmptyState
          title="No achievements added"
          description="Add awards, scholarships, competitions, or other recognition to strengthen your career profile."
        />
      ) : (
        <div className="space-y-3">
          {achievements.map((achievement: CandidateAchievement) => (
            <Card key={achievement.id} className="p-4">
              {editingAchievementId === achievement.id ? (
                <AchievementEditForm
                  achievement={achievement}
                  onSubmit={(request) => handleUpdate(achievement.id, request)}
                  onCancel={() => setEditingAchievementId(null)}
                  loading={updateAchievement.isPending}
                />
              ) : (
                <div className="space-y-3">
                  <div className="flex flex-wrap items-start justify-between gap-3">
                    <div className="min-w-0 space-y-1">
                      <h3 className="font-semibold">
                        {achievement.achievementName}
                      </h3>

                      {achievement.issuingOrganization && (
                        <p className="text-sm text-muted-foreground">
                          {achievement.issuingOrganization}
                        </p>
                      )}

                      <p className="text-sm text-muted-foreground">
                        {formatAchievementType(achievement.achievementType)}
                        {achievement.issueDate && ` · ${achievement.issueDate}`}
                      </p>
                    </div>

                    <div className="flex gap-2">
                      <Button
                        size="sm"
                        variant="secondary"
                        onClick={() => setEditingAchievementId(achievement.id)}
                      >
                        Edit
                      </Button>

                      <Button
                        size="sm"
                        variant="danger"
                        onClick={() => handleDelete(achievement.id)}
                        loading={deleteAchievement.isPending}
                      >
                        Delete
                      </Button>
                    </div>
                  </div>

                  {achievement.description && (
                    <p className="whitespace-pre-wrap text-sm">
                      {achievement.description}
                    </p>
                  )}

                  {achievement.achievementUrl && (
                    <a
                      href={achievement.achievementUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-sm text-primary underline underline-offset-4"
                    >
                      View achievement
                    </a>
                  )}
                </div>
              )}
            </Card>
          ))}
        </div>
      )}
    </section>
  );
};
