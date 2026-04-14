# Architecture Overview

## Overview

The Employees API is a Spring Boot microservice built around hexagonal architecture and DDD boundaries. The system is organized to keep business rules in the domain, orchestrate use cases in the application layer, and isolate framework concerns in infrastructure adapters.

## Layering Model

```text
External Systems -> Infrastructure Adapters -> Application Use Cases -> Domain Model
```

| Layer | Main responsibilities | Typical elements |
| --- | --- | --- |
| Domain | Business rules | `Employee`, domain exceptions, repository ports |
| Application | Use-case orchestration and request/response contracts | create, list, delete use cases |
| Infrastructure | Framework integration and delivery adapters | REST controllers, JPA adapters, filters, entities |

## Runtime Boundaries

```text
REST Client / Swagger / Prometheus
            |
            v
Infrastructure: controllers, filters, JPA adapters
            |
            v
Application: create, list, delete employee use cases
            |
            v
Domain: aggregates, ports, validation rules
```

## Environment Strategy

| Profile | Use case | Database | Observability |
| --- | --- | --- | --- |
| `local` | Local development with Docker or native app run | PostgreSQL | Disabled by default |
| `dev` | Team development with full stack | PostgreSQL | Prometheus, Grafana, Jaeger |
| `stage` | Pre-production | PostgreSQL | Full stack |
| `prod` | Deployment | PostgreSQL | Full stack, restricted exposure |
| `test` | Automated tests | H2 in-memory | Disabled |

## Core Decisions

- Domain logic stays framework-agnostic.
- Spring Boot owns delivery, persistence, and operational integration.
- PostgreSQL is the runtime database for all non-test environments.
- Flyway manages schema evolution and seed data.
- Observability is part of the platform, not a later add-on.

## Operational Surfaces

| Surface | Path |
| --- | --- |
| Employee API | `http://localhost:8080/api/v1/employees` |
| Swagger UI | `http://localhost:8080/api/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/api/v1/api-docs` |
| Health | `http://localhost:8080/api/actuator/health` |
| Prometheus metrics | `http://localhost:8080/api/actuator/prometheus` |

## Persistence Model

- JPA is the persistence adapter.
- PostgreSQL is the source of truth outside tests.
- Flyway migrations live under `src/main/resources/db/migration/`.
- Test execution uses H2 for speed and isolation.

## Observability Stack

| Component | Purpose |
| --- | --- |
| Prometheus | Metrics collection and storage |
| Grafana | Dashboards for runtime and k6 metrics |
| Jaeger | Distributed tracing |
| Spring Actuator | Health, metrics, and operational endpoints |

## Security Notes

- Business endpoints remain protected by the API key filter.
- Documentation and sidecar-facing operational endpoints are intentionally reachable without the business API key where required by tooling.
- Production keeps sensitive credentials externalized through environment variables.

## Related Pages

- [Docker Workflow](/operations/docker.md)
- [Production Setup](/operations/production-setup.md)
- [k6 Stress Testing](/testing/k6.md)
