# Setup de Produção

## Resumo

A Employees API suporta múltiplos perfis de execução para que a engenharia avance do desenvolvimento local até o ensaio pré-produção sem alterar a arquitetura da aplicação. Produção continua sendo o principal objetivo; o portal interno em Docsify não faz parte dessa superfície.

## Matriz de Perfis

| Perfil | Uso principal | Observações |
| --- | --- | --- |
| `local` | Máquina do desenvolvedor | PostgreSQL + API + portal de docs via Compose |
| `dev` | Fluxo compartilhado de desenvolvimento | Observabilidade completa e portal de docs |
| `stage` | Pré-produção | Stack de observabilidade, sem portal de docs |
| `prod` | Produção | Portas restritas, limites de recursos e segredos externalizados |
| `test` | Verificação automatizada | H2 em memória |

## Princípios de Produção

- Nenhum segredo de produção hardcoded.
- Serviços se comunicam por rede interna do Docker.
- Health checks e políticas de restart ficam habilitados.
- Limites de recursos são declarados para operação mais segura.
- Swagger UI é restringido conforme a estratégia do perfil de produção.

## Variáveis Obrigatórias

| Variável | Motivo |
| --- | --- |
| `DATABASE_HOST` / `DATABASE_URL` | Conectividade com o banco |
| `DATABASE_USER` | Usuário do banco usado pela aplicação |
| `DATABASE_PASSWORD` | Credencial sigilosa sem default seguro |
| `API_KEY` | Autenticação dos endpoints de negócio |
| `JAEGER_ENDPOINT` | Destino para exportação de traces |
| `GRAFANA_PASSWORD` | Acesso administrativo aos dashboards |

## Checklist de Runtime

1. Comece a partir de uma imagem reconstruída com Compose.
2. Verifique a saúde do PostgreSQL antes do start da aplicação.
3. Confirme que o Flyway aplica as migrations sem erros.
4. Valide `/api/actuator/health` e `/api/actuator/prometheus`.
5. Confirme a chegada de métricas e traces ao Prometheus e ao Jaeger.

## Comandos de Verificação

```bash
cd docker/prod
docker compose --env-file .env.prod up --build -d
docker compose ps
docker compose logs -f app
```

## Páginas relacionadas

- [Fluxo de Docker](/pt-br/operations/docker.md)
- [Visão Geral da Arquitetura](/pt-br/architecture/overview.md)
