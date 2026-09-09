import type { Theme } from "./theme-types";

const THEME_STORAGE_KEY = "ai-resume-analyzer.theme";

export const themeStorage = {
  getTheme(): Theme | null {
    const storedTheme = localStorage.getItem(THEME_STORAGE_KEY);

    if (storedTheme === "light" || storedTheme === "dark") {
      return storedTheme;
    }

    return null;
  },

  setTheme(theme: Theme): void {
    localStorage.setItem(THEME_STORAGE_KEY, theme);
  },

  clearTheme(): void {
    localStorage.removeItem(THEME_STORAGE_KEY);
  },
};
