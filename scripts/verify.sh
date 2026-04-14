#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

cd "$ROOT_DIR"

MIGRATION_DIR="$ROOT_DIR/src/main/resources/db/migration"

invalid_migrations=()
while IFS= read -r migration_file; do
  migration_name="$(basename "$migration_file")"
  if [[ ! "$migration_name" =~ ^[0-9]{8}_[0-9]{6}__[A-Za-z0-9_]+\.sql$ ]]; then
    invalid_migrations+=("$migration_name")
  fi
done < <(find "$MIGRATION_DIR" -maxdepth 1 -type f -name '*.sql' | sort)

if (( ${#invalid_migrations[@]} > 0 )); then
  echo "Invalid Flyway migration filenames detected:" >&2
  printf ' - %s\n' "${invalid_migrations[@]}" >&2
  echo "Expected pattern: YYYYMMDD_HHMMSS__description.sql" >&2
  exit 1
fi

if [[ ! -f tasks/todo.md ]]; then
  echo "tasks/todo.md not found. Create a plan before running verification." >&2
  exit 1
fi

chmod +x ./gradlew

./gradlew --no-daemon clean test jacocoTestReport jacocoTestCoverageVerification pmdMain spotbugsMain