# Employee API — Production-Grade REST Microservice

## Status: IMPLEMENTED, PENDING FINAL VERIFICATION

**Current Stack:** Hexagonal Architecture (DDD) | PostgreSQL | JPA | Spring Boot 3.4.0 | Java 21 | OpenAPI | Micrometer | OpenTelemetry | Docker Compose

## Implemented

### 1. Project Bootstrap ✅
- [x] Gradle wrapper added for reproducible builds
- [x] Spring Boot build configured with Web, Data JPA, PostgreSQL, Flyway, OpenAPI, Actuator, Micrometer, and OpenTelemetry dependencies
- [x] Test dependencies configured with Spring Boot Test and H2

### 2. Domain Layer ✅
- [x] Employee aggregate root implemented with validation rules
- [x] Domain exceptions implemented
- [x] EmployeeRepository port defined

### 3. Application Layer ✅
- [x] CreateEmployeeUseCase implemented
- [x] ListEmployeesUseCase implemented
- [x] DeleteEmployeeUseCase implemented
- [x] Request and response DTOs implemented

### 4. Infrastructure Layer ✅
- [x] Spring Boot application entry point implemented
- [x] JPA entity and Spring Data repository implemented
- [x] Repository adapter implemented
- [x] REST controller implemented for employee endpoints
- [x] API key filter implemented
- [x] Initial Flyway migration created

### 5. Runtime Configuration ✅
- [x] Base Spring configuration added in application.yml
- [x] Local profile added in application-local.yml
- [x] Development profile added in application-dev.yml
- [x] Staging profile added in application-stage.yml
- [x] Production profile added in application-prod.yml
- [x] Test profile configured with H2 in-memory database

### 6. API Documentation and Observability ✅
- [x] OpenAPI and Swagger UI endpoints configured
- [x] Prometheus metrics export configured
- [x] Jaeger/OpenTelemetry endpoints configured in runtime profiles
- [x] Grafana provisioning and dashboard assets created

### 7. Container and Environment Assets ✅
- [x] Dockerfile created for the application
- [x] docker/local environment created with PostgreSQL
- [x] docker/dev environment created with PostgreSQL, Prometheus, Grafana, and Jaeger
- [x] docker/stage environment created with PostgreSQL, Prometheus, Grafana, and Jaeger
- [x] docker/prod environment created with hardened runtime settings
- [x] Environment files added for local, dev, stage, and prod

### 8. Test Coverage Added ✅
- [x] Domain tests added
- [x] Application use case tests added
- [x] REST integration tests added
- [x] Test-only H2 configuration added

### 9. Documentation Added ✅
- [x] DOCKER.md created
- [x] ARCHITECTURE.md created
- [x] PRODUCTION_SETUP.md created

## Current API Surface

```
POST   /api/v1/employees             Create employee
GET    /api/v1/employees             List employees
DELETE /api/v1/employees/{id}        Delete employee by UUID
```

## Remaining Work

### 10. Verification Pending
- [ ] Run the full Gradle test suite on the current branch
- [ ] Start the docker/local stack and verify local boot
- [ ] Start the docker/dev stack and verify PostgreSQL, Prometheus, Grafana, and Jaeger
- [ ] Verify Flyway migration runs correctly against PostgreSQL
- [ ] Verify actuator health and prometheus endpoints respond correctly

### 11. Performance and Release Hardening Pending
- [ ] Add Grafana k6 stress tests
- [ ] Run load tests and record throughput/latency baselines
- [ ] Review production environment defaults and replace placeholder secrets
- [ ] Perform final code review before merge

## Notes

- The codebase currently targets Java 21 in the Gradle build.
- H2 is retained only for tests.
- PostgreSQL is the runtime database for local, dev, stage, and prod profiles.
- Observability is configured at the runtime and container level, but still needs end-to-end verification in a running environment.

## Task: Expose Agent-Facing Endpoints Without API Key

### Context
- What: Remove API key enforcement from documentation and observability endpoints consumed by sidecar agents.
- Why: Prometheus, Swagger/OpenAPI consumers, and other endpoint-driven agents are being blocked by the API key filter.
- Risk: Over-broad exclusions could weaken protection on employee business endpoints.

### Steps
- [x] Align API key exclusions with the actual Springdoc and Actuator endpoint paths.
- [x] Add regression tests covering public docs and observability endpoints.
- [x] Run verification for the affected test suite and inspect the diff.

### Verification
- [x] Swagger/OpenAPI endpoints do not return 401 without an API key.
- [x] Actuator endpoints used by sidecars do not return 401 without an API key.
- [x] Employee business endpoints still require a valid API key.

## Task: Paginate employee listing

### Context
- What: Replace the employee list use case and REST endpoint response with a paginated result.
- Why: Returning the entire employee table does not scale and creates avoidable latency and payload size issues.
- Risk: Breaking the current API contract or leaking Spring pagination types into the domain port.

### Steps
- [x] Introduce a domain-safe paginated repository result and update the list use case to request a page.
- [x] Expose pagination parameters and metadata in the employee REST API while keeping defaults sensible.
- [x] Add unit and integration coverage for pagination behavior and invalid request handling.

### Verification
- [x] Paginated list use case returns only the requested slice with correct metadata.
- [x] GET /api/v1/employees supports page and size query parameters.
- [x] Affected tests pass and the diff is reviewed.

## Task: Document and sort paginated employee listing

### Context
- What: Add explicit OpenAPI documentation for the paginated employee response and allow clients to choose sort field and direction.
- Why: The API contract should describe the paged payload accurately, and clients should not be forced into a fixed sort order.
- Risk: Weak validation could allow unsupported sort properties or unstable ordering across pages.

### Steps
- [x] Introduce a validated sort model for employee listing and carry it through the use case and repository port.
- [x] Expose sort query parameters on the REST endpoint with deterministic secondary ordering.
- [x] Publish the concrete paginated employee response schema in OpenAPI and cover the behavior with tests.

### Verification
- [x] GET /api/v1/employees sorts by the requested field and direction.
- [x] Invalid sort parameters are rejected with 400.
- [x] OpenAPI documents the paginated employee response and list query parameters.

## Task: Harden page size and align docs tests

### Context
- What: Enforce a maximum employee page size and make the test Springdoc path match the main application configuration.
- Why: Page-size limits protect the endpoint from oversized requests, and consistent docs paths reduce false negatives in integration tests.
- Risk: Tightening validation or test-time configuration can break existing endpoint assertions if not updated together.

### Steps
- [x] Enforce a maximum page size in the employee list use case and document it on the REST endpoint.
- [x] Align the test profile Springdoc path with the main application and update docs/auth integration tests accordingly.
- [x] Add regression coverage for oversized page requests and the aligned docs endpoint.

### Verification
- [x] Oversized employee page requests are rejected with 400.
- [x] OpenAPI is served from the configured test path and still bypasses API-key enforcement.
- [x] Targeted tests pass.

## Task: Centralize employee list query validation

### Context
- What: Move employee list pagination and sorting defaults and validation into a dedicated request object.
- Why: The list contract is currently split between controller parameters and use-case internals, which makes constraints harder to maintain consistently.
- Risk: Spring query binding or OpenAPI parameter generation could drift during the refactor if the request object is not wired correctly.

### Steps
- [x] Introduce a list request object that owns defaults, validation, and conversion to the domain page query.
- [x] Refactor the controller and use case to consume the request object instead of raw pagination and sort parameters.
- [x] Add regression coverage for request-object defaults and validation through unit and integration tests.

### Verification
- [x] The use case accepts the centralized list request and preserves current pagination and sorting behavior.
- [x] GET /v1/employees still binds and documents page, size, sortBy, and direction correctly.
- [x] Affected tests pass.

## Task: Standardize employee use-case request objects

### Context
- What: Make delete follow the same application-request pattern as create and list, and remove primitive overloads from the list use case.
- Why: Application use cases should expose a consistent request-object boundary instead of a mix of transport primitives and request DTOs.
- Risk: Refactoring use-case signatures can break tests or controller wiring if every caller is not updated together.

### Steps
- [x] Introduce a delete request object and refactor the delete use case and controller to consume it.
- [x] Remove the primitive list use-case overloads so request objects are the only application entry point for listing.
- [x] Update unit and integration coverage to exercise the request-based flow consistently.

### Verification
- [x] Delete and list use cases are invoked through request objects only.
- [x] Employee REST endpoints preserve existing behavior after the signature refactor.
- [x] Affected tests pass.

## Task: Add bean validation and shared test support

### Context
- What: Add bean validation annotations to the application request DTOs and replace duplicated fake repositories in application tests with one shared in-memory implementation.
- Why: Request validation should fail at the HTTP boundary where possible, and test support should not duplicate the same repository behavior across suites.
- Risk: Validation error ordering can make tests brittle, and a shared test repository can affect multiple suites if it diverges from expected behavior.

### Steps
- [x] Add bean validation support and annotate employee request DTOs with constraints aligned to domain rules.
- [x] Update the REST controller to trigger validation and return consistent 400 error payloads.
- [x] Extract a shared in-memory employee repository for application tests and update the affected suites.

### Verification
- [x] Invalid employee create and list requests return 400 before reaching use-case logic.
- [x] Application tests use the shared in-memory repository instead of per-file fake implementations.
- [x] Targeted tests pass.

## Task: Unify validation rules and isolate validation tests

### Context
- What: Share employee validation constants between the domain and request DTOs, and move validation-focused HTTP checks into their own integration suite.
- Why: Validation messages should not drift across layers, and controller tests are easier to maintain when validation contracts are isolated from auth, pagination, and docs checks.
- Risk: Over-refactoring could change existing error messages or fragment exception handling if the shared constants and test split are not done consistently.

### Steps
- [x] Extract shared employee validation constants and reuse them in the domain model and request DTO annotations.
- [x] Move validation-specific HTTP assertions into a dedicated controller validation integration test suite.
- [x] Verify whether custom application exception classes add value beyond the current domain and framework exception handling.

### Verification
- [x] Domain and DTO validation messages for employee fields come from one shared source.
- [x] Validation-specific controller tests pass independently from the broader controller suite.
- [x] The exception-layer decision is documented in the final assessment.

## Task: Implement GitHub-only CI quality gates

### Context
- What: Add a GitHub Actions CI workflow and Gradle quality gates for coverage, security, maintainability, and bug detection.
- Why: The repository currently has only a plan-enforcement workflow and no automated code-quality guardrails for pull requests.
- Risk: Static-analysis defaults can create noisy failures unless the build is configured with focused rules and executable verification scripts.

### Steps
- [x] Add Gradle quality plugins and configure the baseline 95% total coverage gate.
- [x] Add repository verification scripts, including a changed-line coverage check enforcing 98% on PR code.
- [x] Add GitHub Actions workflow(s) to run dependency review and the quality gates on pull requests and `main`.

### Verification
- [x] `./scripts/verify.sh` runs locally and fails the build on coverage or static-analysis violations.
- [x] The PR workflow enforces dependency review and 98% changed-line coverage.
- [x] Generated reports are published as CI artifacts for triage.

## Task: Standardize Flyway migration naming

### Context
- What: Move Flyway SQL migrations to a timestamp-first naming convention.
- Why: Migration files should sort chronologically by filename and remain human-readable without relying on `V<number>` sequencing.
- Risk: Renaming the existing baseline migration changes Flyway history expectations for any already-initialized local databases.

### Steps
- [x] Configure Flyway to read timestamp-based versioned migrations.
- [x] Rename the existing SQL migration to the new timestamp-based format.
- [x] Add tooling and documentation so new migrations follow the same convention consistently.

### Verification
- [ ] The migration directory passes the naming validation in `./scripts/verify.sh`.
- [ ] The application can still start Flyway successfully against a clean database with the renamed migration set.
- [ ] The diff documents the required local database reset for developers with existing Flyway history.

## Task: Seed more than 100000 employees

### Context
- What: Add a Flyway migration that inserts a large employee dataset for development and load testing.
- Why: The database needs a realistic high-volume baseline without relying on manual scripts after startup.
- Risk: Bulk inserts can be slow or generate invalid UUID/email values if the SQL is not deterministic.

### Steps
- [x] Add a timestamped Flyway migration that inserts more than 100000 employees.
- [x] Keep the seeded departments capped at 50 distinct values.
- [x] Validate the migration naming and SQL against the dev PostgreSQL instance.

### Verification
- [x] The new migration filename matches the repository timestamp convention.
- [x] The SQL executes successfully on PostgreSQL.
- [x] The seed data creates more than 100000 employees while using no more than 50 departments.

## Task: Humanize seeded employees and add k6 coverage

### Context
- What: Make the large employee seed dataset look more realistic and add a k6 scenario that exercises the employee API against that dataset.
- Why: More human-readable names and department labels improve demos and manual verification, while k6 coverage gives the seeded dataset an immediate load-testing use case.
- Risk: Follow-up data migrations must stay deterministic, and the k6 script has to match the authenticated API contract and context path exactly.

### Steps
- [x] Add a follow-up Flyway migration that rewrites seeded names and department labels into realistic values.
- [x] Add a k6 script that mixes paginated reads with create/delete churn against `/api/v1/employees`.
- [x] Validate the new migration and smoke-test the k6 script against the dev stack.

### Verification
- [x] The live seed data uses realistic names and still caps departments at 50 distinct values.
- [x] The new Flyway migration applies cleanly on PostgreSQL.
- [x] The k6 script runs successfully against the dev stack with the configured API key.

## Task: Wire k6 remote write for Grafana

### Context
- What: Add a repeatable way to run the employee k6 script with Prometheus remote write enabled.
- Why: The Grafana k6 dashboard only populates when k6 exports metrics to Prometheus with a unique `testid` tag.
- Risk: If the wrapper and docs drift from the actual dev stack ports or auth defaults, stress runs will succeed locally but not appear in Grafana.

### Steps
- [x] Add a wrapper script that runs the employee k6 scenario with Prometheus remote write and a `testid` tag.
- [x] Document what the remote-write environment variables do and how to use the wrapper.
- [x] Execute one tagged run and verify Prometheus received the k6 metrics.

### Verification
- [x] The wrapper runs the employee stress script with `-o experimental-prometheus-rw` and a unique `testid`.
- [x] Prometheus exposes k6 metrics for the tagged run.
- [x] Grafana can filter the run by `testid` on the k6 dashboard.

