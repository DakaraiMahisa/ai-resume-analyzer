import { createBrowserRouter, Navigate } from "react-router-dom";

import { ProtectedRoute } from "@/components/auth/ProtectedRoute";
import { AppLayout } from "@/components/layout/AppLayout";
import { PublicLayout } from "@/components/layout/PublicLayout";
import { LoginPage } from "@/modules/auth/pages/LoginPage";

import { DashboardPage } from "@/modules/dashboard/pages/DashboardPage";
import { LandingPage } from "@/modules/landing/pages/LandingPage";
import { RegisterPage } from "@/modules/auth/pages/RegisterPage";
import { ResumesPage } from "@/modules/resume/pages/ResumesPage";

import { ResumeDetailPage } from "@/modules/resume/pages/ResumeDetailPage";
import { JobDescriptionDetailPage, JobDescriptionsPage } from "@/modules/job";
import {
  AnalyzePage,
  AnalysisResultPage,
  AnalysesPage,
} from "@/modules/analysis";
export const router = createBrowserRouter([
  {
    errorElement: (
      <main>
        <h1>Something went wrong</h1>
      </main>
    ),
    children: [
      {
        element: <PublicLayout />,
        children: [
          {
            path: "/",
            element: <LandingPage />,
          },
          {
            path: "/login",
            element: <LoginPage />,
          },
          {
            path: "/register",
            element: <RegisterPage />,
          },
        ],
      },

      {
        element: <ProtectedRoute />,
        children: [
          {
            path: "/app",
            element: <AppLayout />,
            children: [
              {
                index: true,
                element: <Navigate to="/app/dashboard" replace />,
              },
              {
                path: "dashboard",
                element: <DashboardPage />,
              },
              { path: "resumes", element: <ResumesPage /> },
              { path: "resumes/:id", element: <ResumeDetailPage /> },
              {
                path: "jobs",
                element: <JobDescriptionsPage />,
              },
              {
                path: "jobs/:id",
                element: <JobDescriptionDetailPage />,
              },
              {
                path: "analyze",
                element: <AnalyzePage />,
              },
              {
                path: "analyses",
                element: <AnalysesPage />,
              },
              {
                path: "analyses/:analysisId",
                element: <AnalysisResultPage />,
              },
              {
                path: "settings",
                element: <div>Settings</div>,
              },
            ],
          },
        ],
      },

      {
        path: "*",
        element: <Navigate to="/" replace />,
      },
    ],
  },
]);
