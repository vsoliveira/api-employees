# Docker Environments Guide

This document describes how to run the Employees API in different environments using Docker Compose.

## Quick Start

### Prerequisites
- Docker and Docker Compose installed
- Java 21+ (for building the application)
- Gradle wrapper

### Build the Application

Before running any Docker environment, build the application:

```bash
./gradlew bootJar
```

This creates `build/libs/employees-api-*.jar` which is copied into the Docker image.

## Environment Profiles

The application supports multiple Spring profiles for different environments:

| Profile | Usage | Database | Observability |
|---------|-------|----------|-----------------|
| `local` | Local development without Docker | PostgreSQL on localhost | Disabled |
| `dev` | Docker-based development | PostgreSQL in container | Prometheus, Grafana, Jaeger |
| `stage` | Staging/pre-production | PostgreSQL in container | Prometheus, Grafana, Jaeger |
| `prod` | Production | PostgreSQL in container | Prometheus, Grafana, Jaeger (no exposed ports) |
| `test` | Unit tests (H2 in-memory) | H2 | Disabled |

## Local Development (No Docker)

For development with PostgreSQL running locally on your machine.

### Prerequisites
- PostgreSQL 15+ running on localhost:5432
- Database: `employees_db`
- User: `postgres`
- Password: `postgres`

### Run the Application

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### Access the API
- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- Health: http://localhost:8080/api/actuator/health

## Development Environment (Docker)

Complete Docker-based development with full observability stack.

### Run

```bash
cd docker/dev
docker-compose --env-file .env.dev up -d
```

Or using the included .env file:
```bash
cd docker/dev
docker-compose up -d
```

### Services

| Service | URL | Credentials |
|---------|-----|-------------|
| **API** | http://localhost:8080/api | - |
| **Grafana** | http://localhost:3000 | admin/admin |
| **Prometheus** | http://localhost:9090 | - |
| **Jaeger Traces** | http://localhost:16686 | - |
| **PostgreSQL** | localhost:5432 | postgres/postgres |

### Verify Startup

Check that all services are healthy:

```bash
docker-compose ps
```

All containers should show `Up` status with `healthy` health check.

### Access the API

```bash
# Swagger UI
curl http://localhost:8080/api/swagger-ui.html

# Health check
curl http://localhost:8080/api/actuator/health

# Metrics
curl http://localhost:8080/api/actuator/prometheus
```

### View Metrics in Grafana

1. Open http://localhost:3000
2. Login with `admin/admin`
3. Go to **Dashboards** → **Employees API - Metrics**

The dashboard shows:
- JVM Memory Usage
- HTTP Requests Count
- System metrics

### View k6 Stress Runs in Grafana

1. Start a tagged k6 run that exports metrics to Prometheus:

```bash
./scripts/run-k6-prometheus.sh employees-dashboard-smoke
```

You can override the main k6 workload values inline before the command:

| Value | What It Is | Sample Value | Override Example |
|-------|------------|--------------|------------------|
| `BROWSE_RATE` | Target read request rate per second for paginated employee listing. | `8` | `BROWSE_RATE=8 ./scripts/run-k6-prometheus.sh employees-dashboard-smoke` |
| `BROWSE_PRE_ALLOCATED_VUS` | Initial VUs reserved for the browse scenario before k6 scales further. | `3` | `BROWSE_PRE_ALLOCATED_VUS=3 ./scripts/run-k6-prometheus.sh employees-dashboard-smoke` |
| `BROWSE_MAX_VUS` | Hard cap for browse-scenario VUs when the arrival rate increases. | `10` | `BROWSE_MAX_VUS=10 ./scripts/run-k6-prometheus.sh employees-dashboard-smoke` |
| `BROWSE_STAGE_ONE_DURATION` | Ramp-up duration for the first browse stage. | `5s` | `BROWSE_STAGE_ONE_DURATION=5s ./scripts/run-k6-prometheus.sh employees-dashboard-smoke` |
| `BROWSE_STAGE_TWO_DURATION` | Steady-state duration for the main browse load stage. | `10s` | `BROWSE_STAGE_TWO_DURATION=10s ./scripts/run-k6-prometheus.sh employees-dashboard-smoke` |
| `BROWSE_STAGE_THREE_DURATION` | Ramp-down duration for the final browse stage. | `5s` | `BROWSE_STAGE_THREE_DURATION=5s ./scripts/run-k6-prometheus.sh employees-dashboard-smoke` |
| `WRITE_VUS` | Concurrent virtual users for create/delete churn. | `1` | `WRITE_VUS=1 ./scripts/run-k6-prometheus.sh employees-dashboard-smoke` |
| `WRITE_DURATION` | Total duration of the create/delete scenario. | `10s` | `WRITE_DURATION=10s ./scripts/run-k6-prometheus.sh employees-dashboard-smoke` |
| `WRITE_GRACEFUL_STOP` | Extra shutdown window for in-flight write iterations to finish cleanly. | `5s` | `WRITE_GRACEFUL_STOP=5s ./scripts/run-k6-prometheus.sh employees-dashboard-smoke` |
| `PAGE_SIZE` | Employee page size used by the browse scenario. | `100` | `PAGE_SIZE=100 ./scripts/run-k6-prometheus.sh employees-dashboard-smoke` |
| `BASE_URL` | Base API URL used by k6. | `http://localhost:8080/api` | `BASE_URL=http://localhost:8080/api ./scripts/run-k6-prometheus.sh employees-dashboard-smoke` |
| `API_KEY` | API key sent in the `X-API-Key` header for protected endpoints. | `dev-api-key` | `API_KEY=dev-api-key ./scripts/run-k6-prometheus.sh employees-dashboard-smoke` |

Example with several overrides at once:

```bash
BROWSE_RATE=12 \
BROWSE_PRE_ALLOCATED_VUS=4 \
BROWSE_MAX_VUS=12 \
WRITE_VUS=2 \
WRITE_DURATION=20s \
./scripts/run-k6-prometheus.sh employees-dashboard-tuned
```

2. Open http://localhost:3000
3. Login with `admin/admin`
4. Go to **Dashboards** → **k6 Stress Overview**
5. In the `testid` filter, select the run id you used, for example `employees-dashboard-smoke`
6. Optionally narrow the `scenario` filter to `browseEmployees` or `createAndDeleteEmployees`

The k6 dashboard shows:
- Active virtual users and configured max VUs
- HTTP request rate and iteration rate
- Error rate
- Average, p95, p99, and max request latency
- Throughput split by scenario and HTTP status

If the run does not appear immediately, wait a few seconds and refresh the dashboard. The wrapper sends metrics to Prometheus using remote write and tags every series with the selected `testid`.

### View Traces in Jaeger

1. Open http://localhost:16686
2. Select service `employees-api-dev`
3. View distributed traces

### Clean Up

```bash
cd docker/dev
docker-compose down -v
```

This removes all containers and volumes.

## Staging Environment (Docker)

Production-like setup for pre-release testing.

### Run

```bash
cd docker/stage
docker-compose --env-file .env.stage up -d
```

### Configuration

Same stack as dev, but configured for staging conditions:
- Longer Prometheus retention (30 days)
- Container restart on failure
- Production-like logging (WARN level)

### Stop

```bash
cd docker/stage
docker-compose down -v
```

## Production Environment (Docker)

Hardened, secure configuration for production deployment.

### Key Characteristics

- **No exposed debug ports** - PostgreSQL and monitoring tools accessible only from within the network
- **Security hardened** - Uses `no-new-privileges` security options
- **Resource limits** - Memory and CPU limits defined
- **Restart policies** - All services auto-restart on failure
- **Longer retention** - Prometheus stores metrics for 90 days
- **Health checks** - All services monitored for health
- **Secrets management** - Environment variables for sensitive data (DATABASE_PASSWORD, API_KEY, etc.)

### Prerequisites

Create `.env.prod` with actual production values:

```bash
cd docker/prod
cp .env.prod .env.prod.secret

# Edit with your actual values
nano .env.prod.secret
```

Required variables:
```env
POSTGRES_PASSWORD=<strong-password>
DATABASE_PASSWORD=<strong-password>
API_KEY=<long-random-key>
GRAFANA_PASSWORD=<strong-password>
GRAFANA_URL=https://monitoring.yourdomain.com
```

### Run Behind Reverse Proxy

Production setup does NOT expose ports directly. Use a reverse proxy (nginx, AWS ALB, etc.):

1. **Application** (port 8080) - exposed to reverse proxy only
2. **Grafana** (port 3000) - exposed to reverse proxy only
3. **Prometheus** (port 9090) - exposed to reverse proxy only

Example nginx configuration:
```nginx
upstream employees_api {
    server employees-api-prod:8080;
}

server {
    listen 443 ssl;
    server_name api.yourdomain.com;
    
    ssl_certificate /etc/ssl/certs/your-cert.crt;
    ssl_certificate_key /etc/ssl/private/your-key.key;
    
    location /api {
        proxy_pass http://employees_api;
    }
    
    # Monitoring access restricted to VPN
    location /monitoring {
        auth_request /auth;
        proxy_pass http://grafana:3000;
    }
}
```

### Deploy

```bash
cd docker/prod
docker-compose --env-file .env.prod.secret up -d
```

### Monitor

```bash
# Check service health
docker-compose ps

# View logs
docker-compose logs -f app

# Access Prometheus
# Via reverse proxy: https://yourdomain.com/monitoring/prometheus

# Access Grafana
# Via reverse proxy: https://yourdomain.com/monitoring/grafana
```

### Stop

```bash
cd docker/prod
docker-compose down
```

Note: This does NOT remove volumes, so data persists.

## Database Migrations

All environments use **Flyway** for database schema management.

Migrations are located in: `src/main/resources/db/migration/`

Flyway automatically:
1. Creates the migration table on first run
2. Applies pending migrations in order
3. Validates currently applied migrations
4. Prevents deploy if a migration has changed

### Creating New Migrations

1. Create a new file with the timestamp-based naming pattern: `src/main/resources/db/migration/YYYYMMDD_HHMMSS__description.sql`
   - Example: `20260414_143000__add_employee_salary.sql`
   - The date and time stay in ascending order, so file names read from oldest to newest.
   - Use `./scripts/create-migration.sh "add employee salary"` to generate the file name automatically.

2. Write SQL:
```sql
ALTER TABLE employees ADD COLUMN salary DECIMAL(10, 2);
```

3. Migrations run automatically on application startup
4. If you already ran older `V...` migrations locally, reset the local database volume before starting the app on this branch so Flyway can rebuild its schema history with the new names.

## Unit Testing

Unit tests use H2 in-memory database. No Docker required.

### Run Tests

```bash
./gradlew test
```

### Test Configuration

- Profile: `test` (implicit via @SpringBootTest)
- Database: H2 in-memory
- DDL: `create-drop` (schema created/destroyed per test)
- Flyway: Disabled (schema created by Hibernate)

See `src/test/resources/application.yml` for test configuration.

## Troubleshooting

### Container won't start

Check logs:
```bash
docker-compose logs app
```

Common issues:
- PostgreSQL not yet healthy - wait a moment and check with `docker-compose ps`
- Port already in use - change `APP_PORT` in .env file
- JAR not built - run `./gradlew bootJar`

### Can't connect to API

Verify the service is running:
```bash
curl http://localhost:8080/api/actuator/health
```

If not accessible, check:
1. Docker container is running: `docker-compose ps`
2. Port mapping is correct: `docker-compose port app`
3. Network is properly created: `docker network ls`

### Database connection errors

Check PostgreSQL is healthy:
```bash
docker-compose logs postgres
docker-compose exec postgres psql -U postgres -d employees_db -c "SELECT 1"
```

### Metrics not appearing in Prometheus

1. Verify API is exposing metrics: `curl http://localhost:8080/api/actuator/prometheus`
2. Check Prometheus scrape config: `docker-compose exec prometheus cat /etc/prometheus/prometheus.yml`
3. Wait 15+ seconds for first scrape to complete
4. Check Prometheus targets: http://localhost:9090/targets

## Advanced Configuration

### Scale Horizontally

Run multiple API instances:
```bash
docker-compose up -d --scale app=3
```

Note: Requires load balancer in front for traffic distribution.

### Custom Environment Variables

Override defaults:
```bash
cd docker/dev
DATABASE_USER=myuser DATABASE_PASSWORD=mypass docker-compose up -d
```

Or create a custom .env file:
```bash
cp .env.dev .env.custom
nano .env.custom
docker-compose --env-file .env.custom up -d
```

### Persistent Configuration Backup

Save Docker named volumes:
```bash
docker run --rm -v postgres_data_dev:/data -v $(pwd):/backup alpine tar czf /backup/postgres-backup.tar.gz -C /data .
```

## Security Considerations

1. **Secrets Management**
   - Never commit .env files with real passwords
   - Use secret management tools in production (AWS Secrets Manager, HashiCorp Vault, etc.)

2. **Network Security**
   - Production uses internal networks only
   - Place reverse proxy/load balancer in front
   - Enable SSL/TLS at reverse proxy layer

3. **Database**
   - Change default PostgreSQL password in production
   - Use strong passwords (20+ characters, mixed case, symbols)
   - Rotate credentials regularly

4. **API Keys**
   - Use long, random API keys (32+ characters)
   - Store in secure key management system
   - Rotate periodically

5. **Access Control**
   - Restrict monitoring dashboard access (Grafana, Prometheus)
   - Use VPN or network segmentation for operational access
   - Enable authentication on all web interfaces

## References

- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [PostgreSQL Docker Image](https://hub.docker.com/_/postgres)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/grafana/latest/)
- [Jaeger Tracing Documentation](https://www.jaegertracing.io/docs/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Flyway Documentation](https://flywaydb.org/documentation/)
