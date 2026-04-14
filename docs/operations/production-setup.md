# Production Setup

## Summary

The Employees API supports multiple runtime profiles so engineers can move from local development to production-like rehearsal without changing the application architecture. Production remains the hardened deployment target; the internal Docsify portal is not part of that runtime surface.

## Profile Matrix

| Profile | Main purpose | Notes |
| --- | --- | --- |
| `local` | Developer machine | PostgreSQL + API + docs portal via Compose |
| `dev` | Shared development workflow | Full observability and docs portal |
| `stage` | Production rehearsal | Observability stack, no docs portal |
| `prod` | Hardened deployment | Restricted ports, resource limits, externalized secrets |
| `test` | Automated verification | H2 in-memory database |

## Production Principles

- No hardcoded production secrets.
- Runtime services communicate over internal Docker networking.
- Health checks and restart policies are enabled.
- Resource limits are declared for safer operations.
- Swagger UI is disabled or restricted according to the production profile strategy.

## Required Environment Values

| Variable | Why it matters |
| --- | --- |
| `DATABASE_HOST` / `DATABASE_URL` | Database connectivity |
| `DATABASE_USER` | Application database account |
| `DATABASE_PASSWORD` | Secret credential, no safe default |
| `API_KEY` | Business endpoint authentication |
| `JAEGER_ENDPOINT` | Trace export target when enabled |
| `GRAFANA_PASSWORD` | Admin access for dashboards |

## Runtime Checklist

1. Start from a clean image build using Compose.
2. Verify PostgreSQL health before the app starts.
3. Confirm Flyway applies all migrations cleanly.
4. Check `/api/actuator/health` and `/api/actuator/prometheus`.
5. Confirm traces and metrics arrive in Jaeger and Prometheus.

## Verification Commands

```bash
cd docker/prod
docker compose --env-file .env.prod up --build -d
docker compose ps
docker compose logs -f app
```

## Related Pages

- [Docker Workflow](/operations/docker.md)
- [Architecture Overview](/architecture/overview.md)
