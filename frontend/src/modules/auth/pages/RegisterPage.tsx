import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { Link, useNavigate } from "react-router-dom";

import { Button, Card, Input, Label } from "@/components/ui";
import { useRegister } from "@/modules/auth";
import {
  registerSchema,
  type RegisterFormValues,
} from "@/modules/auth/schemas/register.schemas";

export function RegisterPage() {
  const navigate = useNavigate();

  const registerMutation = useRegister();

  const form = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      firstName: "",
      lastName: "",
      email: "",
      password: "",
    },
  });

  const onSubmit = (values: RegisterFormValues) => {
    registerMutation.mutate(values, {
      onSuccess: () => {
        navigate("/login", {
          replace: true,
          state: {
            registered: true,
          },
        });
      },
    });
  };

  return (
    <div className="mx-auto flex min-h-[calc(100vh-4rem)] w-full max-w-lg items-center px-6 py-12">
      <Card className="w-full p-6 sm:p-8">
        <div className="mb-8">
          <p className="mb-2 text-sm font-medium text-brand">
            AI Resume Analyzer
          </p>

          <h1 className="text-3xl font-semibold tracking-tight text-text-primary">
            Create your account
          </h1>

          <p className="mt-2 text-sm leading-6 text-text-secondary">
            Start analyzing your resume against the jobs you want.
          </p>
        </div>

        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-5">
          <div className="grid gap-5 sm:grid-cols-2">
            <div>
              <Label htmlFor="firstName" className="mb-2">
                First name
              </Label>

              <Input
                id="firstName"
                type="text"
                autoComplete="given-name"
                {...form.register("firstName")}
              />

              {form.formState.errors.firstName && (
                <p className="mt-1.5 text-sm text-danger">
                  {form.formState.errors.firstName.message}
                </p>
              )}
            </div>

            <div>
              <Label htmlFor="lastName" className="mb-2">
                Last name
              </Label>

              <Input
                id="lastName"
                type="text"
                autoComplete="family-name"
                {...form.register("lastName")}
              />

              {form.formState.errors.lastName && (
                <p className="mt-1.5 text-sm text-danger">
                  {form.formState.errors.lastName.message}
                </p>
              )}
            </div>
          </div>

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
              autoComplete="new-password"
              {...form.register("password")}
            />

            {form.formState.errors.password && (
              <p className="mt-1.5 text-sm text-danger">
                {form.formState.errors.password.message}
              </p>
            )}

            <p className="mt-1.5 text-xs text-text-tertiary">
              Use between 8 and 72 characters.
            </p>
          </div>

          {registerMutation.isError && (
            <p
              role="alert"
              className="rounded-md border border-danger/20 bg-danger/5 px-3 py-2.5 text-sm text-danger"
            >
              Unable to create your account. Please check your information and
              try again.
            </p>
          )}

          <Button
            type="submit"
            size="lg"
            loading={registerMutation.isPending}
            className="w-full"
          >
            Create account
          </Button>
        </form>

        <p className="mt-6 text-center text-sm text-text-secondary">
          Already have an account?{" "}
          <Link
            to="/login"
            className="font-medium text-brand hover:text-brand-hover"
          >
            Sign in
          </Link>
        </p>
      </Card>
    </div>
  );
}
