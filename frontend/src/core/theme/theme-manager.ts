import { themeStorage } from "./theme-storage";
import type { Theme } from "./theme-types";

const DARK_CLASS = "dark";

const getSystemTheme = (): Theme => {
  return window.matchMedia("(prefers-color-scheme: dark)").matches
    ? "dark"
    : "light";
};

export const themeManager = {
  getTheme(): Theme {
    return themeStorage.getTheme() ?? getSystemTheme();
  },

  applyTheme(theme: Theme): void {
    document.documentElement.classList.toggle(DARK_CLASS, theme === "dark");

    document.documentElement.style.colorScheme = theme;
  },

  initialize(): void {
    const theme = this.getTheme();

    this.applyTheme(theme);
  },

  setTheme(theme: Theme): void {
    themeStorage.setTheme(theme);
    this.applyTheme(theme);
  },

  toggle(): Theme {
    const nextTheme: Theme = this.getTheme() === "dark" ? "light" : "dark";

    this.setTheme(nextTheme);

    return nextTheme;
  },
};
