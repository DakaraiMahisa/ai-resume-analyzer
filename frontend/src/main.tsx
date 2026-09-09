import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { QueryClientProvider } from "@tanstack/react-query";

import { themeManager } from "@/core/theme/theme-manager";
import "./styles/theme.css";
import "./index.css";

import App from "./app/App";
import { queryClient } from "./core/query/query-client";
import { restoreAuthSession } from "@/core/auth/auth-session";
import { configureAuth } from "@/core/auth/configure-auth";

configureAuth();

void restoreAuthSession();

themeManager.initialize();

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <App />
    </QueryClientProvider>
  </StrictMode>,
);
