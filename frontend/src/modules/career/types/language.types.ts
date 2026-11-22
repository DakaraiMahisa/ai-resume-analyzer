export type LanguageProficiency = "A1" | "A2" | "B1" | "B2" | "C1" | "C2";

export type CandidateLanguage = {
  id: string;
  languageName: string;
  speakingLevel: LanguageProficiency;
  listeningLevel: LanguageProficiency;
  readingLevel: LanguageProficiency;
  writingLevel: LanguageProficiency;
  nativeLanguage: boolean;
  createdAt: string;
  updatedAt: string;
};

export type CreateCandidateLanguageRequest = {
  languageName: string;
  speakingLevel: LanguageProficiency;
  listeningLevel: LanguageProficiency;
  readingLevel: LanguageProficiency;
  writingLevel: LanguageProficiency;
  nativeLanguage: boolean;
};

export type UpdateCandidateLanguageRequest = {
  languageName?: string | null;
  speakingLevel?: LanguageProficiency | null;
  listeningLevel?: LanguageProficiency | null;
  readingLevel?: LanguageProficiency | null;
  writingLevel?: LanguageProficiency | null;
  nativeLanguage?: boolean | null;
};
