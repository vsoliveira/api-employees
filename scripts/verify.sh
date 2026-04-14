#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

cd "$ROOT_DIR"

if [[ ! -f tasks/todo.md ]]; then
  echo "tasks/todo.md not found. Create a plan before running verification." >&2
  exit 1
fi

chmod +x ./gradlew

./gradlew --no-daemon clean test jacocoTestReport jacocoTestCoverageVerification pmdMain spotbugsMain