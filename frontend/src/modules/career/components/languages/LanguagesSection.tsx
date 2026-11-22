import { useState } from "react";

import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { EmptyState } from "@/components/feedback/EmptyState";
import { ErrorState } from "@/components/feedback/ErrorState";
import { LoadingState } from "@/components/feedback/LoadingState";

import { useLanguages } from "../../hooks/use-languages";
import {
  useCreateLanguage,
  useDeleteLanguage,
  useUpdateLanguage,
} from "../../hooks/use-language-mutations";

import type {
  CandidateLanguage,
  CreateCandidateLanguageRequest,
  UpdateCandidateLanguageRequest,
} from "../../types/language.types";

import { LanguageForm } from "./LanguageForm";
import { LanguageEditForm } from "./LanguageEditForm";

export function LanguagesSection() {
  const languagesQuery = useLanguages();

  const createMutation = useCreateLanguage();
  const updateMutation = useUpdateLanguage();
  const deleteMutation = useDeleteLanguage();

  const [showCreateForm, setShowCreateForm] = useState(false);
  const [editingLanguage, setEditingLanguage] =
    useState<CandidateLanguage | null>(null);

  function handleCreate(request: CreateCandidateLanguageRequest) {
    createMutation.mutate(request, {
      onSuccess: () => setShowCreateForm(false),
    });
  }

  function handleUpdate(request: UpdateCandidateLanguageRequest) {
    if (!editingLanguage) return;

    updateMutation.mutate(
      {
        id: editingLanguage.id,
        request,
      },
      {
        onSuccess: () => setEditingLanguage(null),
      },
    );
  }

  function handleDelete(id: string) {
    const confirmed = window.confirm(
      "Are you sure you want to delete this language?",
    );

    if (confirmed) {
      deleteMutation.mutate(id);
    }
  }

  if (languagesQuery.isLoading) {
    return <LoadingState />;
  }

  if (languagesQuery.isError) {
    return (
      <ErrorState
        title="Unable to load languages"
        description="We couldn't retrieve your languages. Please try again."
        actionLabel="Retry"
        onAction={() => languagesQuery.refetch()}
      />
    );
  }

  const languages = languagesQuery.data ?? [];

  return (
    <section className="space-y-4">
      <div className="flex items-center justify-between gap-4">
        <div>
          <h2 className="text-lg font-semibold">Languages</h2>
          <p className="text-sm text-muted-foreground">
            Showcase your language skills and proficiency levels.
          </p>
        </div>

        {!showCreateForm && !editingLanguage && (
          <Button size="sm" onClick={() => setShowCreateForm(true)}>
            Add language
          </Button>
        )}
      </div>

      {showCreateForm && (
        <Card className="p-4">
          <h3 className="mb-4 font-medium">Add language</h3>

          <LanguageForm
            onSubmit={handleCreate}
            onCancel={() => setShowCreateForm(false)}
            isSubmitting={createMutation.isPending}
          />

          {createMutation.isError && (
            <p className="mt-3 text-sm text-destructive" role="alert">
              Failed to add language. It may already exist in your profile.
              Please check and try again.
            </p>
          )}
        </Card>
      )}

      {editingLanguage && (
        <Card className="p-4">
          <h3 className="mb-4 font-medium">Edit language</h3>

          <LanguageEditForm
            language={editingLanguage}
            onSubmit={handleUpdate}
            onCancel={() => setEditingLanguage(null)}
            isSubmitting={updateMutation.isPending}
          />

          {updateMutation.isError && (
            <p className="mt-3 text-sm text-destructive" role="alert">
              Failed to update language. Please try again.
            </p>
          )}
        </Card>
      )}

      {languages.length === 0 ? (
        <EmptyState
          title="No languages added yet"
          description="Add the languages you speak and your proficiency in each skill."
        />
      ) : (
        <div className="space-y-3">
          {languages.map((language) => (
            <Card key={language.id} className="p-4">
              <div className="flex flex-col justify-between gap-3 sm:flex-row">
                <div className="min-w-0 space-y-3">
                  <div>
                    <h3 className="font-semibold">{language.languageName}</h3>
                    {language.nativeLanguage && (
                      <span className="text-sm text-muted-foreground">
                        Native language
                      </span>
                    )}
                  </div>

                  <div className="grid grid-cols-2 gap-x-6 gap-y-2 text-sm sm:grid-cols-4">
                    <div>
                      <p className="text-muted-foreground">Speaking</p>
                      <p className="font-medium">{language.speakingLevel}</p>
                    </div>

                    <div>
                      <p className="text-muted-foreground">Listening</p>
                      <p className="font-medium">{language.listeningLevel}</p>
                    </div>

                    <div>
                      <p className="text-muted-foreground">Reading</p>
                      <p className="font-medium">{language.readingLevel}</p>
                    </div>

                    <div>
                      <p className="text-muted-foreground">Writing</p>
                      <p className="font-medium">{language.writingLevel}</p>
                    </div>
                  </div>
                </div>

                <div className="flex shrink-0 items-start gap-2">
                  <Button
                    size="sm"
                    variant="secondary"
                    onClick={() => {
                      setShowCreateForm(false);
                      setEditingLanguage(language);
                    }}
                    disabled={deleteMutation.isPending}
                  >
                    Edit
                  </Button>

                  <Button
                    size="sm"
                    variant="danger"
                    onClick={() => handleDelete(language.id)}
                    loading={
                      deleteMutation.isPending &&
                      deleteMutation.variables === language.id
                    }
                  >
                    Delete
                  </Button>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}

      {deleteMutation.isError && (
        <p className="text-sm text-destructive" role="alert">
          Failed to delete language. Please try again.
        </p>
      )}
    </section>
  );
}
