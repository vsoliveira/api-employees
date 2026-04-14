# Architecture Documentation

This file remains as a compatibility entrypoint.

The maintained architecture guide now lives in [docs/architecture/overview.md](docs/architecture/overview.md).

For the navigable internal portal, start the docs container from `docker/dev` or `docker/local` and open `http://localhost:3200`.
- Interval: 5s (dev), 15s (prod)
- Timeout: 10s

### Distributed Tracing (Jaeger)

**Enabled in**: dev, stage, prod

**captured**:
- HTTP request flows through layers
- Database query execution
- Service-to-service calls (future microservices)

**UI**: Jaeger UI on port 16686 (dev/stage only)

### Visualization (Grafana)

**Dashboards Included**:
- **Employees API - Metrics**: JVM memory, HTTP requests
- (Extensible for custom metrics)

**Data Sources**:
- Prometheus

**Access Control**:
- Dev/Stage: Exposed on port 3000, user admin/admin
- Prod: No exposed port, access via reverse proxy with auth

## Docker Containerization

### Dockerfile Strategy

```dockerfile
FROM eclipse-temurin:21-jre-alpine

# Key aspects:
- Alpine base (small, secure)
- Java 21 runtime
- Non-root user (appuser)
- Health checks enabled
- curl for diagnostics
```

### Docker Compose Orchestration

Each environment has its own `docker-compose.yml`:

| Environment | Services | Purpose |
|-------------|----------|---------|
| local/ | postgres | Local dev (no container for app) |
| dev/ | postgres, prometheus, grafana, jaeger, app | Full dev stack |
| stage/ | postgres, prometheus, grafana, jaeger, app | Pre-prod |
| prod/ | postgres, prometheus, grafana, jaeger, app | Production |

### Container Networking

All services communicate via Docker bridge network `employees-network`:

```
┌─────────────────────────────────────────┐
│      employees-network (bridge)         │
├─────────────────────────────────────────┤
│ ┌──────────┐ ┌──────────┐ ┌──────────┐ │
│ │ postgres │ │   app    │ │prometheus│ │
│ │ :5432    │ │  :8080   │ │  :9090   │ │
│ └──────────┘ └──────────┘ └──────────┘ │
│      ↑              │          ↑       │
│      └──────────────┴──────────┘       │
│           Internal DNS                 │
│  (postgres, app, prometheus)          │
└─────────────────────────────────────────┘
```

**Benefits**:
- Services resolved by name (postgres, not localhost)
- Isolated from host network (security)
- Easy service discovery
- No port conflicts between environments

### Security Hardening

**Production (`prod/`):**
- No exposed debug ports (postgres, monitoring tools)
- `no-new-privileges` security option on all containers
- Resource limits (2 CPU, 2GB memory)
- Restart policies (`always`)
- Health checks on all services
- Secrets via environment variables

**Development (`dev/`):**
- All ports exposed for debugging
- Health checks enabled
- Verbose logging

## API Gateway & Reverse Proxy

### Local/Dev (No Gateway)
Direct access to application:
- API: http://localhost:8080/api
- Swagger: http://localhost:8080/api/swagger-ui.html

### Production (Behind Reverse Proxy)

```
Internet
    │
    ↓ (HTTPS)
┌─────────────────┐
│   Load Balancer │
│  (AWS ALB/NLB)  │
└────────┬────────┘
         │ (HTTP)
┌───────────────────────────────┐
│     Reverse Proxy (nginx)     │
├───────────────────────────────┤
│ /api              → app:8080  │
│ /monitoring       → grafana   │
│ /metrics          → prometheus│
└───────────────────────────────┘
         │
    ┌────┼────┬───────┬─────────┐
    ↓    ↓    ↓       ↓         ↓
  app  postgres prometheus grafana jaeger
         (internal network only)
```

**Benefits**:
- Single entry point
- HTTPS termination (reverse proxy)
- API routing
- Monitoring access with authentication
- DDoS protection
- Rate limiting

## Deployment Pipeline

### Development Workflow

```
1. Write Code
   ↓
2. ./gradlew test (H2 tests)
   ↓
3. cd docker/dev && docker compose up --build (build app image and start stack)
   ↓
4. Integration testing (real PostgreSQL + sidecars)
   ↓
5. Commit & Push
```

### Stage Deployment

```
1. Code merged to main
   ↓
2. CI/CD build & test
   ↓
3. Build container image
   ↓
4. Push to registry (docker.company.com)
   ↓
5. Deploy: docker compose -f docker/stage/docker-compose.yml up --build
   ↓
6. Smoke tests
   ↓
7. Ready for production
```

### Production Deployment

```
1. Approve stage changes
   ↓
2. Deploy with secrets management:
   docker compose -f docker/prod/docker-compose.yml \
     --env-file /etc/employees-api/.env.prod up --build
   ↓
3. Blue-green deployment:
   - Start new version (blue)
   - Verify healthy
   - Switch traffic (green → blue)
   - Keep old version (blue) for rollback
   ↓
4. Monitor dashboards (Grafana)
   ↓
5. Alert on issues (Prometheus alerts)
```

## Performance Characteristics

### Baseline (Single Instance)

**System**: Docker on laptop (4 CPU, 8GB RAM)

| Metric | Value |
|--------|-------|
| Throughput | ~1,000 req/s |
| p50 Latency | ~5ms |
| p99 Latency | ~50ms |
| Memory Usage | ~400MB |
| CPU Usage | ~30% (typical) |

### Scaling Strategy

**Horizontal Scaling** (add instances):
```bash
docker compose up --scale app=3
```

**Load Balancing** Options:
- Docker Swarm mode (native)
- Kubernetes (production grade)
- AWS ECS (cloud native)
- HAProxy (self-hosted)

## Security Architecture

### API Authentication

**Mechanism**: API Key in HTTP Header

```bash
curl -H "X-API-Key: your-api-key" http://localhost:8080/api/employees
```

**Implementation**: `ApiKeyAuthenticationFilter`
- Validates X-API-Key header
- Rejects requests without valid key
- Returns 401 Unauthorized

**Rotation**: 
- Change API_KEY environment variable
- Restart application
- Old tokens immediately invalid

### Network Security

**Development**: Single bridge network (employees-network)
**Production**: 
- Private VPC
- Security groups restrict access
- Database accessible only from app container
- Monitoring accessible only from internal network

### Data Security

- **In Transit**: HTTPS via reverse proxy
- **At Rest**: PostgreSQL encryption (optional)
- **Backups**: Encrypted, separate location

### Secret Management (Production)

**Recommended Tools**:
- AWS Secrets Manager
- HashiCorp Vault
- Kubernetes Secrets
- Docker Secrets (Swarm mode)

**Pattern**:
```bash
# Don't do this in production:
DATABASE_PASSWORD=somehardcodedpass

# Do this instead:
export DATABASE_PASSWORD=$(aws secretsmanager get-secret-value --secret-id employees-api-db-password)
docker compose up
```

## Monitoring & Alerting

### Key Metrics

1. **Availability**: HTTP 5xx error rate (target < 0.1%)
2. **Performance**: p99 latency (target < 100ms)
3. **Capacity**: CPU/Memory utilization (target < 80%)
4. **Reliability**: Request success rate (target > 99.9%)

### Alert Rules (Prometheus)

```yaml
# Example alerts to configure:
- name: "High Error Rate"
  expr: rate(http_server_requests_seconds_count{status=~"5.."}[5m]) > 0.01

- name: "High Latency"
  expr: histogram_quantile(0.99, http_server_requests_seconds_bucket) > 0.1

- name: "High Memory"
  expr: jvm_memory_used_bytes / jvm_memory_max_bytes > 0.8

- name: "Database Connection Pool"
  expr: hikaricp_connections_active / hikaricp_connections_max > 0.8
```

### Dashboard Maintenance

- Review metrics monthly
- Update alert thresholds based on patterns
- Add custom metrics for business KPIs
- Archive old dashboards

## Disaster Recovery

### Backup Strategy

**Database Backups**:
```bash
# Daily backup to object storage
docker exec employees-db-prod pg_dump -U postgres employees_db | \
  aws s3 cp - s3://backups/employees-api/$(date +%Y%m%d).sql.gz
```

**Volume Backups**:
```bash
# Backup PostgreSQL data volume
docker run --rm \
  -v postgres_data_prod:/data \
  -v /backups:/backup \
  alpine tar czf /backup/postgres-data-$(date +%Y%m%d).tar.gz -C /data .
```

### Recovery Procedures

**Application Recovery**:
1. Roll back deployment to previous version
2. Monitor for stability

**Database Recovery** (from backup):
1. Restore backup to staging
2. Verify data integrity
3. Promote to production (blue-green)
4. Monitor replication lag

**Container/Image Recovery**:
1. Pull previous image from registry
2. Update docker-compose.yml
3. `docker compose up --build` (redeploy)

## Documentation Standards

### Code Documentation
- Javadoc on public classes and methods
- Comments on complex business logic
- Explain the "why", not the "what"

### API Documentation
- OpenAPI 3.0 specs in code
- Swagger UI auto-generated
- Endpoint examples

### Runbook Documentation
- How to deploy each environment
- How to monitor health
- How to respond to alerts
- Escalation procedures

## Future Improvements

1. **Kubernetes Migration**: Helm charts for cloud-native deployment
2. **Multi-region**: Database replication, CDN
3. **API Versioning**: Support /api/v1, /api/v2
4. **Authentication**: OAuth2 / OpenID Connect
5. **Advanced Observability**: Distributed tracing for microservices
6. **Auto-scaling**: Based on CPU, memory, request rate
7. **Feature Flags**: Gradual rollouts, A/B testing

## References

- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design](https://www.domainlanguage.com/ddd/)
- [Spring Boot Best Practices](https://spring.io/projects/spring-boot)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Prometheus Monitoring](https://prometheus.io/docs/)
- [Grafana Dashboards](https://grafana.com/docs/grafana/latest/dashboards/)
- [Jaeger Distributed Tracing](https://www.jaegertracing.io/)
