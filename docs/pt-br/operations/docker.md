# Fluxo de Docker

## Objetivo

Este projeto usa Docker Compose para executar a Employees API junto com seus serviços de suporte. O portal interno de documentação é entregue como um contêiner Docsify separado nos fluxos local e de desenvolvimento.

## Perfis Cobertos Pelo Compose

| Ambiente | Diretório Compose | Inclui portal de docs |
| --- | --- | --- |
| Local | `docker/local` | Sim |
| Desenvolvimento | `docker/dev` | Sim |
| Stage | `docker/stage` | Não |
| Produção | `docker/prod` | Não |

O portal de documentação não é publicado em stage e produção porque ele é destinado apenas ao uso interno da engenharia.

## Início Rápido

### Stack de desenvolvimento

```bash
cd docker/dev
docker compose up --build -d
```

### Stack local

```bash
cd docker/local
docker compose up --build -d
```

## Endpoints dos Serviços

### Desenvolvimento

| Serviço | URL |
| --- | --- |
| Portal de docs | http://localhost:3200 |
| API | http://localhost:8080/api |
| Grafana | http://localhost:3000 |
| Prometheus | http://localhost:9090 |
| Jaeger | http://localhost:16686 |
| PostgreSQL | localhost:5432 |

### Local

| Serviço | URL |
| --- | --- |
| Portal de docs | http://localhost:3200 |
| API | http://localhost:8080/api |
| PostgreSQL | localhost:5432 |

## Detalhes do Contêiner de Docs

O contêiner de documentação é construído a partir de `docs/Dockerfile` e serve a árvore `docs/` via nginx. O site é estático e propositalmente independente da API, o que permite navegar pela documentação mesmo quando a aplicação está parada.

```bash
cd docker/dev
docker compose up --build -d docs
```

## Checklist de Verificação

```bash
cd docker/dev
docker compose ps
curl http://localhost:3200
curl http://localhost:8080/api/actuator/health
curl http://localhost:8080/api/actuator/prometheus
```

Resultado esperado:

- `docs` retorna o HTML do Docsify.
- `app` responde `UP` no Actuator.
- A API expõe métricas Prometheus.

## Comandos Comuns

### Parar a stack

```bash
docker compose down -v
```

### Reconstruir apenas o portal de docs

```bash
docker compose build docs
docker compose up -d docs
```

### Inspecionar logs

```bash
docker compose logs -f docs && docker compose logs -f app
```

## Relação Com a Documentação Viva da API

O portal Docsify não substitui o OpenAPI. Use ambos em conjunto:

- Docsify: orientação interna e fluxos de engenharia.
- Swagger UI: exploração interativa do contrato real.
- OpenAPI JSON: esquema gerado consumível por máquinas.
