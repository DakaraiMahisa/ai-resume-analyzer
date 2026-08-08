# Development Guide

This document defines the development workflow for the AI Resume Analyzer project.

It is intended to be a practical reference for every developer working on the repository. It covers Git workflow, branch strategy, commits, Pull Requests, code review, testing, local infrastructure, environment configuration, backend and frontend development, database changes, security, AI-related development, and common troubleshooting.

---

# 1. Development Workflow

The normal development cycle is:

1. Update `develop`
2. Create a development branch
3. Implement the change
4. Test the change
5. Commit the change
6. Push the branch
7. Create a Pull Request
8. Review the Pull Request
9. Merge into `develop`
10. Delete the completed branch

The important principle is:

> Never develop directly on `main` or `develop`. Work on a dedicated branch and integrate the completed work through a Pull Request.

---

# 2. Branch Strategy

The repository uses two permanent branches:

```text
main
develop
```

Temporary development branches are created from `develop`.

```text
                    ┌──────────────┐
                    │     main     │
                    │    stable    │
                    └──────▲───────┘
                           │
                      Pull Request
                           │
                    ┌──────┴───────┐
                    │    develop   │
                    │  integration │
                    └──────▲───────┘
                           │
              ┌────────────┼────────────┐
              │            │            │
          feature/*      fix/*       chore/*
```

---

## 2.1 `main`

`main` represents the stable version of the application.

It should contain code that is production-ready or suitable for a release.

### Rules

- Direct pushes are not allowed.
- Changes must enter through a Pull Request.
- Required review/approval must be satisfied before merging.
- Do not use `main` for normal feature development.
- Do not experiment directly on `main`.

The branch should remain stable.

---

## 2.2 `develop`

`develop` is the primary integration branch for active development.

It represents the latest integrated state of the project.

### Rules

- Direct pushes are not allowed.
- Changes must enter through a Pull Request.
- Approval is not required by the current branch protection policy.
- Review is still encouraged for significant changes.
- Feature development happens on separate branches.
- Completed work is merged into `develop`.
- `develop` should remain buildable and reasonably stable.

Developers should normally create new branches from the latest `develop`.

---

# 3. Development Branches

Never implement a feature directly on `develop`.

Create a dedicated branch for each logical task.

Use the following naming convention:

```text
feature/<name>
fix/<name>
refactor/<name>
chore/<name>
docs/<name>
test/<name>
```

### Examples

```text
feature/resume-upload
feature/job-description-analysis
feature/ats-scoring

fix/resume-parser-error
fix/invalid-file-validation

refactor/ai-client

chore/update-dependencies
chore/docker-configuration

docs/update-setup-guide

test/resume-parser
```

Branch names should:

- be lowercase
- use hyphens
- describe the work
- avoid unnecessary words
- represent one logical task

---

# 4. Starting a New Task

Always start from the latest `develop`.

First check the current branch:

```powershell
git branch --show-current
```

Switch to `develop`:

```powershell
git switch develop
```

Pull the latest changes:

```powershell
git pull origin develop
```

Create a development branch:

```powershell
git switch -c feature/<feature-name>
```

Example:

```powershell
git switch -c feature/resume-upload
```

Verify:

```powershell
git branch --show-current
```

Expected:

```text
feature/resume-upload
```

---

# 5. Why We Use Development Branches

Development branches provide isolation.

For example:

```text
develop
   │
   ├── feature/resume-upload
   │
   ├── feature/ats-scoring
   │
   └── fix/parser-error
```

Developers can work independently without interfering with each other's unfinished work.

Once a task is complete, the branch is reviewed and merged into `develop`.

---

# 6. Working on a Branch

Before starting work:

```powershell
git status
```

Confirm the branch:

```powershell
git branch --show-current
```

During development, periodically check:

```powershell
git status
```

This helps identify:

- modified files
- untracked files
- accidental files
- generated files
- unexpected changes

Keep a development branch focused on one logical task.

Avoid combining unrelated work such as:

```text
feature/resume-upload

+ resume upload
+ authentication changes
+ Docker changes
+ UI redesign
+ unrelated bug fixes
```

Prefer separate branches when the work can be logically separated.

---

# 7. Keeping a Development Branch Updated

`develop` may receive changes while you are working.

Before opening a Pull Request, update your branch with the latest `develop`.

First update `develop`:

```powershell
git switch develop
git pull origin develop
```

Return to your development branch:

```powershell
git switch feature/<feature-name>
```

Merge the latest `develop`:

```powershell
git merge develop
```

Resolve conflicts if necessary.

Then run the relevant tests again.

Push the updated branch:

```powershell
git push
```

---

# 8. Merge vs Rebase

The default team workflow is to merge `develop` into your development branch when synchronization is required:

```powershell
git switch develop
git pull origin develop

git switch feature/<feature-name>
git merge develop
```

This keeps the workflow straightforward and avoids rewriting shared history.

Do not rebase shared branches unless the team explicitly agrees to do so.

---

# 9. Git Status Before Committing

Always inspect your changes before staging them:

```powershell
git status
```

Review the actual changes:

```powershell
git diff
```

Typical files that should not be committed include:

```text
.env
target/
node_modules/
dist/
.idea/
*.iml
```

These should normally be excluded through `.gitignore`.

Never commit:

- passwords
- API keys
- JWT secrets
- database credentials
- SMTP credentials
- cloud credentials
- private keys
- local machine configuration

---

# 10. Reviewing Changes

`git status` shows which files changed.

`git diff` shows what actually changed.

Always review the diff before committing significant work.

For staged changes:

```powershell
git diff --cached
```

A good pre-commit sequence is:

```powershell
git status
git diff
```

After staging:

```powershell
git status
git diff --cached
```

---

# 11. Staging Changes

Prefer explicitly staging files you intend to commit:

```powershell
git add <file>
```

or:

```powershell
git add <file1> <file2> <file3>
```

Then verify:

```powershell
git status
```

For larger, well-understood changes:

```powershell
git add .
```

may be used, but always inspect the staged result afterward.

---

# 12. Commit Convention

Use Conventional Commit-style messages.

Format:

```text
<type>: <description>
```

Common types:

| Type | Purpose |
|---|---|
| `feat` | New functionality |
| `fix` | Bug fix |
| `refactor` | Code restructuring |
| `test` | Tests |
| `docs` | Documentation |
| `chore` | Maintenance |
| `build` | Build system changes |
| `ci` | CI/CD changes |

Examples:

```text
feat: add resume upload
feat: implement job description parser
feat: add ATS scoring service

fix: handle unsupported resume format
fix: prevent duplicate resume processing

refactor: extract resume parsing service

test: add resume parser tests

docs: update development guide

chore: update dependencies

build: configure Maven compiler

ci: add backend build workflow
```

---

# 13. Good Commit Practices

A commit should represent one logical change.

Prefer:

```text
feat: add resume upload
fix: validate uploaded file type
test: add resume upload tests
```

over:

```text
implemented everything
```

Good commits make it easier to:

- review changes
- understand project history
- debug problems
- revert changes
- identify regressions

---

# 14. Push Your Development Branch

The first time you push a new branch:

```powershell
git push -u origin feature/<feature-name>
```

Example:

```powershell
git push -u origin feature/resume-upload
```

After the upstream branch has been configured:

```powershell
git push
```

---

# 15. Pull Requests

Development work enters `develop` through a Pull Request.

The normal flow is:

```text
feature/resume-upload
        │
        │ Push
        ▼
     GitHub
        │
        │ Pull Request
        ▼
     develop
```

Do not push directly to `develop`.

---

# 16. Creating a Pull Request

Before opening a Pull Request:

### 1. Confirm the branch

```powershell
git branch --show-current
```

### 2. Check status

```powershell
git status
```

### 3. Review changes

```powershell
git diff
```

### 4. Run the relevant checks

Backend:

```powershell
cd backend
.\mvnw.cmd clean compile
```

Backend tests:

```powershell
.\mvnw.cmd test
```

Frontend:

```powershell
cd frontend
npm run build
```

Infrastructure, if changed:

```powershell
docker compose config
```

### 5. Push

```powershell
git push
```

### 6. Create the Pull Request

Target:

```text
develop
```

---

# 17. Pull Request Description

A Pull Request should explain what changed and how it was verified.

Recommended structure:

```markdown
## Summary

- Added resume upload endpoint
- Added file validation
- Added resume storage service

## Why

Allows users to upload resumes for analysis.

## Testing

- Backend compilation successful
- Unit tests passed
- Tested valid PDF upload
- Tested unsupported file type

## Notes

- No database migration required
- No breaking API changes
```

The description should allow another developer to understand the change without reading every line of code first.

---

# 18. Code Review

Every Pull Request should be reviewed appropriately before merging.

The reviewer should consider:

### Correctness

- Does the implementation solve the intended problem?
- Are edge cases handled?
- Are failure cases handled?

### Architecture

- Does the code belong in the correct module?
- Are responsibilities separated correctly?
- Is unnecessary coupling introduced?

### Security

- Are credentials protected?
- Is user input validated?
- Are authorization rules correct?
- Is sensitive information exposed?

### Error Handling

- Are expected failures handled?
- Are meaningful error responses returned?
- Could an invalid state silently become a valid result?

### Testing

- Are important behaviors tested?
- Are edge cases covered?
- Do existing tests still pass?

### Maintainability

- Is the code readable?
- Are names meaningful?
- Is duplication avoided?
- Is unnecessary complexity introduced?

---

# 19. Pull Request Checklist

Before approving or merging a Pull Request:

```text
[ ] Correct target branch
[ ] Changes solve the intended problem
[ ] Code is understandable
[ ] Architecture is appropriate
[ ] Validation is present where required
[ ] Error handling is appropriate
[ ] Security implications considered
[ ] Tests added/updated where appropriate
[ ] Backend builds successfully
[ ] Frontend builds successfully when applicable
[ ] Infrastructure configuration is valid when applicable
[ ] No secrets committed
[ ] No generated files committed
[ ] No unnecessary dependencies added
[ ] Documentation updated when necessary
```

---

# 20. Merging Pull Requests

Once a Pull Request satisfies the repository rules and any required review, it can be merged into `develop`.

The integration flow is:

```text
feature/*
    ↓
Pull Request
    ↓
develop
```

After merging, the development branch is no longer the integration source.

---

# 21. After Your Pull Request Is Merged

Switch back to `develop`:

```powershell
git switch develop
```

Update it:

```powershell
git pull origin develop
```

Delete the local branch:

```powershell
git branch -d feature/resume-upload
```

If the remote branch was deleted through GitHub, no further action is required.

---

# 22. Starting the Next Task

After completing a task, always start the next task from an updated `develop`:

```powershell
git switch develop
git pull origin develop
git switch -c feature/<next-feature>
```

Do not create new work from an old feature branch.

---

# 23. Handling Merge Conflicts

Merge conflicts occur when Git cannot safely combine changes from two branches.

If your branch conflicts with `develop`:

```powershell
git switch develop
git pull origin develop
git switch feature/<feature-name>
git merge develop
```

Git will identify conflicting files.

Open each conflicting file and resolve the conflict.

After resolving:

```powershell
git status
```

Stage the resolved files:

```powershell
git add <resolved-file>
```

Complete the merge:

```powershell
git commit
```

Run the relevant tests again.

Then push:

```powershell
git push
```

---

# 24. Never Ignore Merge Conflicts

Do not blindly choose:

```text
Accept Current
```

or:

```text
Accept Incoming
```

without understanding the changes.

A merge conflict represents two potentially valid pieces of work that Git cannot safely combine automatically.

The developer must determine the correct final behavior.

---

# 25. Do Not Force Push Shared Branches

Avoid:

```powershell
git push --force
```

on:

```text
main
develop
```

or any branch being actively used by another developer.

Force pushing rewrites branch history and can cause other developers to lose commits.

If force pushing your own unpublished branch is ever necessary, understand why it is required before doing so.

---

# 26. Environment Variables

Environment-specific configuration belongs in `.env`.

Example:

```env
DB_NAME=ai_resume_analyzer
DB_USER=resume_app
DB_PASSWORD=your-local-password
DB_ROOT_PASSWORD=your-local-root-password

FRONTEND_URL=http://localhost:5173
```

The actual `.env` file must never be committed.

The repository contains:

```text
.env.example
```

This file documents the variables required by the application without containing real secrets.

---

# 27. Adding a New Environment Variable

When adding a new environment variable:

### Step 1

Add it to `.env.example`.

Example:

```env
AI_PROVIDER=
```

### Step 2

Use it from application configuration.

Example:

```yaml
ai:
  provider: ${AI_PROVIDER}
```

### Step 3

Add the real value to your local `.env`.

```env
AI_PROVIDER=provider-name
```

### Step 4

Do not commit `.env`.

### Step 5

Update `SETUP.md` if the variable is required for another developer to run the application.

---

# 28. Dependency Management

Do not add dependencies without a reason.

Before adding one:

1. Confirm the requirement.
2. Check whether the project already has a dependency that provides the functionality.
3. Consider security and maintenance.
4. Consider whether the dependency introduces unnecessary complexity.
5. Document significant dependency additions.

## Backend

Backend dependencies belong in:

```text
backend/pom.xml
```

After changing dependencies:

```powershell
cd backend
.\mvnw.cmd clean compile
```

## Frontend

Frontend dependencies are managed through npm.

Use:

```powershell
cd frontend
npm install <package>
```

This updates:

```text
package.json
package-lock.json
```

Do not manually modify `package-lock.json`.

---

# 29. Database Development

The project uses MySQL for persistence.

During the initial foundation stage, the database schema may not yet be implemented.

When database development begins, schema changes should be reproducible.

The project uses Flyway for database migrations.

A migration might look like:

```text
V1__create_users_table.sql
V2__create_resumes_table.sql
V3__add_resume_status.sql
```

Once Flyway is active:

> Database schema changes should be represented by migration files rather than undocumented manual changes.

---

# 30. Database Change Rules

When a feature requires a database change:

1. Create the appropriate Flyway migration.
2. Test the migration locally.
3. Verify the application starts correctly.
4. Include the migration in the same Pull Request as the feature.
5. Explain the database change in the Pull Request description.

Do not modify a previously applied migration to change an existing database schema.

Create a new migration instead.

---

# 31. API Development

When adding or modifying an API, consider:

```text
Request
   ↓
Validation
   ↓
Controller
   ↓
Service
   ↓
Repository / External Service
   ↓
Response
```

Consider the following before changing an endpoint:

- HTTP method
- URL structure
- request DTO
- response DTO
- validation
- HTTP status codes
- error responses
- authentication
- authorization
- existing frontend consumers
- backward compatibility

Avoid changing an existing API contract without considering its impact on the frontend and other consumers.

---

# 32. Error Handling

Errors should be handled intentionally.

Do not allow unexpected exceptions to become meaningless responses.

The backend should:

- validate incoming data
- reject invalid requests
- return appropriate HTTP status codes
- provide useful error information
- avoid exposing internal implementation details
- log unexpected failures appropriately

Never return sensitive information such as:

```text
database passwords
JWT secrets
API keys
stack traces
internal credentials
```

to the client.

---

# 33. Testing

Testing should be performed before opening a Pull Request.

## Backend compilation

From `backend/`:

```powershell
.\mvnw.cmd clean compile
```

## Backend tests

```powershell
.\mvnw.cmd test
```

## Frontend build

From `frontend/`:

```powershell
npm run build
```

## Docker configuration

If Docker configuration was modified:

```powershell
docker compose config
```

The appropriate level of testing depends on the change.

A documentation-only change does not require the same testing as a database or authentication change.

---

# 34. Local Infrastructure

Infrastructure services are managed through Docker Compose.

From the repository root:

```powershell
docker compose up -d
```

Check services:

```powershell
docker compose ps
```

View logs:

```powershell
docker compose logs
```

Stop services:

```powershell
docker compose down
```

Do not use `docker compose down -v` unless you intentionally want to delete local persistent volumes.

---

# 35. Docker Volumes

Normal shutdown:

```powershell
docker compose down
```

This removes containers but preserves persistent volumes.

Avoid:

```powershell
docker compose down -v
```

unless you intentionally want to delete local database and other persisted service data.

The distinction is:

```text
docker compose down
        ↓
containers removed
volumes preserved

docker compose down -v
        ↓
containers removed
volumes removed
local persistent data deleted
```

---

# 36. Generated Files

Do not commit generated artifacts such as:

```text
backend/target/
frontend/node_modules/
frontend/dist/
```

These are generated locally and should be recreated when required.

The repository should contain source code and configuration, not machine-specific build output.

---

# 37. IDE Configuration

Developers may use different IDEs.

The project should not depend on personal IDE configuration.

Do not commit personal:

```text
.idea/
*.iml
```

configuration unless the team explicitly decides otherwise.

---

# 38. IntelliJ IDEA Backend Setup

For IntelliJ IDEA:

1. Open the project.
2. Locate `backend/pom.xml`.
3. Import it as a Maven project.
4. Configure JDK 21.
5. Allow Maven to resolve dependencies.
6. Run the Spring Boot application.

The project uses Java 21.

Other JDK versions may remain installed on the developer's machine for other projects. Developers do not need to uninstall them.

Verify that Maven uses Java 21.

The Maven compiler output should indicate:

```text
release 21
```

---

# 39. Frontend Development

The frontend uses:

```text
React
TypeScript
Vite
```

From:

```text
frontend/
```

install dependencies:

```powershell
npm install
```

Start development:

```powershell
npm run dev
```

Create a production build:

```powershell
npm run build
```

---

# 40. Frontend and Backend URLs

Local development uses:

```text
Frontend:
http://localhost:5173

Backend:
http://localhost:8080
```

The backend configuration contains the frontend URL so security configuration can reference the correct frontend origin.

Do not hard-code production URLs into development configuration.

---

# 41. CORS

CORS configuration belongs to the backend security configuration.

When Spring Security is implemented, the configured frontend URL should be used as the allowed origin.

For local development:

```text
http://localhost:5173
```

Avoid allowing unrestricted origins such as:

```text
*
```

when credentials or authenticated requests are involved.

---

# 42. Security

Security-sensitive changes require additional care.

Examples include:

- authentication
- authorization
- JWT handling
- password management
- file uploads
- access control
- CORS
- CSRF
- secrets
- external AI provider credentials
- database credentials

Never commit credentials directly into:

```text
Java source files
YAML files
TypeScript files
Docker Compose files
Git history
```

Use environment variables or an appropriate secret-management mechanism.

---

# 43. File Uploads

The application processes resumes and other documents.

File upload functionality must consider:

- allowed file types
- file size
- filename handling
- malicious files
- MIME type validation
- storage location
- access control
- virus/malware scanning where required
- secure download behavior

Do not assume that a file extension alone proves the file type.

---

# 44. AI-Related Changes

AI functionality should not be implemented as an uncontrolled call from a controller.

Prefer a clear separation:

```text
Controller
    ↓
Application Service
    ↓
AI / Domain Service
    ↓
AI Provider
```

AI-related functionality should consider:

- prompt construction
- input validation
- structured outputs
- model failures
- timeouts
- retries
- token usage
- provider failures
- unsupported outputs
- hallucinations
- logging
- sensitive resume information

Do not expose provider credentials to the frontend.

---

# 45. Logging

Logs should help developers diagnose problems without exposing sensitive information.

Do not log:

```text
passwords
JWT tokens
API keys
database credentials
full sensitive resume contents
```

Use appropriate log levels:

```text
DEBUG
INFO
WARN
ERROR
```

Avoid excessive logging in production code.

---

# 46. Documentation Requirements

Update documentation when a change affects:

- setup
- environment variables
- architecture
- API behavior
- database configuration
- development workflow
- deployment
- external services

Relevant documentation includes:

```text
README.md
SETUP.md
DEVELOPMENT.md
```

Additional architecture documentation can be added as the project grows.

Do not document implementation details that are likely to become obsolete unless they provide long-term value.

---

# 47. Before Requesting Review

Use this checklist:

```text
[ ] I am on the correct branch
[ ] My branch is based on the latest develop
[ ] git status contains only expected changes
[ ] No secrets are committed
[ ] No generated files are committed
[ ] The code compiles
[ ] Tests pass where applicable
[ ] Frontend builds where applicable
[ ] Docker configuration validates if changed
[ ] Database migrations work if changed
[ ] API behavior is intentional
[ ] Documentation is updated if required
[ ] Commit messages are meaningful
[ ] Pull Request description explains the change
```

---

# 48. Recommended Daily Workflow

A typical development session:

## Start

```powershell
git switch develop
git pull origin develop
git switch -c feature/<feature-name>
```

## Develop

```powershell
git status
```

Make changes and test locally.

## Review

```powershell
git diff
git status
```

## Commit

```powershell
git add <files>
git commit -m "feat: description"
```

## Push

```powershell
git push -u origin feature/<feature-name>
```

## Pull Request

Create a Pull Request targeting:

```text
develop
```

## After Merge

```powershell
git switch develop
git pull origin develop
git branch -d feature/<feature-name>
```

---

# 49. Quick Git Reference

## Check current branch

```powershell
git branch --show-current
```

## Check status

```powershell
git status
```

## Update develop

```powershell
git switch develop
git pull origin develop
```

## Create branch

```powershell
git switch -c feature/<feature-name>
```

## Stage files

```powershell
git add <files>
```

## Review staged changes

```powershell
git diff --cached
```

## Commit

```powershell
git commit -m "feat: description"
```

## Push new branch

```powershell
git push -u origin feature/<feature-name>
```

## Push existing branch

```powershell
git push
```

## Update branch with develop

```powershell
git switch develop
git pull origin develop
git switch feature/<feature-name>
git merge develop
```

## Delete merged local branch

```powershell
git branch -d feature/<feature-name>
```

## View recent commits

```powershell
git log --oneline -10
```

---

# 50. Quick Docker Reference

## Start infrastructure

```powershell
docker compose up -d
```

## Check services

```powershell
docker compose ps
```

## View logs

```powershell
docker compose logs
```

## View MySQL logs

```powershell
docker compose logs mysql
```

## Stop infrastructure

```powershell
docker compose down
```

## Validate Compose configuration

```powershell
docker compose config
```

---

# 51. Quick Backend Reference

From `backend/`:

## Compile

```powershell
.\mvnw.cmd clean compile
```

## Run tests

```powershell
.\mvnw.cmd test
```

## Run the application

Using IntelliJ IDEA is recommended during development.

Alternatively:

```powershell
.\mvnw.cmd spring-boot:run
```

---

# 52. Quick Frontend Reference

From `frontend/`:

## Install dependencies

```powershell
npm install
```

## Start development server

```powershell
npm run dev
```

## Build

```powershell
npm run build
```

---

# 53. Golden Rules

Every developer should follow these rules:

1. Never push directly to `main`.
2. Never push directly to `develop`.
3. Create a branch for every logical task.
4. Always start new work from the latest `develop`.
5. Use Pull Requests to integrate work.
6. Review your own diff before committing.
7. Do not commit secrets.
8. Do not commit generated files.
9. Test your changes before opening a Pull Request.
10. Keep commits focused and understandable.
11. Do not rewrite shared Git history.
12. Keep `develop` stable.
13. Document configuration changes.
14. Treat security and database changes carefully.
15. Ask for review when a change affects shared architecture.

---

# 54. Development Philosophy

The project follows a simple principle:

> Make changes easy to understand, easy to review, and easy to reproduce.

A good contribution should allow another developer to answer:

```text
What changed?
Why did it change?
How does it work?
How was it tested?
What does it affect?
```

If those questions can be answered clearly, the project remains maintainable as the team and codebase grow.
