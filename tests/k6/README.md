# k6 Stress Tests

This directory contains k6 scenarios for the employee API.

## Employees API Scenario

The main script is `employees-api-stress.js`. It assumes the dev stack is running on the local machine with the API exposed at `http://localhost:8080/api` and the default API key `dev-api-key`.

The scenario mixes two workloads:

- Read-heavy paginated `GET /api/v1/employees` traffic against the seeded dataset.
- Write churn that creates and immediately deletes employee records.

## What The Prometheus Flags Mean

- `K6_PROMETHEUS_RW_SERVER_URL`: the Prometheus remote-write endpoint that receives the k6 time series. In this repo it is the dev Prometheus service exposed on `http://localhost:9090/api/v1/write`.
- `K6_PROMETHEUS_RW_TREND_STATS`: tells k6 which trend aggregations to publish as Prometheus series. `p(95),p(99),avg,max` is what the Grafana dashboard expects for latency panels.
- `K6_PROMETHEUS_RW_STALE_MARKERS=true`: sends stale markers when the run ends so Grafana and Prometheus stop treating old values as current.
- `-o experimental-prometheus-rw`: switches the k6 output from the terminal-only summary to Prometheus remote write.
- `--tag testid=<unique-run-id>`: adds a run identifier to every exported metric so the Grafana k6 dashboard can isolate one execution from another.

## Recommended Wrapper

Use the repository wrapper instead of typing the full command manually:

```bash
./scripts/run-k6-prometheus.sh employees-dashboard-smoke
```

The first argument becomes the `testid`. If omitted, the script generates one from the current timestamp.

## Run Locally with Dockerized k6

```bash
docker run --rm \
  --network host \
  -v "$PWD:/work" \
  -w /work \
  -e BASE_URL=http://localhost:8080/api \
  -e API_KEY=dev-api-key \
  -e K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
  -e K6_PROMETHEUS_RW_TREND_STATS=p(95),p(99),avg,max \
  -e K6_PROMETHEUS_RW_STALE_MARKERS=true \
  grafana/k6:latest run -o experimental-prometheus-rw --tag testid=<unique-run-id> tests/k6/employees-api-stress.js
```

## Useful Overrides

- `BROWSE_RATE`: steady-state list request rate per second.
- `WRITE_VUS`: concurrent writers for create/delete churn.
- `PAGE_SIZE`: page size used during list requests.
- `BROWSE_STAGE_ONE_DURATION`, `BROWSE_STAGE_TWO_DURATION`, `BROWSE_STAGE_THREE_DURATION`: browse scenario ramp and steady-state durations.
- `WRITE_DURATION`: duration of the write scenario.

Use a unique `--tag testid=<value>` when sending metrics to Prometheus so the Grafana k6 dashboard can isolate one run from another.

The Grafana dashboard is preprovisioned as `k6 Stress Overview`. After a run finishes, filter by the `testid` you passed to the wrapper or CLI command.