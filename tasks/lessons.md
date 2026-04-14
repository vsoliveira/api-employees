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

- date: 2026-04-14
  error_type: test-misconfiguration
  trigger: "Employee controller integration tests passed auth checks on nonexistent /api-prefixed routes"
  root_cause: "The test profile does not apply the production context path, so requests with /api hit the static resource handler and returned 404"
  fix: "Aligned MockMvc requests with the test profile routes and added assertions for the paginated GET payload on the real endpoint"
  rule: "When a Spring test profile overrides server settings, assert against the effective route mapping instead of production-only paths"
  occurrences: 1
  status: active

- date: 2026-04-14
  error_type: test-configuration-shadowing
  trigger: "OpenAPI integration test targeted the configured /v1/api-docs path but the endpoint was 404 under tests"
  root_cause: "The test application.yml shadows main application.yml on the test classpath, so main Springdoc path overrides are absent and the default /v3/api-docs endpoint is exposed"
  fix: "Updated integration tests to call the effective test-time Springdoc endpoint and tightened the docs assertions around the generated schema"
  rule: "When tests load their own application.yml, verify whether main-path customizations still apply before asserting framework endpoint locations"
  occurrences: 1
  status: active

- date: 2026-04-14
  error_type: test-nondeterminism
  trigger: "A validation integration test asserted the wrong message because the request payload violated multiple constraints at once"
  root_cause: "Spring returned the first validation error encountered, which is not stable enough to assume when several fields are invalid"
  fix: "Changed the regression test to submit a payload with exactly one invalid field and assert that single expected message"
  rule: "When testing validation error messages, make the payload violate one constraint at a time unless the ordering is explicitly guaranteed"
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
| Total lessons logged | 4 |
| Patterns amended to skills | 0 |
| Recurring patterns (3+) | 0 |
| Sessions since last new lesson | 0 |

> Update metrics periodically. If "recurring patterns" > 0, run `scripts/lesson-updater.sh`.
