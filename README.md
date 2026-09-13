# AI Resume Analyzer

## ✨ Smart resume-to-job matching with AI-powered insights

AI Resume Analyzer is a full-stack application designed to help candidates and teams evaluate how well a resume aligns with a specific job description. It extracts structured information from both documents, compares key requirements, and surfaces evidence-backed recommendations for improvement.

This project blends AI-driven understanding, ATS-style scoring, secure authentication, and a modern web interface into one workflow for resume analysis.

---

## 🚀 What this project does

- Analyzes resumes against target job descriptions
- Extracts skills, experience, education, and project evidence
- Identifies missing or weakly supported requirements
- Computes coverage and match quality across resume and job criteria
- Produces actionable recommendations with supporting evidence
- Stores analysis results for review in a dashboard workflow
- Supports secure user authentication and document management

---

## ✨ Core features

- 🧠 AI-powered claim extraction and document understanding
- 📄 Resume upload and storage workflows
- 📝 Job description ingestion and structured requirement parsing
- 🎯 ATS-style scoring and requirement coverage evaluation
- 🔎 Evidence-backed matching and recommendation engine
- 📊 Analysis history and dashboard views
- 🔐 JWT-based authentication with refresh token support
- 🗃️ MySQL-backed persistence with Flyway migrations

---

## 🏗️ Technology stack

### Backend

- Java 21
- Spring Boot 4.1.0
- Spring Security
- Spring Data JPA
- Flyway
- MySQL 8.4
- OpenAPI / Swagger
- Google GenAI + Ollama-based AI integration

### Frontend

- React 19
- TypeScript
- Vite
- React Router
- Tailwind CSS
- Zustand for state management

### Infrastructure

- Docker Compose
- MySQL 8.4
- Environment-based configuration

---

## 📁 Repository structure

```text
ai-resume-analyzer/
├── backend/                  # Spring Boot API and business logic
├── frontend/                 # React + TypeScript application
├── infrastructure/           # Infrastructure-related config
├── storage/                  # Local document storage
├── docker-compose.yml        # Local database services
├── .env                      # Local environment variables (not committed)
├── .env.example              # Environment variable template
├── .gitignore
├── README.md
├── docs/
│   ├── DEVELOPMENT.md
│   ├── SETUP.md
│   └── handbook/
└── ...
```

---

## 🧭 Project status

🚧 Active foundation / early development

The repository already has a working full-stack foundation, including:

- Java backend with Spring Boot
- React frontend with protected routes and dashboard pages
- MySQL database and migration setup
- Docker-based local infrastructure
- AI-assisted resume/job matching flows

The application is being actively expanded with more analysis workflows and product refinement.

---

## ⚙️ Prerequisites

Before starting the project, make sure you have:

- ☑️ Git
- ☑️ Java 21
- ☑️ Maven Wrapper (included in the backend)
- ☑️ Node.js LTS and npm
- ☑️ Docker Desktop / Docker Compose

---

## 🚀 Quick start

### 1) Clone the repository

```bash
git clone <repository-url>
cd ai-resume-analyzer
```

### 2) Configure environment variables

Create your local `.env` file from the example:

```bash
copy .env.example .env
```

Update the values in `.env` for your local setup. The example includes the database connection and JWT configuration.

### 3) Start infrastructure

```bash
docker compose up -d mysql
```

This starts the MySQL container required by the backend.

### 4) Run the backend

```bash
cd backend
./mvnw spring-boot:run
```

The API runs on:

- http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html

### 5) Run the frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend runs on:

- http://localhost:5173

---

## 🔐 Environment variables

The project uses environment-based configuration. Key values include:

- `DB_NAME` — MySQL database name
- `DB_USER` — database user
- `DB_PASSWORD` — database password
- `DB_ROOT_PASSWORD` — root password for MySQL
- `DB_URL` — JDBC URL for the backend
- `JWT_SECRET` — secret for JWT signing
- `GEMINI_API_KEY` — Google GenAI API key
- `GEMINI_CHAT_MODEL` — model used for AI chat analysis
- `FRONTEND_URL` — frontend origin used by the backend

Example values are available in `.env.example`.

---

## 🧩 Application flow

The system is designed around a simple workflow:

1. Upload a resume
2. Add or select a job description
3. Parse structured requirements and evidence
4. Run matching and ATS-style analysis
5. Review score, gaps, and recommendations
6. View saved analyses in the dashboard

---

## 🧪 Development notes

- The backend uses Flyway for database migration management.
- The frontend is organized around route-based pages for dashboard, resumes, jobs, and analysis.
- The application supports protected authenticated areas and public landing/auth pages.
- AI features are integrated into the matching and validation logic for more explainable resume analysis.

---

## 📌 Useful local URLs

- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- MySQL: localhost:3307

---

## 💡 Summary

AI Resume Analyzer is a modern resume intelligence platform for understanding candidate fit in a practical, explainable way. It brings together AI analysis, structured matching, and a polished web interface to help users turn resume data into actionable hiring insights.

