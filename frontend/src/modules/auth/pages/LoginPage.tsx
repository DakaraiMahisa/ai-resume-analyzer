import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { Link, useLocation, useNavigate } from "react-router-dom";

import { Button, Card, Input, Label } from "@/components/ui";
import { useLogin } from "@/modules/auth";
import {
  loginSchema,
  type LoginFormValues,
} from "@/modules/auth/schemas/auth.schemas";

export function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();

  const loginMutation = useLogin();

  const form = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: "",
      password: "",
    },
  });

  const onSubmit = (values: LoginFormValues) => {
    loginMutation.mutate(values, {
      onSuccess: () => {
        const from = location.state?.from?.pathname ?? "/app/dashboard";

        navigate(from, { replace: true });
      },
    });
  };

  return (
    <div className="mx-auto flex min-h-[calc(100vh-4rem)] w-full max-w-md items-center px-6 py-12">
      <Card className="w-full p-6 sm:p-8">
        <div className="mb-8">
          <p className="mb-2 text-sm font-medium text-brand">
            AI Resume Analyzer
          </p>

          <h1 className="text-3xl font-semibold tracking-tight text-text-primary">
            Welcome back
          </h1>

          <p className="mt-2 text-sm leading-6 text-text-secondary">
            Sign in to continue analyzing your resumes.
          </p>
        </div>

        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-5">
          <div>
            <Label htmlFor="email" className="mb-2">
              Email
            </Label>

            <Input
              id="email"
              type="email"
              autoComplete="email"
              {...form.register("email")}
            />

            {form.formState.errors.email && (
              <p className="mt-1.5 text-sm text-danger">
                {form.formState.errors.email.message}
              </p>
            )}
          </div>

          <div>
            <Label htmlFor="password" className="mb-2">
              Password
            </Label>

            <Input
              id="password"
              type="password"
              autoComplete="current-password"
              {...form.register("password")}
            />

            {form.formState.errors.password && (
              <p className="mt-1.5 text-sm text-danger">
                {form.formState.errors.password.message}
              </p>
            )}
          </div>

          {loginMutation.isError && (
            <p
              role="alert"
              className="rounded-md border border-danger/20 bg-danger/5 px-3 py-2.5 text-sm text-danger"
            >
              Unable to sign in. Please check your credentials and try again.
            </p>
          )}

          <Button
            type="submit"
            size="lg"
            loading={loginMutation.isPending}
            className="w-full"
          >
            Sign in
          </Button>
        </form>

        <p className="mt-6 text-center text-sm text-text-secondary">
          Don't have an account?{" "}
          <Link
            to="/register"
            className="font-medium text-brand hover:text-brand-hover"
          >
            Create one
          </Link>
        </p>
      </Card>
    </div>
  );
}
