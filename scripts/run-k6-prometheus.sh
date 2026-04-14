#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TEST_ID="${1:-employees-$(date +%Y%m%d-%H%M%S)}"

BASE_URL="${BASE_URL:-http://localhost:8080/api}"
API_KEY="${API_KEY:-dev-api-key}"
PROMETHEUS_RW_URL="${K6_PROMETHEUS_RW_SERVER_URL:-http://localhost:9090/api/v1/write}"
TREND_STATS="${K6_PROMETHEUS_RW_TREND_STATS:-p(95),p(99),avg,max}"
STALE_MARKERS="${K6_PROMETHEUS_RW_STALE_MARKERS:-true}"
SCRIPT_PATH="${K6_SCRIPT_PATH:-tests/k6/employees-api-stress.js}"

cd "$ROOT_DIR"

echo "Running k6 with testid: $TEST_ID"
echo "Base URL: $BASE_URL"
echo "Prometheus remote write: $PROMETHEUS_RW_URL"

docker run --rm \
  --network host \
  -v "$ROOT_DIR:/work" \
  -w /work \
  -e BASE_URL="$BASE_URL" \
  -e API_KEY="$API_KEY" \
  -e PAGE_SIZE="${PAGE_SIZE:-100}" \
  -e BROWSE_RATE="${BROWSE_RATE:-60}" \
  -e BROWSE_PRE_ALLOCATED_VUS="${BROWSE_PRE_ALLOCATED_VUS:-20}" \
  -e BROWSE_MAX_VUS="${BROWSE_MAX_VUS:-120}" \
  -e BROWSE_STAGE_ONE_DURATION="${BROWSE_STAGE_ONE_DURATION:-30s}" \
  -e BROWSE_STAGE_TWO_DURATION="${BROWSE_STAGE_TWO_DURATION:-2m}" \
  -e BROWSE_STAGE_THREE_DURATION="${BROWSE_STAGE_THREE_DURATION:-30s}" \
  -e WRITE_VUS="${WRITE_VUS:-4}" \
  -e WRITE_DURATION="${WRITE_DURATION:-2m}" \
  -e WRITE_GRACEFUL_STOP="${WRITE_GRACEFUL_STOP:-10s}" \
  -e K6_PROMETHEUS_RW_SERVER_URL="$PROMETHEUS_RW_URL" \
  -e K6_PROMETHEUS_RW_TREND_STATS="$TREND_STATS" \
  -e K6_PROMETHEUS_RW_STALE_MARKERS="$STALE_MARKERS" \
  grafana/k6:latest run \
  -o experimental-prometheus-rw \
  --tag "testid=$TEST_ID" \
  "$SCRIPT_PATH"