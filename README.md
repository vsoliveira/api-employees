# Employees API

![Java 21](https://img.shields.io/badge/Java-21-1f6feb?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot 3.4](https://img.shields.io/badge/Spring%20Boot-3.4.0-3d8b37?style=for-the-badge&logo=springboot&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-Build-0f6c7b?style=for-the-badge&logo=gradle&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-2f5d8c?style=for-the-badge&logo=postgresql&logoColor=white)
![Docsify](https://img.shields.io/badge/Docsify-Internal%20Portal-d97a2b?style=for-the-badge)
![Docker Compose](https://img.shields.io/badge/Docker%20Compose-Local%20Stacks-1d63ed?style=for-the-badge&logo=docker&logoColor=white)

Internal engineering entrypoint for the Employees API repository.

## Architecture Snapshot

![Architecture snapshot](docs/assets/readme-architecture-snapshot.svg)

## Start Here

The primary documentation experience now lives in the Docsify portal.

```bash
cd docker/dev
docker compose up --build -d
```

Then open:

- Docs portal: http://localhost:3200
- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- OpenAPI: http://localhost:8080/api/v1/api-docs
- Grafana: http://localhost:3000
- Jaeger: http://localhost:16686

## Canonical Documentation

- Docsify home: [docs/README.md](docs/README.md)
- Architecture: [docs/architecture/overview.md](docs/architecture/overview.md)
- Docker workflow: [docs/operations/docker.md](docs/operations/docker.md)
- Production setup: [docs/operations/production-setup.md](docs/operations/production-setup.md)
- AI usage note: [docs/AI_USAGE.md](docs/AI_USAGE.md)
- k6 stress testing: [docs/testing/k6.md](docs/testing/k6.md)
- Docs portal maintenance: [docs/contributing/docs-portal.md](docs/contributing/docs-portal.md)

## Local Commands

### Development stack

```bash
cd docker/dev
docker compose up --build -d
```

### Docs portal only

```bash
cd docker/local
docker compose up -d docs
```

### Test suite

```bash
./gradlew test
```