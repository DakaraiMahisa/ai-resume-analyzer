const REFRESH_TOKEN_KEY = "ai-resume-analyzer.refresh-token";

let accessToken: string | null = null;

export const tokenStorage = {
  getAccessToken(): string | null {
    return accessToken;
  },

  setAccessToken(token: string): void {
    accessToken = token;
  },

  clearAccessToken(): void {
    accessToken = null;
  },

  getRefreshToken(): string | null {
    return sessionStorage.getItem(REFRESH_TOKEN_KEY);
  },

  setRefreshToken(token: string): void {
    sessionStorage.setItem(REFRESH_TOKEN_KEY, token);
  },

  clearRefreshToken(): void {
    sessionStorage.removeItem(REFRESH_TOKEN_KEY);
  },

  clear(): void {
    accessToken = null;
    sessionStorage.removeItem(REFRESH_TOKEN_KEY);
  },
};
