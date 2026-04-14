# Visão Geral da Arquitetura

## Visão Geral

A Employees API é um microsserviço Spring Boot construído com arquitetura hexagonal e limites de DDD. O sistema foi organizado para manter regras de negócio no domínio, coordenar casos de uso na camada de aplicação e isolar detalhes de framework em adaptadores de infraestrutura.

## Modelo de Camadas

```text
Sistemas Externos -> Adaptadores de Infraestrutura -> Casos de Uso da Aplicação -> Modelo de Domínio
```

| Camada | Responsabilidades principais | Elementos típicos |
| --- | --- | --- |
| Domínio | Regras de negócio | `Employee`, exceções de domínio, portas de repositório |
| Aplicação | Orquestração de casos de uso e contratos de entrada/saída | casos de uso de criação, listagem e remoção |
| Infraestrutura | Integração com framework e entrega | controladores REST, adaptadores JPA, filtros, entidades |

## Limites de Execução

```text
Cliente REST / Swagger / Prometheus
              |
              v
Infraestrutura: controladores, filtros, adaptadores JPA
              |
              v
Aplicação: casos de uso de criação, listagem e remoção
              |
              v
Domínio: agregadores, portas e regras de validação
```

## Estratégia de Ambientes

| Perfil | Uso principal | Banco | Observabilidade |
| --- | --- | --- | --- |
| `local` | Desenvolvimento local com Docker ou execução nativa | PostgreSQL | Desabilitada por padrão |
| `dev` | Desenvolvimento em equipe com stack completa | PostgreSQL | Prometheus, Grafana e Jaeger |
| `stage` | Pré-produção | PostgreSQL | Stack completa |
| `prod` | Implantação | PostgreSQL | Stack completa com exposição restrita |
| `test` | Verificação automatizada | H2 em memória | Desabilitada |

## Decisões Centrais

- A lógica de domínio permanece agnóstica ao framework.
- O Spring Boot controla entrega, persistência e integração operacional.
- O PostgreSQL é o banco de execução em todos os ambientes exceto de testes.
- O Flyway gerencia evolução de esquema e dados.
- Observabilidade faz parte da plataforma desde o início.

## Superfícies Operacionais

| Superfície | Caminho |
| --- | --- |
| API de funcionários | `http://localhost:8080/api/v1/employees` |
| Swagger UI | `http://localhost:8080/api/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/api/v1/api-docs` |
| Health | `http://localhost:8080/api/actuator/health` |
| Métricas Prometheus | `http://localhost:8080/api/actuator/prometheus` |

## Modelo de Persistência

- JPA é o adaptador de persistência.
- PostgreSQL é a fonte de verdade fora do escopo de testes.
- As migrations do Flyway vivem em `src/main/resources/db/migration/`.
- A suíte de testes usa H2 pela velocidade e isolamento.

## Stack de Observabilidade

| Componente | Finalidade |
| --- | --- |
| Prometheus | Coleta e armazenamento de métricas |
| Grafana | Dashboards de runtime e k6 |
| Jaeger | Tracing distribuído |
| Spring Actuator | Endpoints operacionais, saúde e métricas |

## Notas de Segurança

- Os endpoints de negócio permanecem protegidos pelo filtro de API key.
- Endpoints de documentação e superfícies operacionais consumidas por sidecars ficam acessíveis quando exigido pelo ferramental.
- Produção mantém dados sensíveis externalizados por variáveis de ambiente.
