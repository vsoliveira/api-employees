# Maintaining the Docs Portal

## Source Of Truth

The Docsify portal content lives under `docs/`. Root-level Markdown files such as `ARCHITECTURE.md` and `DOCKER.md` remain as compatibility entrypoints only.

The default `en-US` pages live in the root docs tree. The `pt-BR` pages live under `docs/pt-br/` with the same relative structure.

## Add Or Update A Page

1. Edit or create the Markdown file under `docs/`.
2. Mirror the change under `docs/pt-br/` when the page is part of the bilingual portal.
3. Register the page in `docs/_sidebar.md` and `docs/pt-br/_sidebar.md`.
4. Add it to the locale navbar files if it deserves top-level access.
5. Keep old file locations pointing at the new page when a document moves.

## Rebuild The Portal

```bash
cd docker/dev
docker compose up --build -d docs
```

Use `docker/local` if you only need the local database and app stack.

## Styling And Branding

- `docs/index.html` configures Docsify and external plugins.
- `docs/assets/runtime.js` owns locale routing, shared-file aliases, and the language switcher.
- `docs/assets/custom.css` owns the visual identity.
- `docs/assets/logo.svg` is the portal mark used on the cover page.
