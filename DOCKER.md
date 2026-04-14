# Docker Environments Guide

This file remains as a compatibility entrypoint.

The maintained Docker workflow now lives in [docs/operations/docker.md](docs/operations/docker.md).

For the navigable internal portal, start the docs container from `docker/dev` or `docker/local` and open `http://localhost:3200`.

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
docker compose --env-file .env.prod.secret up --build -d
```

### Monitor

```bash
# Check service health
docker compose ps

# View logs
docker compose logs -f app

# Access Prometheus
# Via reverse proxy: https://yourdomain.com/monitoring/prometheus

# Access Grafana
# Via reverse proxy: https://yourdomain.com/monitoring/grafana
```

### Stop

```bash
cd docker/prod
docker compose down
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
docker compose logs app
```

Common issues:
- PostgreSQL not yet healthy - wait a moment and check with `docker compose ps`
- Port already in use - change `APP_PORT` in .env file
- Docker image is stale - rerun `docker compose up --build -d`

### Can't connect to API

Verify the service is running:
```bash
curl http://localhost:8080/api/actuator/health
```

If not accessible, check:
1. Docker container is running: `docker compose ps`
2. Port mapping is correct: `docker compose port app 8080`
3. Network is properly created: `docker network ls`

### Database connection errors

Check PostgreSQL is healthy:
```bash
docker compose logs postgres
docker compose exec postgres psql -U postgres -d employees_db -c "SELECT 1"
```

### Metrics not appearing in Prometheus

1. Verify API is exposing metrics: `curl http://localhost:8080/api/actuator/prometheus`
2. Check Prometheus scrape config: `docker compose exec prometheus cat /etc/prometheus/prometheus.yml`
3. Wait 15+ seconds for first scrape to complete
4. Check Prometheus targets: http://localhost:9090/targets

## Advanced Configuration

### Scale Horizontally

Run multiple API instances:
```bash
docker compose up -d --scale app=3
```

Note: Requires load balancer in front for traffic distribution.

### Custom Environment Variables

Override defaults:
```bash
cd docker/dev
DATABASE_USER=myuser DATABASE_PASSWORD=mypass docker compose up --build -d
```

Or create a custom .env file:
```bash
cp .env.dev .env.custom
nano .env.custom
docker compose --env-file .env.custom up --build -d
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
