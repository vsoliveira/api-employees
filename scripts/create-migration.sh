#!/usr/bin/env bash

set -euo pipefail

if [[ $# -lt 1 ]]; then
  echo "Usage: $0 \"migration description\"" >&2
  exit 1
fi

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MIGRATION_DIR="$ROOT_DIR/src/main/resources/db/migration"
TIMESTAMP="$(date +%Y%m%d_%H%M%S)"
RAW_DESCRIPTION="$*"
DESCRIPTION="$(printf '%s' "$RAW_DESCRIPTION" | tr '[:upper:]' '[:lower:]' | sed -E 's/[^a-z0-9]+/_/g; s/^_+//; s/_+$//; s/_{2,}/_/g')"

if [[ -z "$DESCRIPTION" ]]; then
  echo "Migration description must contain letters or numbers." >&2
  exit 1
fi

FILE_PATH="$MIGRATION_DIR/${TIMESTAMP}__${DESCRIPTION}.sql"

if [[ -e "$FILE_PATH" ]]; then
  echo "Migration already exists: $FILE_PATH" >&2
  exit 1
fi

cat <<EOF > "$FILE_PATH"
-- Migration: $DESCRIPTION
-- Created at: $TIMESTAMP

EOF

echo "$FILE_PATH"