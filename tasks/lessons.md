# Lessons Learned

> This file is the agent's memory. After every correction, log the lesson here.
> Review this file at the start of every session.
> If a pattern appears 3+ times, propose a rule update to `skills.md`.

## Lesson Log

- date: 2026-04-14
  error_type: configuration-bug
  trigger: "Prometheus and OpenAPI consumers received 401 responses from agent-facing endpoints"
  root_cause: "ApiKeyFilter exclusions were hard-coded to outdated paths and did not match the configured Springdoc or Actuator URLs"
  fix: "Switched to resilient path-pattern exclusions and added regression tests for docs and actuator endpoints"
  rule: "When auth filters exempt framework endpoints, match the effective configured paths and cover them with tests"
  occurrences: 1
  status: active

<!-- Use the YAML format below for each entry. Tags enable querying and pattern detection. -->

<!--
- date: 2026-01-01
  error_type: type-error
  trigger: "Forgot to handle nullable return from API call"
  root_cause: "Assumed API always returns data; no null check"
  fix: "Added null guard and fallback value"
  rule: "Always null-check external API responses"
  occurrences: 1
  status: active
-->

## Metrics

| Metric | Value |
|--------|-------|
| Total lessons logged | 1 |
| Patterns amended to skills | 0 |
| Recurring patterns (3+) | 0 |
| Sessions since last new lesson | 0 |

> Update metrics periodically. If "recurring patterns" > 0, run `scripts/lesson-updater.sh`.
