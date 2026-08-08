# Setup Guide

This document explains how to set up the AI Resume Analyzer development environment from a clean machine.

Follow the steps in order. The goal is to make it possible for a new team member to clone the repository, configure the required environment variables, start the infrastructure, start the backend and frontend, and verify that the complete local foundation is working.

---

# 1. Project Overview

AI Resume Analyzer is a full-stack application for analyzing resumes against job descriptions and producing structured insights.

The repository is organized as:

```text
ai-resume-analyzer/
├── backend/
├── frontend/
├── infrastructure/
├── docker-compose.yml
├── .env
├── .env.example
├── .gitignore
├── README.md
├── SETUP.md
└── DEVELOPMENT.md
```

The major components are:

```text
Frontend
React + TypeScript + Vite

Backend
Java + Spring Boot + Maven

Database
MySQL

Infrastructure
Docker Compose
```

---

# 2. Prerequisites

Install the following software before starting.

## 2.1 Git

Git is required to clone the repository and work with branches.

Verify:

```powershell
git --version
```

---

## 2.2 Java

The backend is built for **Java 21**.

Verify:

```powershell
java -version
```

The active Java version should be Java 21.

Example:

```text
java version "21.x.x"
```

You may have other JDK versions installed for other projects. They do not need to be removed.

The important requirement is that this project uses Java 21.

### Check Java installations on Windows

```powershell
where.exe java
```

Check:

```powershell
$env:JAVA_HOME
```

If `JAVA_HOME` is not configured, IntelliJ IDEA and Maven can still be configured to use the correct JDK, but configuring `JAVA_HOME` to Java 21 is recommended for consistency.

---

# 3. Maven

The project uses the Maven Wrapper.

You normally do **not** need to install Maven globally.

The backend contains:

```text
backend/
├── mvnw
├── mvnw.cmd
└── pom.xml
```

On Windows, use:

```powershell
.\mvnw.cmd
```

Using the Maven Wrapper ensures that developers use the Maven version expected by the project.

Verify the project:

```powershell
cd backend
.\mvnw.cmd -version
```

The Maven output should indicate that Java 21 is being used.

---

# 4. Node.js and npm

The frontend uses React, TypeScript, Vite, and npm.

Verify Node.js:

```powershell
node --version
```

Verify npm:

```powershell
npm --version
```

Use a current LTS version of Node.js unless the project specifies a different version.

---

# 5. Docker Desktop

Docker Desktop is required for local infrastructure.

Verify Docker:

```powershell
docker --version
```

Verify Docker Compose:

```powershell
docker compose version
```

Both commands should return valid version information.

Make sure Docker Desktop is running before starting the infrastructure.

---

# 6. Clone the Repository

Clone the repository:

```powershell
git clone <repository-url>
```

Move into the project:

```powershell
cd ai-resume-analyzer
```

Verify:

```powershell
git status
```

You should see the repository status.

---

# 7. Check the Project Structure

From the repository root:

```powershell
Get-ChildItem
```

The project should contain:

```text
backend/
frontend/
infrastructure/
docker-compose.yml
.env.example
.gitignore
README.md
SETUP.md
DEVELOPMENT.md
```

The repository root is important because Docker Compose and environment configuration are designed to be used from this location.

---

# 8. Environment Configuration

The project uses environment variables for configuration.

There are two important files:

```text
.env
.env.example
```

## `.env.example`

This file is committed to Git.

It documents the variables required by the project.

It must not contain real passwords, API keys, or private credentials.

## `.env`

This file contains your local development values.

It must **not** be committed.

The `.gitignore` already excludes `.env`.

---

# 9. Create the Local `.env`

Copy the example file:

```powershell
Copy-Item .env.example .env
```

Then open:

```text
.env
```

and provide your local values.

Example:

```env
DB_NAME=ai_resume_analyzer
DB_USER=resume_app
DB_PASSWORD=your-local-password
DB_ROOT_PASSWORD=your-local-root-password

FRONTEND_URL=http://localhost:5173

JWT_SECRET=your-local-jwt-secret

EMAIL_USER=
EMAIL_PASSWORD=
```

The exact variables should always match the current `.env.example`.

Do not blindly copy credentials from another developer.

Each developer should use their own local development secrets.

---

# 10. Environment Variable Rules

Never commit:

```text
.env
```

Never put real credentials into:

```text
.env.example
```

Use placeholders in `.env.example`:

```env
DB_PASSWORD=
JWT_SECRET=
EMAIL_PASSWORD=
```

Use real local values only in `.env`:

```env
DB_PASSWORD=my-local-password
JWT_SECRET=my-local-development-secret
```

---

# 11. Database Configuration

The local development database is MySQL.

The application uses:

```text
Database:
MySQL

Database name:
ai_resume_analyzer

Application user:
resume_app
```

The actual values are controlled by `.env`.

Do not hard-code database credentials in:

```text
application.yaml
Java source code
Docker Compose
frontend source code
```

---

# 12. Why MySQL Runs Through Docker

The project uses Docker for the MySQL development environment.

This prevents developers from having to install and maintain a separate MySQL server configuration on every machine.

It also makes the development environment more consistent across the team.

The host MySQL server does not need to be stopped.

If another local MySQL installation is already using port `3306`, the project can expose the Docker MySQL instance through another host port, such as:

```text
3307
```

while MySQL continues to listen on:

```text
3306
```

inside the container.

The actual mapping should be taken from the current `docker-compose.yml`.

---

# 13. Start Docker Infrastructure

From the **repository root**, run:

```powershell
docker compose up -d
```

Docker Compose reads:

```text
docker-compose.yml
```

and the root:

```text
.env
```

configuration.

---

# 14. Check Infrastructure Status

Run:

```powershell
docker compose ps
```

A healthy database should show a status similar to:

```text
Up ... (healthy)
```

Example:

```text
NAME                       IMAGE       STATUS
ai-resume-analyzer-mysql   mysql:8.4   Up ... (healthy)
```

If the container is restarting, inspect its logs:

```powershell
docker compose logs mysql
```

---

# 15. Check MySQL Health

You can inspect the container:

```powershell
docker compose ps
```

You can also inspect the MySQL health status:

```powershell
docker inspect --format="{{.State.Health.Status}}" ai-resume-analyzer-mysql
```

Expected:

```text
healthy
```

If the container name changes, use the name shown by:

```powershell
docker compose ps
```

---

# 16. Connect to MySQL

You can connect directly to MySQL inside the container:

```powershell
docker exec -it ai-resume-analyzer-mysql mysql -u resume_app -p
```

Enter the value of:

```env
DB_PASSWORD
```

when prompted.

If the connection succeeds, you should see:

```text
Welcome to the MySQL monitor.
```

---

# 17. Verify the Database

Inside MySQL:

```sql
SHOW DATABASES;
```

Select the application database:

```sql
USE ai_resume_analyzer;
```

Check the current database:

```sql
SELECT DATABASE();
```

Expected:

```text
ai_resume_analyzer
```

At the initial foundation stage, the schema may intentionally be empty.

Do not manually create application tables unless the project documentation or development task explicitly requires it.

---

# 18. Database Persistence

Docker Compose uses a persistent volume for MySQL.

Normal shutdown:

```powershell
docker compose down
```

This removes containers but preserves persistent database data.

Be careful with:

```powershell
docker compose down -v
```

This removes the Docker volumes and can delete the local database data.

Use `down -v` only when you intentionally want to reset the local database.

---

# 19. Recreating the Database Container

If the container needs to be recreated:

```powershell
docker compose down
docker compose up -d
```

The persistent volume remains unless `-v` is specified.

You normally do **not** need to pull the MySQL image again.

Docker reuses the locally cached image when the requested image is already available.

---

# 20. Pulling Images

The first time infrastructure is started, Docker may download the required images.

For example:

```text
mysql:8.4
```

After the image has been downloaded, subsequent:

```powershell
docker compose up -d
```

commands normally reuse the local image.

You do not need to manually pull the image every time.

To explicitly pull newer images:

```powershell
docker compose pull
```

Do this only when updating infrastructure images intentionally.

---

# 21. Backend Configuration

The backend configuration is located under:

```text
backend/src/main/resources/application.yaml
```

The backend reads environment variables such as:

```text
DB_URL
DB_USER
DB_PASSWORD
FRONTEND_URL
JWT_SECRET
```

The application configuration should use environment-variable placeholders rather than hard-coded secrets.

Example:

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3307/ai_resume_analyzer}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
```

The exact configuration should match the current project files.

---

# 22. Frontend URL

The backend must know which frontend origin is allowed to communicate with it.

For local development:

```text
http://localhost:5173
```

The environment configuration should therefore contain:

```env
FRONTEND_URL=http://localhost:5173
```

The backend configuration should reference that value.

This will later be used by Spring Security/CORS configuration.

Do not hard-code production frontend URLs into the development environment.

---

# 23. Backend Maven Project

Open the backend:

```powershell
cd backend
```

Verify that `pom.xml` exists:

```powershell
Get-ChildItem pom.xml
```

The backend is a Maven project.

If IntelliJ does not recognize it automatically:

1. Open the project in IntelliJ IDEA.
2. Locate `backend/pom.xml`.
3. Right-click `pom.xml`.
4. Select the Maven import/reload option.
5. Allow IntelliJ to resolve dependencies.

Do not open only the Java source directory and expect IntelliJ to infer the complete Maven project.

---

# 24. Configure IntelliJ Java Version

The project uses Java 21.

In IntelliJ IDEA, configure:

```text
Project SDK → Java 21
```

and ensure the Maven importer uses the same JDK.

The exact IntelliJ menus may vary by version, but the important requirement is:

```text
Project JDK = Java 21
Maven JDK  = Java 21
```

---

# 25. Compile the Backend

From:

```text
backend/
```

run:

```powershell
.\mvnw.cmd clean compile
```

A successful build ends with:

```text
BUILD SUCCESS
```

The compiler should indicate:

```text
release 21
```

---

# 26. Run Backend Tests

From:

```text
backend/
```

run:

```powershell
.\mvnw.cmd test
```

At the early foundation stage, there may be few or no application tests.

As the application grows, tests should be added alongside new functionality.

---

# 27. Start the Backend

From:

```text
backend/
```

run:

```powershell
.\mvnw.cmd spring-boot:run
```

Alternatively, run the main Spring Boot application class from IntelliJ IDEA.

The backend should start on the configured server port.

The default Spring Boot port is:

```text
8080
```

Therefore the local backend is normally:

```text
http://localhost:8080
```

---

# 28. Backend Startup Verification

A successful startup should show Spring Boot startup messages and eventually indicate that the application has started.

If the application fails during startup:

1. Read the first meaningful error.
2. Check the environment variables.
3. Check Docker infrastructure.
4. Check database connectivity.
5. Check the configured port.
6. Check the Java version.
7. Check Maven output.

Do not immediately change multiple configuration files at once.

---

# 29. Frontend Setup

Open a second terminal.

From the repository root:

```powershell
cd frontend
```

Verify:

```powershell
Get-ChildItem
```

You should see files such as:

```text
package.json
vite.config.*
src/
```

---

# 30. Install Frontend Dependencies

Run:

```powershell
npm install
```

This installs dependencies declared in:

```text
package.json
```

and uses:

```text
package-lock.json
```

Do not commit:

```text
node_modules/
```

---

# 31. Start the Frontend

From:

```text
frontend/
```

run:

```powershell
npm run dev
```

Vite should display a local URL similar to:

```text
http://localhost:5173/
```

Open that URL in a browser.

---

# 32. Frontend Build Verification

Run:

```powershell
npm run build
```

A successful build confirms that the frontend can be compiled for production.

The generated `dist/` directory should not be committed.

---

# 33. Complete Local Startup

Once setup is complete, a typical development environment has:

```text
Docker
  │
  └── MySQL
       │
       ▼
Spring Boot Backend
       │
       ▼
React + TypeScript Frontend
```

Expected local endpoints:

```text
Frontend
http://localhost:5173

Backend
http://localhost:8080

MySQL
localhost:<configured-host-port>
```

The exact MySQL host port must be taken from:

```powershell
docker compose ps
```

---

# 34. Recommended Terminal Layout

A convenient setup is to use three terminals.

### Terminal 1 — Infrastructure

From repository root:

```powershell
docker compose up -d
```

Keep this available for:

```powershell
docker compose ps
docker compose logs
```

### Terminal 2 — Backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

### Terminal 3 — Frontend

```powershell
cd frontend
npm run dev
```

---

# 35. IntelliJ IDEA Recommended Setup

Open the entire repository:

```text
ai-resume-analyzer/
```

Do not open only:

```text
backend/src/
```

The project contains multiple components:

```text
backend/
frontend/
infrastructure/
```

Opening the repository root makes it easier to work with the complete project.

For the backend, make sure IntelliJ recognizes:

```text
backend/pom.xml
```

as a Maven project.

---

# 36. Git Verification After Setup

After completing setup:

```powershell
git status
```

You should not see:

```text
.env
node_modules/
target/
```

as untracked files.

If `.env` appears in `git status`, stop and verify `.gitignore` before committing anything.

---

# 37. First-Time Developer Verification Checklist

Before starting development, verify:

```text
[ ] Git installed
[ ] Java 21 installed
[ ] Maven Wrapper available
[ ] Node.js installed
[ ] npm installed
[ ] Docker Desktop installed
[ ] Docker Desktop running
[ ] Repository cloned
[ ] .env created
[ ] .env contains local values
[ ] .env is ignored by Git
[ ] Docker infrastructure starts
[ ] MySQL container is healthy
[ ] MySQL application user can connect
[ ] Backend Maven project is recognized
[ ] Backend compiles
[ ] Backend starts
[ ] Frontend dependencies install
[ ] Frontend starts
[ ] Frontend opens at localhost:5173
```

---

# 38. Common Problem: Maven Says No POM

If you run:

```powershell
mvn clean compile
```

from:

```text
ai-resume-analyzer/
```

and receive:

```text
There is no POM in this directory
```

that is expected if `pom.xml` is inside `backend/`.

Move into the backend:

```powershell
cd backend
```

Then run:

```powershell
.\mvnw.cmd clean compile
```

---

# 39. Common Problem: IntelliJ Does Not Recognize Maven

Make sure the backend contains:

```text
backend/pom.xml
```

Then import/reload the Maven project from that `pom.xml`.

Also verify IntelliJ is using Java 21.

If Maven dependencies remain stale, reload the Maven project.

---

# 40. Common Problem: MySQL Port Already in Use

If Docker reports:

```text
ports are not available
bind: Only one usage of each socket address
```

another process is already using the host port.

On Windows:

```powershell
netstat -ano | findstr :3306
```

Find the process:

```powershell
tasklist | findstr <PID>
```

If an existing local MySQL server is required by another project, do not stop it just to run this project.

Instead, map the Docker MySQL container to another host port.

For example:

```yaml
ports:
  - "3307:3306"
```

This means:

```text
Host:
localhost:3307

Container:
3306
```

The application running directly on Windows must then connect to:

```text
localhost:3307
```

---

# 41. Common Problem: Docker Compose Variables Are Blank

If Compose reports warnings such as:

```text
The "DB_USER" variable is not set.
Defaulting to a blank string.
```

verify that `.env` exists at the repository root:

```text
ai-resume-analyzer/
├── .env
├── docker-compose.yml
└── ...
```

Then run Compose from the repository root:

```powershell
docker compose up -d
```

Check that `.env` contains the required variables.

---

# 42. Common Problem: MySQL Container Keeps Restarting

Check:

```powershell
docker compose ps
```

Then:

```powershell
docker compose logs mysql
```

Common causes include:

- missing `DB_ROOT_PASSWORD`
- missing `DB_NAME`
- missing `DB_USER`
- missing `DB_PASSWORD`
- invalid environment configuration
- conflicting initialization settings
- corrupted local volume

Do not delete the volume immediately.

First inspect the logs and identify the actual cause.

---

# 43. Common Problem: Backend Cannot Connect to MySQL

Check that MySQL is healthy:

```powershell
docker compose ps
```

Then verify the database credentials in `.env`.

If the backend runs directly on Windows and MySQL is exposed as:

```text
3307:3306
```

the backend should connect to:

```text
localhost:3307
```

not:

```text
localhost:3306
```

If both backend and MySQL run inside Docker, the connection configuration is different and should use the Compose service name rather than `localhost`.

---

# 44. Common Problem: Port 8080 Is Already in Use

Check:

```powershell
netstat -ano | findstr :8080
```

Identify the process:

```powershell
tasklist | findstr <PID>
```

Either stop the conflicting process if appropriate or configure the Spring Boot server to use another port.

Do not randomly change ports without updating the corresponding configuration.

---

# 45. Common Problem: Frontend Port 5173 Is Already in Use

Vite may automatically select another port.

Check the terminal output:

```text
Local: http://localhost:5173/
```

If another port is selected, update any backend CORS configuration that explicitly depends on the frontend origin.

---

# 46. Common Problem: `.env` Is Not Being Read

Remember that Spring Boot does not automatically treat every `.env` file as a general-purpose environment source in the same way Docker Compose does.

The project should have an explicit strategy for how environment variables reach the Spring Boot process.

For local development, ensure the variables are available to the process running Spring Boot.

If running from IntelliJ IDEA, configure the required environment variables in the Run Configuration if they are not otherwise supplied to the application.

Do not assume that placing `.env` at the repository root automatically makes every variable available to every process.

---

# 47. Common Problem: CORS Errors

CORS configuration belongs to the backend security configuration.

During local development, the frontend origin is normally:

```text
http://localhost:5173
```

The backend should be configured to allow the intended frontend origin.

Do not solve CORS problems by permanently allowing:

```text
*
```

especially when authenticated requests or credentials are involved.

---

# 48. Common Problem: Docker Image Pulling Every Time

Normally:

```powershell
docker compose up -d
```

does not download an image repeatedly if the required image already exists locally.

Docker reuses the cached image.

To see local images:

```powershell
docker images
```

To explicitly download updated images:

```powershell
docker compose pull
```

Only use this when intentionally updating images.

---

# 49. Common Problem: Database Data Disappeared

Check whether the project was started with:

```powershell
docker compose down -v
```

The `-v` option removes volumes.

Normal shutdown should use:

```powershell
docker compose down
```

Persistent data should remain available when the container is recreated without deleting the volume.

---

# 50. Resetting the Local Database

A complete database reset is destructive.

Only do this when you intentionally want to discard local database data.

```powershell
docker compose down -v
docker compose up -d
```

Then wait for MySQL to become healthy:

```powershell
docker compose ps
```

Do not use this command as a routine troubleshooting step.

---

# 51. Working With `.env.example`

When a developer adds a required environment variable:

1. Add the variable to `.env.example`.
2. Add the real value to the local `.env`.
3. Update application configuration.
4. Update this setup guide if necessary.
5. Explain the variable in the Pull Request.

Example:

```env
AI_API_KEY=
```

Do not commit:

```env
AI_API_KEY=real-secret-value
```

---

# 52. Local Development Secrets

Local development secrets do not need to match another developer's secrets.

For example:

```env
DB_PASSWORD=developer-specific-password
JWT_SECRET=developer-specific-secret
```

What matters is that:

- the value is valid
- the application and infrastructure use the same value where required
- the value is not committed

---

# 53. Clean Setup Verification

After everything is running, verify:

### Infrastructure

```powershell
docker compose ps
```

MySQL should be:

```text
healthy
```

### Backend

```powershell
cd backend
.\mvnw.cmd clean compile
```

Expected:

```text
BUILD SUCCESS
```

### Frontend

```powershell
cd frontend
npm run build
```

Expected:

```text
build completed successfully
```

### Git

From the repository root:

```powershell
git status
```

No local secrets or generated artifacts should be staged.

---

# 54. Setup Completion Criteria

A developer is considered ready to work on the project when:

```text
Docker infrastructure
        ↓
MySQL healthy
        ↓
Spring Boot starts
        ↓
Frontend starts
        ↓
Frontend can communicate with backend
```

At this point, the developer can follow:

```text
DEVELOPMENT.md
```

for the normal development workflow.

---

# 55. Recommended First-Day Workflow

After completing setup:

1. Read `README.md`.
2. Read this `SETUP.md`.
3. Read `DEVELOPMENT.md`.
4. Verify Docker.
5. Verify MySQL.
6. Compile the backend.
7. Start the backend.
8. Start the frontend.
9. Verify the frontend in the browser.
10. Check Git status.
11. Create a development branch before making project changes.

The developer should not modify shared branches directly.

---

# 56. Setup Command Reference

## Clone

```powershell
git clone <repository-url>
cd ai-resume-analyzer
```

## Create environment file

```powershell
Copy-Item .env.example .env
```

## Start infrastructure

```powershell
docker compose up -d
```

## Check infrastructure

```powershell
docker compose ps
```

## View MySQL logs

```powershell
docker compose logs mysql
```

## Stop infrastructure

```powershell
docker compose down
```

## Backend

```powershell
cd backend
.\mvnw.cmd clean compile
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

## Frontend

```powershell
cd frontend
npm install
npm run dev
```

## Frontend build

```powershell
npm run build
```

---

# 57. Final Setup Checklist

Before considering your machine ready:

```text
Environment
[ ] Git installed
[ ] Java 21 installed
[ ] Node.js installed
[ ] npm installed
[ ] Docker Desktop installed and running

Repository
[ ] Repository cloned
[ ] Correct project structure present
[ ] .env created from .env.example
[ ] .env contains valid local configuration
[ ] .env is ignored by Git

Infrastructure
[ ] docker compose up -d succeeds
[ ] MySQL container is running
[ ] MySQL health check is healthy
[ ] Application database exists
[ ] Application user can connect

Backend
[ ] IntelliJ recognizes backend/pom.xml
[ ] IntelliJ uses Java 21
[ ] Maven Wrapper works
[ ] Backend compiles
[ ] Backend tests run
[ ] Spring Boot starts

Frontend
[ ] npm install succeeds
[ ] Vite starts
[ ] Frontend opens in browser
[ ] Frontend production build succeeds

Git
[ ] git status is clean or contains only intentional work
[ ] .env is not tracked
[ ] target/ is not tracked
[ ] node_modules/ is not tracked

Ready
[ ] README.md reviewed
[ ] SETUP.md reviewed
[ ] DEVELOPMENT.md reviewed
[ ] Ready to create a development branch
```

---

# 58. Important Rules

Keep these rules in mind:

1. Do not commit `.env`.
2. Do not commit passwords or API keys.
3. Do not delete Docker volumes unless you intentionally want to delete local data.
4. Do not stop another developer's or another project's MySQL server unnecessarily.
5. Do not change ports randomly; update dependent configuration when a port changes.
6. Use Java 21 for this project.
7. Use the Maven Wrapper for backend builds.
8. Run Docker Compose from the repository root.
9. Keep `.env.example` updated when configuration requirements change.
10. Follow `DEVELOPMENT.md` for Git and contribution workflow.
