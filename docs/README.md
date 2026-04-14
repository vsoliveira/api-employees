# HUB Employees API

> Internal engineering documentation for the Employees API platform.

Use the language switcher in the top navigation to move between `en-US` and `pt-BR`.

<div class="callout-grid">
  <div class="callout-card">
    <strong>Architecture</strong>
    <p>Understand the hexagonal structure, environment strategy, and operational boundaries.</p>
    <a href="#/architecture/overview">Open architecture guide</a>
  </div>
  <div class="callout-card">
    <strong>Operations</strong>
    <p>Run the app, the observability stack, and the docs portal from Docker Compose.</p>
    <a href="#/operations/docker">Open Docker workflow</a>
  </div>
  <div class="callout-card">
    <strong>Testing</strong>
    <p>Review the k6 load profile, Prometheus export flow, and dashboard expectations.</p>
    <a href="#/testing/k6">Open k6 guide</a>
  </div>
</div>

## Quick Start

```bash
cd docker/dev
docker compose up --build -d
```

After startup, the main internal entry points are:

| Surface | URL | Notes |
| --- | --- | --- |
| Docs portal | http://localhost:3200 | Docsify container for internal docs |
| API | http://localhost:8080/api | Spring Boot app |
| Swagger UI | http://localhost:8080/api/swagger-ui.html | Live API exploration |
| OpenAPI | http://localhost:8080/api/v1/api-docs | Generated API contract |
| Actuator health | http://localhost:8080/api/actuator/health | Health and readiness |
| Prometheus | http://localhost:9090 | Metrics scrape and k6 remote write |
| Grafana | http://localhost:3000 | Dashboards |
| Jaeger | http://localhost:16686 | Trace inspection |

## Documentation Map

| Area | Canonical page | Purpose |
| --- | --- | --- |
| Architecture | [Architecture Overview](/architecture/overview.md) | System boundaries, layers, and deployment model |
| Operations | [Docker Workflow](/operations/docker.md) | Day-to-day startup, verification, and cleanup |
| Operations | [Production Setup](/operations/production-setup.md) | Environment model and hardening summary |
| Contributing | [AI Usage](/AI_USAGE.md) | How AI is used during development and documentation work |
| Testing | [k6 Stress Testing](/testing/k6.md) | Load profile and Prometheus export path |
| Contributing | [Maintaining the Docs Portal](/contributing/docs-portal.md) | How to update Docsify content and navigation |

## Principles

- Keep English source pages in the root docs tree and mirrored localized pages under `docs/pt-br/`.
- Treat the docs portal as internal infrastructure for engineers, not a public website.
- Prefer links to live surfaces such as Swagger UI and Actuator over copied screenshots.
- Update the sidebar and compatibility stubs whenever a page moves.

## Maintenance Flow

1. Edit or add Markdown under `docs/`.
2. Update `docs/_sidebar.md` and `docs/_navbar.md` when navigation changes.
3. Rebuild the docs container with `docker compose up --build -d docs` from `docker/dev` or `docker/local`.
4. Keep the compatibility stubs at the old paths pointing at the new canonical page.
