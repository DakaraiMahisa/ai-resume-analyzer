import { useState, type FormEvent } from "react";

import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";

import type { CreateCandidateCertificationRequest } from "../../types/certification.types";

type CertificationFormProps = {
  onSubmit: (request: CreateCandidateCertificationRequest) => void;
  onCancel: () => void;
  isSubmitting: boolean;
};

export function CertificationForm({
  onSubmit,
  onCancel,
  isSubmitting,
}: CertificationFormProps) {
  const [certificationName, setCertificationName] = useState("");
  const [issuingOrganization, setIssuingOrganization] = useState("");
  const [issueDate, setIssueDate] = useState("");
  const [expirationDate, setExpirationDate] = useState("");
  const [credentialId, setCredentialId] = useState("");
  const [credentialUrl, setCredentialUrl] = useState("");
  const [doesNotExpire, setDoesNotExpire] = useState(false);

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    onSubmit({
      certificationName: certificationName.trim(),
      issuingOrganization: issuingOrganization.trim(),
      issueDate: issueDate || null,
      expirationDate: doesNotExpire ? null : expirationDate || null,
      credentialId: credentialId.trim() || null,
      credentialUrl: credentialUrl.trim() || null,
      doesNotExpire,
    });
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="certificationName">Certification name</Label>
        <Input
          id="certificationName"
          value={certificationName}
          onChange={(event) => setCertificationName(event.target.value)}
          placeholder="e.g. AWS Certified Developer"
          required
          maxLength={255}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="issuingOrganization">Issuing organization</Label>
        <Input
          id="issuingOrganization"
          value={issuingOrganization}
          onChange={(event) => setIssuingOrganization(event.target.value)}
          placeholder="e.g. Amazon Web Services"
          required
          maxLength={255}
        />
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div className="space-y-2">
          <Label htmlFor="issueDate">Issue date</Label>
          <Input
            id="issueDate"
            type="date"
            value={issueDate}
            onChange={(event) => setIssueDate(event.target.value)}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="expirationDate">Expiration date</Label>
          <Input
            id="expirationDate"
            type="date"
            value={expirationDate}
            onChange={(event) => setExpirationDate(event.target.value)}
            disabled={doesNotExpire}
          />
        </div>
      </div>

      <div className="flex items-center gap-2">
        <input
          id="doesNotExpire"
          type="checkbox"
          checked={doesNotExpire}
          onChange={(event) => {
            setDoesNotExpire(event.target.checked);
            if (event.target.checked) setExpirationDate("");
          }}
          className="h-4 w-4"
        />
        <Label htmlFor="doesNotExpire">
          This certification does not expire
        </Label>
      </div>

      <div className="space-y-2">
        <Label htmlFor="credentialId">Credential ID</Label>
        <Input
          id="credentialId"
          value={credentialId}
          onChange={(event) => setCredentialId(event.target.value)}
          placeholder="Optional credential ID"
          maxLength={255}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="credentialUrl">Credential URL</Label>
        <Input
          id="credentialUrl"
          type="url"
          value={credentialUrl}
          onChange={(event) => setCredentialUrl(event.target.value)}
          placeholder="https://..."
          maxLength={500}
        />
      </div>

      <div className="flex justify-end gap-2">
        <Button type="button" variant="secondary" onClick={onCancel}>
          Cancel
        </Button>

        <Button type="submit" loading={isSubmitting}>
          Add certification
        </Button>
      </div>
    </form>
  );
}
