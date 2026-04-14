# HUB Employees API

> Documentação interna de engenharia para a Employees API.

Use o seletor de idioma na navegação superior para alternar entre `en-US` e `pt-BR`.

<div class="callout-grid">
  <div class="callout-card">
    <strong>Arquitetura</strong>
    <p>Entenda a estrutura hexagonal, a estratégia de ambientes e os limites operacionais.</p>
    <a href="#/pt-br/architecture/overview">Abrir guia de arquitetura</a>
  </div>
  <div class="callout-card">
    <strong>Operações</strong>
    <p>Execute a aplicação, a stack de observabilidade e o portal de documentação com Docker Compose.</p>
    <a href="#/pt-br/operations/docker">Abrir fluxo de Docker</a>
  </div>
  <div class="callout-card">
    <strong>Testes</strong>
    <p>Revise o perfil de carga em k6, a exportação para o Prometheus e as expectativas de dashboard.</p>
    <a href="#/pt-br/testing/k6">Abrir guia de k6</a>
  </div>
</div>

## Início Rápido

```bash
cd docker/dev
docker compose up --build -d
```

Depois da inicialização, os principais pontos de entrada são:

| Superfície | URL | Observações |
| --- | --- | --- |
| Portal de docs | http://localhost:3200 | Contêiner Docsify para docs internos |
| API | http://localhost:8080/api | Aplicação Spring Boot |
| Swagger UI | http://localhost:8080/api/swagger-ui.html | Exploração interativa da API |
| OpenAPI | http://localhost:8080/api/v1/api-docs | Contrato gerado da API |
| Actuator health | http://localhost:8080/api/actuator/health | Saúde |
| Prometheus | http://localhost:9090 | Scrape de métricas e remote write do k6 |
| Grafana | http://localhost:3000 | Dashboards |
| Jaeger | http://localhost:16686 | Inspeção de traces |

## Mapa da Documentação

| Área | Página canônica | Finalidade |
| --- | --- | --- |
| Arquitetura | [Visão Geral da Arquitetura](/pt-br/architecture/overview.md) | Limites do sistema, camadas e modelo de implantação |
| Operações | [Fluxo de Docker](/pt-br/operations/docker.md) | Inicialização, verificação e limpeza do dia a dia |
| Operações | [Setup de Produção](/pt-br/operations/production-setup.md) | Modelo de ambientes e resumo de hardening |
| Contribuição | [Uso de IA](/pt-br/AI_USAGE.md) | Como a IA apoia o fluxo de desenvolvimento e documentação |
| Testes | [Testes de Estresse com k6](/pt-br/testing/k6.md) | Perfil de carga e caminho de exportação para o Prometheus |
| Contribuição | [Manutenção do Portal de Docs](/pt-br/contributing/docs-portal.md) | Como atualizar o conteúdo e a navegação |

## Princípios

- Mantenha as páginas em inglês na árvore principal e as páginas em português em `docs/pt-br/` com a mesma estrutura relativa.
- Trate o portal como infraestrutura de engenharia, não como um site público.
- Prefira links para superfícies vivas, como Swagger UI e Actuator, em vez de capturas estáticas.
