
import { useState } from "react";

import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { EmptyState } from "@/components/feedback/EmptyState";
import { ErrorState } from "@/components/feedback/ErrorState";
import { LoadingState } from "@/components/feedback/LoadingState";

import { useCertifications } from "../../hooks/use-certifications";
import {
  useCreateCertification,
  useDeleteCertification,
  useUpdateCertification,
} from "../../hooks/use-certification-mutations";

import type {
  CandidateCertification,
  CreateCandidateCertificationRequest,
  UpdateCandidateCertificationRequest,
} from "../../types/certification.types";

import { CertificationForm } from "./CertificationForm";
import { CertificationEditForm } from "./CertificationEditForm";

function formatDate(date: string | null) {
  if (!date) return "Not provided";

  return new Date(`${date}T00:00:00`).toLocaleDateString();
}

export function CertificationsSection() {
  const certificationsQuery = useCertifications();

  const createMutation = useCreateCertification();
  const updateMutation = useUpdateCertification();
  const deleteMutation = useDeleteCertification();

  const [showCreateForm, setShowCreateForm] = useState(false);
  const [editingCertification, setEditingCertification] =
    useState<CandidateCertification | null>(null);

  function handleCreate(request: CreateCandidateCertificationRequest) {
    createMutation.mutate(request, {
      onSuccess: () => setShowCreateForm(false),
    });
  }

  function handleUpdate(request: UpdateCandidateCertificationRequest) {
    if (!editingCertification) return;

    updateMutation.mutate(
      {
        id: editingCertification.id,
        request,
      },
      {
        onSuccess: () => setEditingCertification(null),
      },
    );
  }

  function handleDelete(id: string) {
    const confirmed = window.confirm(
      "Are you sure you want to delete this certification?",
    );

    if (confirmed) {
      deleteMutation.mutate(id);
    }
  }

  if (certificationsQuery.isLoading) {
    return <LoadingState />;
  }

  if (certificationsQuery.isError) {
    return (
      <ErrorState
        title="Unable to load certifications"
        description="We couldn't retrieve your certifications. Please try again."
        actionLabel="Retry"
        onAction={() => certificationsQuery.refetch()}
      />
    );
  }

  const certifications = certificationsQuery.data ?? [];

  return (
    <section className="space-y-4">
      <div className="flex items-center justify-between gap-4">
        <div>
          <h2 className="text-lg font-semibold">Certifications</h2>
          <p className="text-sm text-muted-foreground">
            Add professional certifications and credentials.
          </p>
        </div>

        {!showCreateForm && !editingCertification && (
          <Button
            size="sm"
            onClick={() => setShowCreateForm(true)}
          >
            Add certification
          </Button>
        )}
      </div>

      {showCreateForm && (
        <Card className="p-4">
          <h3 className="mb-4 font-medium">Add certification</h3>

          <CertificationForm
            onSubmit={handleCreate}
            onCancel={() => setShowCreateForm(false)}
            isSubmitting={createMutation.isPending}
          />

          {createMutation.isError && (
            <p className="mt-3 text-sm text-destructive" role="alert">
              Failed to create certification. Please try again.
            </p>
          )}
        </Card>
      )}

      {editingCertification && (
        <Card className="p-4">
          <h3 className="mb-4 font-medium">Edit certification</h3>

          <CertificationEditForm
            certification={editingCertification}
            onSubmit={handleUpdate}
            onCancel={() => setEditingCertification(null)}
            isSubmitting={updateMutation.isPending}
          />

          {updateMutation.isError && (
            <p className="mt-3 text-sm text-destructive" role="alert">
              Failed to update certification. Please try again.
            </p>
          )}
        </Card>
      )}

      {certifications.length === 0 ? (
        <EmptyState
          title="No certifications yet"
          description="Add your professional certifications to strengthen your career profile."
        />
      ) : (
        <div className="space-y-3">
          {certifications.map((certification) => (
            <Card key={certification.id} className="p-4">
              <div className="flex flex-col justify-between gap-3 sm:flex-row">
                <div className="min-w-0 space-y-2">
                  <div>
                    <h3 className="font-semibold">
                      {certification.certificationName}
                    </h3>
                    <p className="text-sm text-muted-foreground">
                      {certification.issuingOrganization}
                    </p>
                  </div>

                  <div className="space-y-1 text-sm text-muted-foreground">
                    <p>
                      Issued: {formatDate(certification.issueDate)}
                    </p>

                    <p>
                      {certification.doesNotExpire
                        ? "Does not expire"
                        : `Expires: ${formatDate(certification.expirationDate)}`}
                    </p>

                    {certification.credentialId && (
                      <p>Credential ID: {certification.credentialId}</p>
                    )}
                  </div>

                  {certification.credentialUrl && (
                    <a
                      href={certification.credentialUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="inline-block text-sm text-primary underline"
                    >
                      View credential
                    </a>
                  )}
                </div>

                <div className="flex shrink-0 items-start gap-2">
                  <Button
                    size="sm"
                    variant="secondary"
                    onClick={() => {
                      setShowCreateForm(false);
                      setEditingCertification(certification);
                    }}
                    disabled={deleteMutation.isPending}
                  >
                    Edit
                  </Button>

                  <Button
                    size="sm"
                    variant="danger"
                    onClick={() => handleDelete(certification.id)}
                    loading={
                      deleteMutation.isPending &&
                      deleteMutation.variables === certification.id
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
          Failed to delete certification. Please try again.
        </p>
      )}
    </section>
  );
}
