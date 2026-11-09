# Containerised Deployment Guide

## 1. Overview

The repository deploys the AI Resume Analyzer as a small Docker Compose
environment:

- **MySQL** stores application data and runs as the `mysql:8.4` container.
- **Backend** runs the Spring Boot executable JAR on port `8080`. It connects
  to MySQL by the Docker service name `mysql`, runs Flyway migrations, exposes
  the REST API, and provides health and OpenAPI endpoints.
- **Frontend** is built with Node.js and served as static files by Nginx on
  container port `80`.
- **Nginx** serves the React application, supports client-side routes, and
  proxies browser requests under `/api/` to the backend container.

The Compose files provide containerised local or server deployment. Production
deployment is automated separately by `.github/workflows/ci.yml`: successful
`main` pushes publish the backend image to GHCR, update Azure Container Apps,
and deploy the tested frontend artifact to Azure Static Web Apps.

## 2. Prerequisites

Install and verify the following on the deployment machine:

- Docker Desktop with a running Docker Engine and Docker Compose support.
- Git and access to the repository.
- Network access to pull the `mysql:8.4`, Maven/Java build, Node.js, and Nginx
  base images during the first build.
- Credentials and network access for any enabled external provider, such as
  Gemini or Backblaze B2.

On Windows, run the examples in this guide from PowerShell. Confirm Docker is
available before continuing:

```powershell
docker --version
docker compose version
docker info
```

`docker info` must succeed; a Docker CLI installation without a running
Docker Desktop daemon is not sufficient.

## 3. Architecture and networking

The deployment uses two Compose files:

| File | Responsibility |
|---|---|
| `docker-compose.yml` | Starts MySQL, publishes it to host port `3307`, defines the `mysql_data` named volume, and defines the MySQL healthcheck. |
| `docker-compose.app.yml` | Builds and starts the backend and frontend images, publishes backend port `8080` and frontend port `3000`, and connects both application services to the external network `ai-resume-analyzer_default`. |

The backend image is defined by `backend/Dockerfile`, and the frontend image
is defined by `frontend/Dockerfile`. The frontend image copies
`frontend/nginx.conf` into Nginx as its default server configuration. The
corresponding `.dockerignore` files are `backend/.dockerignore` and
`frontend/.dockerignore`; they exclude build output, local dependencies,
environment files, and logs from their respective build contexts.

Run the MySQL Compose project from the repository root first. Docker Compose
creates its default project network, which must be named
`ai-resume-analyzer_default` because the application Compose file declares
that exact external network. With the repository directory/project name
`ai-resume-analyzer`, the normal command is sufficient:

```powershell
docker compose up -d mysql
```

If the repository was cloned into a directory with a different name, the
default network name may differ. In that case, use the Compose project name
explicitly for the MySQL project:

```powershell
docker compose -p ai-resume-analyzer up -d mysql
```

Do not create a second application network with a different name. The
backend and frontend in `docker-compose.app.yml` resolve each other by Docker
service/container names on `ai-resume-analyzer_default`. Nginx uses
`http://ai-resume-analyzer-backend:8080` for `/api/` requests. The browser
uses the frontend at `http://localhost:3000`; `localhost` in the browser is
not the backend container, so API requests must go through the Nginx proxy.

The backend is also published directly at `http://localhost:8080` for
diagnostics and direct API access. Internal backend-to-MySQL traffic uses
`mysql:3306`, while host tools use the published MySQL port
`localhost:3307`.

## 4. Environment configuration

Create the root `.env` file from the checked-in template:

```powershell
Copy-Item .env.example .env
notepad .env
```

Never commit `.env`. The repository `.gitignore` excludes `.env` and other
environment files while retaining `.env.example`.

### Database variables

The variables present in `.env.example` are:

| Variable | Purpose in the Compose deployment |
|---|---|
| `DB_NAME` | MySQL database name and the database selected by the backend container. |
| `DB_USER` | Application MySQL user and the MySQL healthcheck user. |
| `DB_PASSWORD` | Application MySQL password and healthcheck password. |
| `DB_ROOT_PASSWORD` | MySQL root password used only to initialise the MySQL container. |
| `DB_URL` | Host-oriented JDBC URL in the template. The application Compose file overrides this inside the backend container with `jdbc:mysql://mysql:3306/${DB_NAME}`. |

The values in `.env.example` are placeholders. Replace them with non-default
values appropriate for the environment. The backend must use the same
`DB_NAME`, `DB_USER`, and `DB_PASSWORD` values that MySQL receives.

### Backend and provider variables

The backend configuration in
`backend/src/main/resources/application.yaml` also reads the following:

| Variable | Purpose |
|---|---|
| `JWT_SECRET` | Secret used to sign and validate JWTs. Use a strong, unique secret. |
| `GEMINI_API_KEY` | API key for the configured Google Gemini chat and embedding providers. |
| `GEMINI_CHAT_MODEL` | Optional Gemini chat model; the application supplies a default when omitted. |
| `GEMINI_EMBEDDING_MODEL` | Optional Gemini embedding model; the application supplies a default when omitted. |
| `STORAGE_PROVIDER` | Selects the configured document storage provider; the application default is `local`. |
| `DOCUMENT_STORAGE_DIRECTORY` | Directory used by the local document storage provider; the application default is `./storage/documents`. |
| `B2_ENDPOINT` | Backblaze B2/S3-compatible endpoint when B2 storage is selected. |
| `B2_REGION` | B2 region when B2 storage is selected. |
| `B2_BUCKET` | B2 bucket when B2 storage is selected. |
| `B2_ACCESS_KEY` | B2 access key when B2 storage is selected. |
| `B2_SECRET_KEY` | B2 secret key when B2 storage is selected. |
| `EMBEDDING_PROVIDER` | Selects the embedding provider; the application default is `ollama`. |
| `OLLAMA_EMBEDDING_BASE_URL` | Optional Ollama embedding service URL when Ollama embedding is selected. |
| `OLLAMA_EMBEDDING_MODEL` | Optional Ollama embedding model. |
| `FRONTEND_URL` | Backend frontend-origin configuration. The application default is `http://localhost:5173`; use `http://localhost:3000` for the containerised frontend origin. |
| `ANALYSIS_DAILY_LIMIT` | Optional daily analysis quota; the application default is `10`. |

The checked-in `.env.example` now mirrors the variable mapping used by the
current `.env`, including JWT, Gemini, storage, Backblaze B2, frontend-origin,
and embedding-provider settings. Copy it to the untracked `.env` and replace
every placeholder with an environment-specific value before deployment.
B2 credentials are required when `STORAGE_PROVIDER` selects B2; they are not
required for the default local storage provider. Likewise, Ollama must be
reachable if `EMBEDDING_PROVIDER=ollama` is selected. Optional application
settings that are not present in the current `.env` continue to use the
defaults defined in `application.yaml`.

The application Compose file sets `SPRING_PROFILES_ACTIVE=local` and overrides
`DB_URL` for container-to-container DNS. It passes the remaining `.env`
variables to the backend using `env_file`. The frontend image is built without
a Compose `build.args` or frontend `env_file`; its API client uses
`VITE_API_BASE_URL`, but the checked-in Compose configuration does not provide
that build-time variable. For this deployment, API paths are expected to be
relative and Nginx proxies `/api/` to the backend. If the frontend build
requires an explicit `VITE_API_BASE_URL`, that setting is not defined by the
repository configuration and requires manual confirmation before changing the
Compose setup.

For local development outside the containers, the backend CORS configuration
allows `http://localhost:5173` and `http://localhost:3000`. The containerised
frontend is reached at port `3000`, so set `FRONTEND_URL=http://localhost:3000`
for a consistent Docker deployment origin.

## 5. Build and deployment procedure

### 5.1 Clone and configure

```powershell
git clone <repository-url>
Set-Location ai-resume-analyzer
Copy-Item .env.example .env
notepad .env
```

Populate `.env` with the environment-specific database values, a strong
`JWT_SECRET`, `GEMINI_API_KEY`, and any conditional B2/Ollama settings. Do not
paste secrets into this guide or commit them.

### 5.2 Start MySQL infrastructure

From the repository root:

```powershell
docker compose up -d mysql
```

This creates or starts the MySQL container, attaches the `mysql_data` named
volume, publishes host port `3307`, and creates the network expected by the
application Compose file. Check its state and health:

```powershell
docker compose ps mysql
docker inspect --format '{{json .State.Health}}' ai-resume-analyzer-mysql
```

Wait until the health status reports `healthy`. The healthcheck uses the
configured application database user and password. If the container was
initialised previously, changing `MYSQL_*` values in `.env` does not rewrite
existing MySQL users or data; see the database section before changing them.

### 5.3 Build and start the application

```powershell
docker compose -f docker-compose.app.yml up -d --build
```

This builds the backend multi-stage image with Maven and Java 21, builds the
frontend with Node.js, packages the frontend into Nginx, and starts both
application containers. The backend image build uses `mvn -B clean package
-DskipTests`; run the backend verification commands outside the image or in
CI before release because the image build intentionally skips tests.

Verify containers and published ports:

```powershell
docker compose -f docker-compose.app.yml ps
docker ps --filter "name=ai-resume-analyzer"
```

The expected application endpoints are:

- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080`

### 5.4 GitHub Actions CI/CD deployment

The workflow in `.github/workflows/ci.yml` runs on GitHub-hosted Ubuntu
runners. It is triggered by pushes to `main`, `develop`, and
`feature/**`, and by pull requests targeting `main` or `develop`.

Every workflow run performs these checks:

1. **Backend Tests** checks out the repository, sets up Temurin Java 21 with
   Maven caching, and runs `./mvnw --batch-mode --no-transfer-progress clean
   verify` from `backend`.
2. **Frontend Checks** sets up Node.js 22 with npm caching, runs `npm ci`,
   ESLint, and the production build from `frontend`. The build receives
   `VITE_API_BASE_URL` set to the deployed Azure Container Apps backend URL.
   The resulting `frontend/dist` is uploaded as the short-lived
   `frontend-dist` artifact.
3. **Security Scan & Docker Build** waits for both checks, scans the
   repository filesystem with Trivy for HIGH and CRITICAL vulnerabilities,
   builds both backend and frontend images, scans both images, generates
   CycloneDX SBOMs, and uploads them as the `container-sboms` artifact.

The Docker job logs in to GHCR and publishes **only the backend image** on a
push to `main`. The published tag is the commit SHA:

```text
ghcr.io/dakaraimahisa/ai-resume-analyzer-backend:<commit-sha>
```

The frontend is deployed from the tested `frontend-dist` artifact rather than
from a published frontend container image.

The **Deploy to Azure Production** job runs only after the backend checks,
frontend checks, and Docker/security job succeed, and only for a push to
`main`. It:

1. Authenticates to Azure with OIDC using the `AZURE_CLIENT_ID`,
   `AZURE_TENANT_ID`, and `AZURE_SUBSCRIPTION_ID` repository secrets.
2. Installs or upgrades the Azure Container Apps CLI extension.
3. Updates Container App `aira-backend` in resource group
   `rg-ai-resume-analyzer-prod` to the backend image tagged with the current
   commit SHA.
4. Verifies the backend at
   `https://aira-backend.proudground-834143ea.southeastasia.azurecontainerapps.io/actuator/health`.
5. Downloads the `frontend-dist` artifact and deploys `frontend/dist` to Azure
   Static Web Apps using the `AZURE_STATIC_WEB_APPS_API_TOKEN` secret.
6. Verifies the frontend at
   `https://lively-sand-0cb751f00.8.azurestaticapps.net/`.

The workflow requires these repository secrets for production deployment:

| Secret | Used by |
|---|---|
| `AZURE_CLIENT_ID` | Azure OIDC login |
| `AZURE_TENANT_ID` | Azure OIDC login |
| `AZURE_SUBSCRIPTION_ID` | Azure OIDC login |
| `AZURE_STATIC_WEB_APPS_API_TOKEN` | Static Web Apps deployment |

The workflow grants `id-token: write` only to the deploy job and
`packages: write` to the Docker job. Pull requests and non-`main` pushes run
validation and security checks but do not publish images or deploy production.

## 6. Application verification

Check the backend health endpoint:

```powershell
Invoke-WebRequest http://localhost:8080/actuator/health
```

The configured endpoint is `http://localhost:8080/actuator/health`; health
details are not exposed by the application configuration. The backend also
exposes the Springdoc Swagger UI at:

```text
http://localhost:8080/swagger-ui.html
```

The security path configuration permits Swagger UI and OpenAPI paths. If a
browser follows the UI redirect, `/swagger-ui/index.html` is the corresponding
static UI path. The OpenAPI document is available at
`http://localhost:8080/v3/api-docs`.

Open `http://localhost:3000` and verify that the application loads. Complete a
functional smoke test using a non-production account:

1. Register and log in.
2. Upload a small supported resume file within the configured backend
   multipart limits (5 MB file and 6 MB request).
3. Confirm the resume appears in the application and start processing.
4. Add or select a job description.
5. Start an analysis and verify that the status and result or an explicit
   provider error are surfaced.

The upload and analysis steps require the configured storage and AI/embedding
providers to be reachable. A successful container start alone does not prove
that external Gemini, B2, or Ollama operations work.

Inspect logs and status when verification fails:

```powershell
docker compose -f docker-compose.app.yml logs --tail 200 backend
docker compose -f docker-compose.app.yml logs --tail 200 frontend
docker compose logs --tail 200 mysql
docker compose -f docker-compose.app.yml ps
docker compose ps mysql
```

## 7. Routine application updates

Application images can be rebuilt and restarted without recreating MySQL:

```powershell
docker compose -f docker-compose.app.yml build backend frontend
docker compose -f docker-compose.app.yml up -d backend frontend
docker compose -f docker-compose.app.yml ps
```

The normal one-command update is:

```powershell
docker compose -f docker-compose.app.yml up -d --build
```

Stop only application services when needed:

```powershell
docker compose -f docker-compose.app.yml stop backend frontend
```

Start them again without rebuilding:

```powershell
docker compose -f docker-compose.app.yml start backend frontend
```

Remove application containers and recreate them without touching MySQL:

```powershell
docker compose -f docker-compose.app.yml down
docker compose -f docker-compose.app.yml up -d --build
```

Routine application deployment should not require stopping, recreating, or
removing the database container or its volume. Flyway migrations run during
backend startup; review migration changes and take a verified backup before
deploying a version that changes the schema.

## 8. Database persistence and volume protection

`docker-compose.yml` declares the named volume `mysql_data` and mounts it at
`/var/lib/mysql` inside `ai-resume-analyzer-mysql`. This volume is where the
MySQL data files persist across container replacement. Docker Compose may show
the runtime name with a project prefix, typically
`ai-resume-analyzer_mysql_data`.

Removing a container does not necessarily remove a named volume. Therefore,
stopping the service or running `docker compose down` without `-v` preserves
the database data. **Never use the following as a routine troubleshooting
shortcut:**

```powershell
docker compose down -v
docker volume rm <mysql-volume-name>
```

Those operations can permanently remove the database volume and all data
stored in it. Also do not change the declared volume name or replace the
volume mapping casually. A new volume name, a different Compose project name,
or a changed mount can make MySQL initialise an empty database, which can
look like data loss even though the original volume still exists.

Inspect the volume without deleting it:

```powershell
docker volume ls
docker volume inspect ai-resume-analyzer_mysql_data
```

The exact prefixed volume name depends on the Compose project name. Confirm
the actual name with `docker volume ls` before backup or maintenance.

For a logical backup, use the running MySQL container and redirect the dump to
a protected file. The following uses the values already loaded in the
PowerShell session; it does not place real credentials in this document:

```powershell
$env:DB_NAME = "<database-name>"
$env:DB_USER = "<database-user>"
$env:DB_PASSWORD = "<database-password>"
docker exec ai-resume-analyzer-mysql mysqldump `
  --single-transaction --routines --triggers `
  -u"$env:DB_USER" "-p$env:DB_PASSWORD" "$env:DB_NAME" |
  Set-Content -Encoding utf8 "backup-$((Get-Date).ToString('yyyyMMdd-HHmmss')).sql"
```

Protect the backup file as sensitive data, copy it to a separate durable
location, and verify that it can be read before relying on it. A restoration
should be a planned maintenance operation against the intended database and
should first preserve a current backup:

```powershell
Get-Content .\backup.sql -Raw |
  docker exec -i ai-resume-analyzer-mysql mysql `
  -u"$env:DB_USER" "-p$env:DB_PASSWORD" "$env:DB_NAME"
```

Confirm the target container, database, credentials, and backup contents
before restoring. Do not restore over a live database as an ad-hoc fix.

## 9. Troubleshooting

### MySQL connection or healthcheck failures

Inspect the container, health state, and logs:

```powershell
docker compose ps mysql
docker inspect --format '{{json .State.Health}}' ai-resume-analyzer-mysql
docker compose logs --tail 200 mysql
```

Confirm Docker is running, `DB_NAME`, `DB_USER`, and `DB_PASSWORD` are
consistent, and host port `3307` is free. The backend must use
`jdbc:mysql://mysql:3306/<database>` inside Docker; it must not use
`localhost:3307` from the backend container. If credentials were changed
after the volume was initialised, do not delete the volume; use a planned
database credential-rotation procedure.

### Docker network problems

The app Compose file requires the external network
`ai-resume-analyzer_default`. Check it and the container attachments:

```powershell
docker network inspect ai-resume-analyzer_default
docker inspect ai-resume-analyzer-mysql --format '{{json .NetworkSettings.Networks}}'
docker inspect ai-resume-analyzer-backend --format '{{json .NetworkSettings.Networks}}'
```

If the network is missing, start MySQL from the repository root with the
expected Compose project name, then start the app stack again. Do not solve
this by changing the network name in the Compose files.

### Port conflicts

The configured host ports are `3307` for MySQL, `8080` for the backend, and
`3000` for the frontend. Find a process using a port:

```powershell
Get-NetTCPConnection -LocalPort 3000,3307,8080 -ErrorAction SilentlyContinue
```

Stop the conflicting process or use an approved environment-specific port
mapping change. If the host mapping is changed, update the URL used for
verification; container-to-container ports remain `3306`, `8080`, and `80`.

### Backend startup failures

Read the backend log:

```powershell
docker compose -f docker-compose.app.yml logs --tail 300 backend
```

Common causes are missing `JWT_SECRET` or `GEMINI_API_KEY`, invalid database
settings, an unhealthy or incorrectly networked MySQL container, a Flyway
migration failure, or unavailable required external providers. Validate the
effective non-secret variable names in `.env`, confirm the backend is using
the Compose-overridden `DB_URL`, and inspect migration errors before making
any database change.

### Frontend or API proxy failures

Inspect frontend and backend logs and confirm both containers are on the
same external network:

```powershell
docker compose -f docker-compose.app.yml logs --tail 200 frontend backend
docker compose -f docker-compose.app.yml ps
```

The Nginx proxy target is the fixed container name
`ai-resume-analyzer-backend:8080`. A browser request to
`http://localhost:3000/api/...` must be handled by Nginx; do not expect
`localhost` inside Nginx to refer to the host backend. If the frontend loads
but API calls fail, check network attachment, backend health, and that the
frontend build is using relative `/api/` paths as expected by the checked-in
Nginx configuration.

### Environment-variable configuration errors

Check for missing names and malformed values without printing secret values:

```powershell
Get-Content .env | ForEach-Object {
  if ($_ -match '^\s*([A-Za-z_][A-Za-z0-9_]*)\s*=') { $Matches[1] }
}
```

Compare the names with `.env.example` and
`backend/src/main/resources/application.yaml`. Remember that `.env.example`
does not list all required backend variables. Never post the complete `.env`
file in an issue or log.

### Gemini or Backblaze B2 connectivity

Gemini calls require a valid `GEMINI_API_KEY` and the configured model values.
B2 requires `STORAGE_PROVIDER` to select B2 and valid endpoint, region, bucket,
access-key, and secret-key variables. Check the backend logs for the
provider's error without enabling secret or request-payload logging. Verify
DNS, outbound network access, account permissions, bucket configuration, and
provider quotas. Do not substitute production credentials into a diagnostic
environment.

## 10. Operational security and best practices

- Keep `.env`, database dumps, and provider credentials out of Git. The
  repository already ignores `.env` and environment-specific files.
- Never expose `JWT_SECRET`, API keys, passwords, or signed tokens in logs,
  screenshots, issues, or documentation.
- The backend runtime image creates and uses the non-root `app` user. Preserve
  this configuration when changing the Dockerfile.
- Keep independent, tested database backups and protect them like production
  data.
- Review Flyway migrations before deployment, back up before schema changes,
  and monitor backend startup logs for migration failures.
- Keep the MySQL volume mapping and Compose project/network names stable.
- Use non-production accounts and data for functional upload and analysis
  verification.

## 11. Deployment checklist

- [ ] Docker Desktop is running and `docker info` succeeds.
- [ ] Repository is cloned and commands are being run from its root.
- [ ] `.env` was created from `.env.example` and is not committed.
- [ ] Database values, `JWT_SECRET`, `GEMINI_API_KEY`, and conditional
      provider settings are configured.
- [ ] MySQL is running and healthy on `ai-resume-analyzer_default`.
- [ ] The existing `mysql_data` volume is attached; no `down -v` or volume
      removal was used.
- [ ] `docker compose -f docker-compose.app.yml up -d --build` completed.
- [ ] Backend, frontend, and MySQL containers are running.
- [ ] `http://localhost:8080/actuator/health` reports a healthy response.
- [ ] `http://localhost:3000` loads the frontend.
- [ ] Swagger UI is reachable at `http://localhost:8080/swagger-ui.html`.
- [ ] A non-production account can upload a resume and start processing.
- [ ] A non-production account can start an analysis, or a provider failure
      is clearly reported and investigated.
- [ ] Logs and a recent database backup are available for the deployment.
- [ ] For production, the `main` workflow has passed backend, frontend, Docker,
      Trivy, and SBOM checks.
- [ ] Production deployment secrets are configured:
      `AZURE_CLIENT_ID`, `AZURE_TENANT_ID`, `AZURE_SUBSCRIPTION_ID`, and
      `AZURE_STATIC_WEB_APPS_API_TOKEN`.
- [ ] The deployed Azure Container Apps health URL returns success.
- [ ] The deployed Azure Static Web Apps URL loads successfully.

## Configuration requiring manual confirmation

The environment template now contains the same variable names as the current
`.env`, but its values are intentionally placeholders or non-secret defaults.
Before deployment, confirm the target database name and credentials, generate a
strong unique `JWT_SECRET`, provide a valid `GEMINI_API_KEY`, and verify the
selected storage and embedding providers. If B2 is selected, confirm the B2
endpoint, region, bucket, access key, and secret key.

The checked-in Compose file does not pass a `VITE_API_BASE_URL` build argument
or frontend environment file. The intended containerised routing remains the
relative `/api/` path through Nginx; confirm this behavior if the frontend API
configuration changes.

The GitHub Actions frontend build uses the deployed Container Apps URL through
`VITE_API_BASE_URL`. If that backend hostname, Azure Container App name,
resource group, Static Web Apps deployment target, or repository secret names
change, update `.github/workflows/ci.yml` and this guide together.
