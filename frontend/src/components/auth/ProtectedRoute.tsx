import { Navigate, Outlet, useLocation } from "react-router-dom";

import { useAuthStore } from "@/core/auth/auth-store";

export function ProtectedRoute() {
  const isInitializing = useAuthStore((state) => state.isInitializing);
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const location = useLocation();

  if (isInitializing) {
    return (
      <main className="flex min-h-screen items-center justify-center bg-background">
        <p className="text-sm text-text-secondary">Restoring your session...</p>
      </main>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return <Outlet />;
}
