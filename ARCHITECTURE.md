# Architecture Documentation

## Overview

The Employees API is a production-grade Spring Boot microservice implementing Hexagonal Architecture (Ports & Adapters) with Domain-Driven Design principles. This document describes the system architecture, environment configuration, and deployment strategy.

## System Architecture

### Architectural Pattern: Hexagonal Architecture (Ports & Adapters)

The application is organized into three core layers:

```
┌─────────────────────────────────────────┐
│         External Systems                │
│  (REST Client, PostgreSQL, OpenAPI)     │
└─────────────┬───────────────────────────┘
              │ (Adapters)
┌─────────────────────────────────────────┐
│      Infrastructure Layer                │
│  - EmployeeJpaRepository                │
│  - EmployeeController (REST)            │
│  - ApiKeyAuthenticationFilter           │
│  - EmployeeEntity (JPA)                 │
└─────────────┬───────────────────────────┘
              │ (Ports)
┌─────────────────────────────────────────┐
│    Application Layer (Use Cases)        │
│  - CreateEmployeeUseCase                │
│  - ListEmployeesUseCase                 │
│  - DeleteEmployeeUseCase                │
└─────────────┬───────────────────────────┘
              │ (Interfaces)
┌─────────────────────────────────────────┐
│      Domain Layer (Business Logic)      │
│  - Employee (Aggregate Root)            │
│  - EmployeeRepository (Port)            │
│  - Domain Exceptions                    │
│  - Validation Rules                     │
└─────────────────────────────────────────┘
```

### Benefits of This Architecture

- **Independence**: Domain logic is isolated from frameworks
- **Testability**: Core business logic tested without infrastructure
- **Flexibility**: Easy to switch implementations (e.g., MongoDB → PostgreSQL)
- **Clarity**: Clear separation of concerns
- **Maintainability**: Changes in one layer don't affect others

## Environment Configuration Strategy

### Multi-Environment Design

The application supports four distinct Spring profiles, each targeting specific deployment scenarios:

```
┌──────────────────────────────────────────────────┐
│         Environment Strategy                     │
├──────────────────────────────────────────────────┤
│ Profile  │ Use Case    │ Database    │ Container │
├──────────┼─────────────┼─────────────┼───────────┤
│ local    │ Dev Machine │ Postgres    │ Native   │
│ dev      │ Team Dev    │ Postgres    │ Docker   │
│ stage    │ Pre-Prod    │ Postgres    │ Docker   │
│ prod     │ Production  │ Postgres    │ Docker   │
│ test     │ Unit Tests  │ H2          │ None     │
└──────────────────────────────────────────────────┘
```

### Profile-Specific Configuration

#### 1. Local Profile (`application-local.yml`)
- **Usage**: Local development without Docker
- **Database**: PostgreSQL on localhost:5432
- **Credentials**: postgres/postgres (hardcoded for convenience)
- **Observability**: Disabled
- **Logging**: DEBUG level for com.company
- **Flyway**: Enabled with baselineOnMigrate

#### 2. Development Profile (`application-dev.yml`)
- **Usage**: Docker-based team development
- **Database**: PostgreSQL in Docker (postgres:5432 from within container)
- **Credentials**: postgres/postgres
- **Observability**: 
  - Jaeger tracing enabled (http://jaeger:14250)
  - Prometheus metrics enabled
  - Grafana dashboards provisioned
- **Logging**: DEBUG level for com.company, SQL statements visible
- **Connection Pooling**: 10 max, 5 min
- **Flyway**: Enabled

#### 3. Staging Profile (`application-stage.yml`)
- **Usage**: Pre-production testing
- **Database**: PostgreSQL in Docker (configurable via ENV)
- **Credentials**: Externalized via environment variables
- **Observability**: Full stack (same as dev)
- **Logging**: WARN level (production-like)
- **Connection Pooling**: 20 max, 10 min (higher than dev)
- **Flyway**: Enabled

#### 4. Production Profile (`application-prod.yml`)
- **Usage**: Production deployment
- **Database**: PostgreSQL in Docker (configurable via ENV)
- **Credentials**: REQUIRED via environment variables (no defaults)
- **Observability**: Full stack but no exposed ports
- **Logging**: WARN level for all, INFO for com.company
- **Connection Pooling**: 50 max, 20 min (high capacity)
- **Flyway**: Enabled with validation
- **Security**: 
  - Swagger UI disabled
  - Health details only for authorized requests
  - Error messages minimal (no stack traces)
  - HTTP compression enabled
- **Performance Optimizations**:
  - SQL format_sql disabled
  - Hibernate batch size: 20
  - Fetch size: 50
  - Order inserts/updates enabled

#### 5. Test Profile (implicit, `test/resources/application.yml`)
- **Usage**: Unit and integration tests (@DataJpaTest, @SpringBootTest)
- **Database**: H2 in-memory
- **DDL Mode**: create-drop (fresh schema per test)
- **Flyway**: Disabled (schema created by Hibernate)
- **Observability**: Disabled
- **H2 Console**: Enabled for debugging tests

## Externalized Configuration

### Environment Variables

All sensitive and environment-specific values use Spring property placeholders:

```yaml
# application.yml (base defaults)
spring:
  datasource:
    url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/employees_db}
    username: ${DATABASE_USER:postgres}
    password: ${DATABASE_PASSWORD:postgres}

api:
  key: ${API_KEY:default-api-key}
```

### Environment Variable Mapping

| Variable | Purpose | Default | Production Required |
|----------|---------|---------|---------------------|
| DATABASE_URL | PostgreSQL JDBC URL | localhost:5432 | Yes |
| DATABASE_USER | PostgreSQL user | postgres | Yes |
| DATABASE_PASSWORD | PostgreSQL password | postgres | **Yes** |
| API_KEY | API authentication key | defaults/per-profile | **Yes** |
| JAEGER_GRPC_ENDPOINT | Distributed tracing | http://localhost:14250 | Optional |
| JAEGER_ENDPOINT | Jaeger endpoint (alt) | http://jaeger:14250 | Optional |

### .env Files

Each Docker environment includes a `.env` file:

```
docker/
├── local/     .env.local       (for local docker setup)
├── dev/       .env.dev         (for dev team)
├── stage/     .env.stage       (for staging)
└── prod/      .env.prod        (for production - keep secure!)
```

**Security Rule**: Never commit `.env.prod` with real secrets. Use `git-crypt`, `sealed-secrets`, or `HashiCorp Vault`.

## Database Architecture

### PostgreSQL as Primary Database

All non-test environments use PostgreSQL 15+:

- **Connection Pooling**: HikariCP with profile-specific settings
- **Dialect**: PostgreSQL
- **Migrations**: Flyway handles schema versioning
- **Validation**: DDL mode set to `validate` (no automatic schema creation)

### Migration Strategy (Flyway)

- **Location**: `src/main/resources/db/migration/`
- **Naming**: `YYYYMMDD_HHMMSS__description.sql`
- **Example**: `20260414_143000__create_employees_table.sql`
- **Execution**: Automatic on application startup
- **Validation**: Checksums prevent accidental modifications
- **Ordering**: Timestamp prefixes sort chronologically from oldest migration to newest migration
- **Baseline**: Enabled for existing schemas

### H2 for Testing Only

H2 in-memory database used exclusively for:
- Unit tests (@DataJpaTest)
- Integration tests (@SpringBootTest)
- Local test execution (`./gradlew test`)

**Why H2 for Tests?**
- Fast (in-memory, no I/O)
- Isolated (creates/destroys per test)
- No external dependencies
- Complete schema control via Hibernate

## Observability Stack

### Metrics Collection (Prometheus)

**What's Monitored**:
- JVM metrics (memory, GC, threads)
- HTTP request metrics (latency, status codes, counts)
- Database connection pool metrics
- Custom application metrics

**Retention**: 
- Dev/Stage: 30 days
- Prod: 90 days

**Scrape Config**: 
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
3. ./gradlew bootJar (build container image)
   ↓
4. cd docker/dev && docker-compose up (verify)
   ↓
5. Integration testing (real PostgreSQL)
   ↓
6. Commit & Push
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
5. Deploy: docker-compose -f docker/stage/docker-compose.yml up
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
   docker-compose -f docker/prod/docker-compose.yml \
     --env-file /etc/employees-api/.env.prod up
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
docker-compose up --scale app=3
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
docker-compose up
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
3. `docker-compose up` (redeploy)

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
