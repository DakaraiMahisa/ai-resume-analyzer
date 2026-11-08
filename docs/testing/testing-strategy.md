# Testing Strategy

## 1. Overview

This document defines the testing strategy for the Spring Boot backend as it is
currently implemented. It records the tests that exist today, the infrastructure
used to run them, the current verification boundary, and the work that remains
before production deployment.

The objectives are to:

- verify authentication and token lifecycle behavior;
- verify processing-job state transitions and validation;
- verify controller request validation and response contracts;
- verify security enforcement and resume ownership isolation;
- verify that the application context, Flyway migrations, and MySQL persistence
  can start together in an isolated test environment; and
- provide repeatable checks for local development and release decisions.

The scope is the Java 21 Maven module in `backend/`. Frontend checks, end-to-end
workflows, and live external-service integrations are not currently part of the
automated backend test suite.

## 2. Testing approach

The project uses three principal test levels:

| Level | Implementation | Purpose |
|---|---|---|
| Unit | JUnit 5 with Mockito and `MockitoExtension` | Exercises service logic with repositories and collaborators isolated as mocks. These tests are fast and focus on business rules, validation, exceptions, and interaction order. |
| Web layer | Spring Boot `@WebMvcTest` with MockMvc and mocked collaborators | Loads the MVC slice for a controller and verifies HTTP status codes, validation behavior, JSON response fields, and service delegation without running the complete application. |
| Integration | Spring Boot `@SpringBootTest`, MockMvc, repositories, security configuration, and Testcontainers MySQL | Exercises the application context and cross-component behavior, including JWT authentication, authorization, persistence, Flyway migrations, and ownership checks. |

The levels are complementary. Unit tests isolate decisions inside a service;
web-layer tests verify the HTTP boundary with service dependencies replaced by
mocks; integration tests verify that security, persistence, migrations, and
Spring configuration work together.

## 3. Technology stack

| Tool or framework | Role |
|---|---|
| Java 21 | Runtime and language level configured in `backend/pom.xml`. |
| Maven Wrapper (`mvnw`, `mvnw.cmd`) | Reproducible Maven entry point for compilation, tests, and verification. |
| JUnit 5 | Test and assertion framework used by the test classes. |
| Mockito | Mocking, interaction verification, argument capture, and ordered-call assertions in unit and web tests. |
| Spring Boot Test | Application-context and Spring integration testing support. |
| Spring MVC Test / MockMvc | In-process HTTP request execution for controller and security tests. |
| Testcontainers | Starts an isolated MySQL container for Spring Boot integration tests. |
| MySQL 8.4 | Database engine used by the Testcontainers test database. |
| Flyway | Applies the application migrations to the test database before database-backed tests run. |

These test dependencies are declared in `backend/pom.xml`, including
Spring Boot's test starters, `spring-boot-testcontainers`,
`testcontainers-junit-jupiter`, and `testcontainers-mysql`.

## 4. Test organisation

Tests are grouped by application feature under
`backend/src/test/java/com/airesumeanalyzer/backend/`. The current classes and
responsibilities are:

| Test class | Level | Implemented responsibility |
|---|---|---|
| `auth/service/AuthServiceTest` | Unit | Registration, duplicate-email and compromised-password handling; login and access/refresh token generation; refresh-token rotation and invalid, expired, disabled-account, and logout/revocation paths. Dependencies are isolated with Mockito. |
| `processing/service/ProcessingJobServiceTest` | Unit | Job creation, starting, progress updates and bounds validation, completion, failure, retrieval by ID, latest-job retrieval, missing-job handling, invalid states, and unsupported document handlers. |
| `processing/service/DocumentProcessingServiceImplTest` | Unit | Resume and job-description orchestration, successful completion, failure handling and fallback messages, exception propagation, and ordered interaction verification. |
| `auth/controller/AuthControllerTest` | Web layer | Registration, invalid registration input, login, refresh-token, and logout endpoints; HTTP status, response fields, validation behavior, and mocked service delegation. |
| `auth/security/AuthSecurityIntegrationTest` | Integration | Rejection of missing, invalid, and expired JWTs; acceptance of a valid JWT; and resume retrieval, deletion, and listing isolation between authenticated users. |
| `BackendApplicationTests` | Integration/context smoke test | Loads the complete Spring Boot application context with `TestcontainersConfiguration` imported and verifies `contextLoads()`. |
| `config/TestcontainersConfiguration` | Test infrastructure | Defines the MySQL 8.4 Testcontainer and exposes it through Spring Boot `@ServiceConnection`. It is configuration rather than a test class. |

There are no files under `backend/src/test/resources` in the inspected
repository.

## 5. Test infrastructure

`TestcontainersConfiguration` defines a `MySQLContainer` using the
`mysql:8.4` image, with the test database name `resume_analyzer_test` and
test-only container credentials. The bean is annotated with Spring Boot's
`@ServiceConnection`, so Spring Boot derives the datasource connection for the
test application context from the running container rather than requiring a
hard-coded test database URL.

The configuration is imported by both `BackendApplicationTests` and
`AuthSecurityIntegrationTest`. Consequently, those tests use an isolated
containerized MySQL instance rather than the developer's normal database.
Flyway is enabled by `backend/src/main/resources/application.yaml` and uses
`classpath:db/migration`; the migrations under
`backend/src/main/resources/db/migration/` are therefore applied to the
containerized database during context startup. JPA is configured with
`ddl-auto: validate`, so the schema is validated rather than generated by
Hibernate.

The test configuration does not provide a checked-in
`application-test.properties` or test resource override. The application
configuration still references environment-backed settings such as
`GEMINI_API_KEY` and `JWT_SECRET`. Test runs must provide whatever
configuration is required by the active Spring context using non-production
test values, without committing or documenting secret values. For database
connections, the imported `@ServiceConnection` is the intended source for the
integration-test datasource.

## 6. Current test coverage

The implemented suite currently verifies:

- authentication registration, login, refresh-token rotation, logout/revocation,
  invalid and expired token paths, compromised-password rejection, and disabled
  account handling;
- authentication controller validation and response contracts for the public
  auth endpoints;
- processing-job lifecycle rules, state transitions, progress limits,
  handler selection, retrieval, and error validation;
- resume and job-description processing orchestration, including completion,
  failure marking, fallback failure messages, and propagation of retrieval,
  startup, and completion exceptions;
- JWT protection for secured endpoints;
- authenticated resume access, prevention of cross-user retrieval and deletion,
  and restriction of resume listings to the authenticated user; and
- Spring context startup with a containerized MySQL database and Flyway-managed
  schema.

The repository does not contain a generated coverage report, so this strategy
does not state a numerical coverage percentage. The latest recorded
`mvn clean verify` result was **67 tests, 0 failures, 0 errors, and 0 skipped
tests**. This is a point-in-time verification result, not a permanent
guarantee for future changes.

## 7. Deferred testing

The following areas are not comprehensively tested in the current repository
and remain planned or deferred:

| Area | Current status |
|---|---|
| Resume service tests | No dedicated test class was found; deferred. |
| Job description service tests | No dedicated test class was found; deferred. |
| Resume processing service tests | No dedicated test class was found. The orchestration class is tested, but the concrete resume processing service is not; deferred. |
| Job description processing service tests | No dedicated test class was found. The orchestration class is tested, but the concrete job-description processing service is not; deferred. |
| Backblaze B2 storage integration tests | No B2 integration test was found; deferred. |
| Gemini API integration and failure handling tests | No live Gemini integration or external failure-handling test was found; deferred. |
| Frontend lint and production build checks | `frontend/package.json` defines `npm run lint` and `npm run build`, but no passing CI or test result was established by the backend suite; deferred as release automation. |
| End-to-end application workflow tests | No end-to-end test suite was found; deferred. |

These gaps are intentionally distinguished from the implemented
`DocumentProcessingServiceImplTest`, which verifies orchestration and error
handling through mocked processing services but does not verify the concrete
external-service or storage implementations.

## 8. Test execution

Run commands from the `backend/` directory. The Maven Wrapper is present in
both Unix and Windows forms; the Windows commands are:

```powershell
cd backend
.\mvnw.cmd test
.\mvnw.cmd clean verify
```

Run an individual test class with Maven Surefire's `-Dtest` selector:

```powershell
.\mvnw.cmd -Dtest=AuthServiceTest test
.\mvnw.cmd -Dtest=ProcessingJobServiceTest test
.\mvnw.cmd -Dtest=DocumentProcessingServiceImplTest test
.\mvnw.cmd -Dtest=AuthControllerTest test
.\mvnw.cmd -Dtest=AuthSecurityIntegrationTest test
.\mvnw.cmd -Dtest=BackendApplicationTests test
```

Testcontainers requires a working Docker installation and a running Docker
daemon. Docker Desktop is the expected Windows prerequisite. The daemon must
be able to pull and start `mysql:8.4`; otherwise the context and security
integration tests cannot start their database.

The application configuration uses environment variables including
`DB_URL`, `DB_USER`, `DB_PASSWORD`, `GEMINI_API_KEY`, and `JWT_SECRET`, with
additional optional variables documented by `backend/src/main/resources/application.yaml`.
For Testcontainers-backed integration tests, the datasource is supplied by
Spring Boot's service connection. Any other required placeholders must be
provided as safe test configuration in the execution environment. Never place
real credentials, API keys, JWT secrets, or other secret values in this
document or in source control.

## 9. Quality gates

At minimum, the following checks are expected before merging or releasing:

1. Backend compilation succeeds:

   ```powershell
   .\mvnw.cmd clean compile
   ```

2. The automated backend tests pass:

   ```powershell
   .\mvnw.cmd test
   ```

3. Full Maven verification succeeds:

   ```powershell
   .\mvnw.cmd clean verify
   ```

4. Frontend lint and production build are required once they are configured as
   CI gates. The currently defined local commands are:

   ```powershell
   cd frontend
   npm run lint
   npm run build
   ```

5. Dependency vulnerability checks are required once a dependency-scanning
   check is configured in CI. No such check is represented in the inspected
   Maven or frontend configuration.

The recorded successful build result supports the backend gate at the time it
was run; it does not replace rerunning the gates for a new change.

## 10. Future testing improvements

Future work should be prioritized by deployment risk rather than by attempting
to test every implementation detail:

1. Add focused tests for the concrete resume and job-description services,
   especially persistence boundaries, file handling, and state/error
   transitions. These services are central to document processing but are not
   currently isolated by dedicated tests.
2. Add controlled integration tests for storage behavior, starting with the
   configured local provider and then Backblaze B2 behavior using test
   credentials, mocks, or a dedicated non-production account rather than
   production data.
3. Add Gemini client contract/failure tests using a stubbed or otherwise
   controlled provider. Cover timeout, malformed response, quota, and
   provider-error handling without making ordinary builds depend on a live
   API.
4. Add CI execution of the existing frontend lint and production build
   commands, followed by a small number of end-to-end workflows covering
   registration, authentication, resume upload/processing, and analysis.
5. Add dependency vulnerability scanning to CI and retain the existing
   compile, test, and verification gates.

This order targets authentication, data access, document processing, storage,
and external-provider risks first while keeping the initial deployment scope
focused.
