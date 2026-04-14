# Docker Workflow

## Purpose

This project uses Docker Compose to run the Employees API together with its supporting services. The internal docs portal is delivered as a separate Docsify container for local and development workflows.

## Profiles Covered By Compose

| Environment | Compose directory | Includes docs portal |
| --- | --- | --- |
| Local | `docker/local` | Yes |
| Development | `docker/dev` | Yes |
| Stage | `docker/stage` | No |
| Production | `docker/prod` | No |

The docs portal is intentionally omitted from stage and production because it is for internal engineering use only.

## Quick Start

### Development stack

```bash
cd docker/dev
docker compose up --build -d
```

### Local stack

```bash
cd docker/local
docker compose up --build -d
```

## Service Endpoints

### Development

| Service | URL |
| --- | --- |
| Docs portal | http://localhost:3200 |
| API | http://localhost:8080/api |
| Grafana | http://localhost:3000 |
| Prometheus | http://localhost:9090 |
| Jaeger | http://localhost:16686 |
| PostgreSQL | localhost:5432 |

### Local

| Service | URL |
| --- | --- |
| Docs portal | http://localhost:3200 |
| API | http://localhost:8080/api |
| PostgreSQL | localhost:5432 |

## Docs Container Details

The docs container is built from `docs/Dockerfile` and serves the `docs/` directory through nginx. The site is static and intentionally independent from the API container, so engineers can browse documentation even if the application is stopped.

```bash
cd docker/dev
docker compose up --build -d docs
```

## Verification Checklist

```bash
cd docker/dev
docker compose ps
curl http://localhost:3200
curl http://localhost:8080/api/actuator/health
curl http://localhost:8080/api/actuator/prometheus
```

Expected result:

- `docs` returns the Docsify shell HTML.
- `app` reports `UP` through Actuator.
- Prometheus metrics are exposed from the API container.

## Common Commands

### Stop the stack

```bash
docker compose down -v
```

### Rebuild only the docs portal

```bash
docker compose build docs
docker compose up -d docs
```

### Inspect logs

```bash
docker compose logs -f docs
docker compose logs -f app
```

## Relationship To The Live API Docs

The Docsify portal is not a replacement for OpenAPI. Use them together:

- Docsify: curated internal engineering guidance and workflow documentation.
- Swagger UI: live interactive contract exploration.
- OpenAPI JSON: generated machine-readable API schema.
