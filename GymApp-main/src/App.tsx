import React, { useEffect, useState } from "react";
import { BrowserRouter, Routes, Route, Navigate, useNavigate, useLocation } from "react-router-dom";
import { supabase } from "./lib/supabase";
import { Toaster } from "sonner";

// Layouts
import AppLayout from "./components/layout/AppLayout";
import AuthLayout from "./features/auth/AuthLayout";

// Pages
import LandingPage from "./features/landing/LandingPage";
import LoginPage from "./features/auth/LoginPage";
import SignupPage from "./features/auth/SignupPage";
import DashboardPage from "./features/dashboard/DashboardPage";
import ProgramsPage from "./features/programs/ProgramsPage";
import NewProgramPage from "./features/programs/NewProgramPage";
import ProgramDetailsPage from "./features/programs/ProgramDetailsPage";
import ProgramDayDetailsPage from "./features/programs/ProgramDayDetailsPage";
import SessionsPage from "./features/sessions/SessionsPage";
import NewSessionPage from "./features/sessions/NewSessionPage";
import ExercisesPage from "./features/exercises/ExercisesPage";
import GymsPage from "./features/gyms/GymsPage";
import AnalyticsPage from "./features/analytics/AnalyticsPage";
import SettingsPage from "./features/settings/SettingsPage";

function AuthGuard({ children }: { children: React.ReactNode }) {
  const [session, setSession] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    supabase.auth.getSession().then(({ data: { session } }) => {
      setSession(session);
      setLoading(false);
    });

    const { data: { subscription } } = supabase.auth.onAuthStateChange((_event, session) => {
      setSession(session);
      setLoading(false);
    });

    return () => subscription.unsubscribe();
  }, []);

  if (loading) {
    return (
      <div className="flex h-screen w-screen items-center justify-center bg-background">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
      </div>
    );
  }

  if (!session) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <>{children}</>;
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public Routes */}
        <Route path="/" element={<LandingPage />} />
        
        <Route element={<AuthLayout />}>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
        </Route>

        {/* Protected App Routes */}
        <Route
          path="/app"
          element={
            <AuthGuard>
              <AppLayout />
            </AuthGuard>
          }
        >
          <Route index element={<Navigate to="/app/dashboard" replace />} />
          <Route path="dashboard" element={<DashboardPage />} />
          
          {/* Programs Routes */}
          <Route path="programs">
            <Route index element={<ProgramsPage />} />
            <Route path="new" element={<NewProgramPage />} />
            <Route path=":id" element={<ProgramDetailsPage />} />
            <Route path=":id/days/:dayId" element={<ProgramDayDetailsPage />} />
          </Route>

          <Route path="sessions">
            <Route index element={<SessionsPage />} />
            <Route path="new" element={<NewSessionPage />} />
          </Route>
          <Route path="exercises" element={<ExercisesPage />} />
          <Route path="gyms" element={<GymsPage />} />
          <Route path="analytics" element={<AnalyticsPage />} />
          <Route path="settings" element={<SettingsPage />} />
        </Route>

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
      <Toaster position="top-right" />
    </BrowserRouter>
  );
}
