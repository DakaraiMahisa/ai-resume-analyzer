import type {
  CandidateCareerPreference,
  UpdateCandidateCareerPreferenceRequest,
} from "../../types/preference.types";

import { CareerPreferenceForm } from "./CareerPreferenceForm";

type CareerPreferenceEditFormProps = {
  preference: CandidateCareerPreference;

  onUpdate: (request: UpdateCandidateCareerPreferenceRequest) => void;
  onCancel: () => void;
  loading?: boolean;
};

export const CareerPreferenceEditForm = ({
  preference,
  onUpdate,
  onCancel,
  loading = false,
}: CareerPreferenceEditFormProps) => {
  return (
    <CareerPreferenceForm
      preference={preference}
      onUpdate={onUpdate}
      onCancel={onCancel}
      loading={loading}
    />
  );
};
