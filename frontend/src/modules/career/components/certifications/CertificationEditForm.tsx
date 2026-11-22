import { useState, type FormEvent } from "react";

import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";

import type {
  CandidateCertification,
  UpdateCandidateCertificationRequest,
} from "../../types/certification.types";

type CertificationEditFormProps = {
  certification: CandidateCertification;
  onSubmit: (request: UpdateCandidateCertificationRequest) => void;
  onCancel: () => void;
  isSubmitting: boolean;
};

export function CertificationEditForm({
  certification,
  onSubmit,
  onCancel,
  isSubmitting,
}: CertificationEditFormProps) {
  const [certificationName, setCertificationName] = useState(
    certification.certificationName,
  );
  const [issuingOrganization, setIssuingOrganization] = useState(
    certification.issuingOrganization,
  );
  const [issueDate, setIssueDate] = useState(certification.issueDate ?? "");
  const [expirationDate, setExpirationDate] = useState(
    certification.expirationDate ?? "",
  );
  const [credentialId, setCredentialId] = useState(
    certification.credentialId ?? "",
  );
  const [credentialUrl, setCredentialUrl] = useState(
    certification.credentialUrl ?? "",
  );
  const [doesNotExpire, setDoesNotExpire] = useState(
    certification.doesNotExpire,
  );

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const request: UpdateCandidateCertificationRequest = {};

    if (certificationName.trim() !== certification.certificationName) {
      request.certificationName = certificationName.trim();
    }

    if (issuingOrganization.trim() !== certification.issuingOrganization) {
      request.issuingOrganization = issuingOrganization.trim();
    }

    if (issueDate !== (certification.issueDate ?? "")) {
      request.issueDate = issueDate || null;
    }

    if (doesNotExpire !== certification.doesNotExpire) {
      request.doesNotExpire = doesNotExpire;
    }

    if (doesNotExpire) {
      if (
        !certification.doesNotExpire ||
        certification.expirationDate !== null
      ) {
        request.expirationDate = null;
      }
    } else if (
      expirationDate !== (certification.expirationDate ?? "") ||
      certification.doesNotExpire
    ) {
      request.expirationDate = expirationDate || null;
    }

    if (credentialId !== (certification.credentialId ?? "")) {
      request.credentialId = credentialId;
    }

    if (credentialUrl !== (certification.credentialUrl ?? "")) {
      request.credentialUrl = credentialUrl;
    }

    onSubmit(request);
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="edit-certificationName">Certification name</Label>
        <Input
          id="edit-certificationName"
          value={certificationName}
          onChange={(event) => setCertificationName(event.target.value)}
          required
          maxLength={255}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="edit-issuingOrganization">Issuing organization</Label>
        <Input
          id="edit-issuingOrganization"
          value={issuingOrganization}
          onChange={(event) => setIssuingOrganization(event.target.value)}
          required
          maxLength={255}
        />
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div className="space-y-2">
          <Label htmlFor="edit-issueDate">Issue date</Label>
          <Input
            id="edit-issueDate"
            type="date"
            value={issueDate}
            onChange={(event) => setIssueDate(event.target.value)}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="edit-expirationDate">Expiration date</Label>
          <Input
            id="edit-expirationDate"
            type="date"
            value={expirationDate}
            onChange={(event) => setExpirationDate(event.target.value)}
            disabled={doesNotExpire}
          />
        </div>
      </div>

      <div className="flex items-center gap-2">
        <input
          id="edit-doesNotExpire"
          type="checkbox"
          checked={doesNotExpire}
          onChange={(event) => {
            setDoesNotExpire(event.target.checked);
            if (event.target.checked) setExpirationDate("");
          }}
          className="h-4 w-4"
        />
        <Label htmlFor="edit-doesNotExpire">
          This certification does not expire
        </Label>
      </div>

      <div className="space-y-2">
        <Label htmlFor="edit-credentialId">Credential ID</Label>
        <Input
          id="edit-credentialId"
          value={credentialId}
          onChange={(event) => setCredentialId(event.target.value)}
          maxLength={255}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="edit-credentialUrl">Credential URL</Label>
        <Input
          id="edit-credentialUrl"
          type="url"
          value={credentialUrl}
          onChange={(event) => setCredentialUrl(event.target.value)}
          maxLength={500}
        />
      </div>

      <div className="flex justify-end gap-2">
        <Button type="button" variant="secondary" onClick={onCancel}>
          Cancel
        </Button>

        <Button type="submit" loading={isSubmitting}>
          Save changes
        </Button>
      </div>
    </form>
  );
}
