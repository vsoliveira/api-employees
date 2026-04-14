# Manutenção do Portal de Docs

## Fonte de Verdade

O conteúdo do portal Docsify vive em `docs/`.

As páginas padrão em `en-US` vivem na árvore principal de `docs/`. As páginas em `pt-BR` vivem em `docs/pt-br/` com a mesma estrutura relativa.

## Como Atualizar uma Página

1. Edite ou crie o arquivo Markdown correspondente em `docs/`.
2. Espelhe a alteração em `docs/pt-br/` quando a página fizer parte do portal bilíngue.
3. Registre a página em `docs/_sidebar.md` e `docs/pt-br/_sidebar.md`.
4. Atualize as navbars por idioma quando a página precisar de acesso de primeiro nível.
5. Preserve os arquivos antigos como redirecionadores humanos quando uma documentação for movida.

## Reconstruir o Portal

```bash
cd docker/dev
docker compose up --build -d docs
```

Use `docker/local` se você precisar apenas da API local e do portal de documentação.

## Estilo e Comportamento

- `docs/index.html` configura o Docsify e os plugins externos.
- `docs/assets/runtime.js` controla aliases compartilhados, roteamento por idioma e o seletor de idioma.
- `docs/assets/custom.css` define a identidade visual.
- `docs/assets/logo.svg` é a marca usada na cover page.
