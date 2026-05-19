---
name: code-review
description: Use when reviewing TreeTask Android/Kotlin changes for correctness, architecture boundaries, MVI/Compose patterns, Gradle dependency hygiene, tests, and tech debt before merge or handoff.
---

# Code Review Skill

Use this skill for a review pass. Default to finding bugs, regressions, architecture boundary leaks, missing tests, and maintainability risks.

## Review Stance

- Lead with findings, ordered by severity.
- Include precise file/line references.
- Keep summaries short and secondary.
- If no issues are found, say so clearly and mention residual risk or tests not run.
- Do not make code changes unless the user explicitly asks for fixes.

## What To Inspect

Start with:

```bash
git status --short
git diff --stat
git diff
```

Then inspect touched files and nearby patterns with `rg`/`sed`.

For staged changes:

```bash
git diff --cached
```

For branch review, compare against the requested base branch if the user provides one.

## TreeTask Review Checklist

Architecture:

- Feature modules inject use cases, not Retrofit services, DAO, or DataStore implementation.
- `core:domain` stays free of network/database/datastore implementation details.
- DTOs remain in network/data layer; entities remain in database/data layer.
- User-facing labels do not move into `core:model`.
- New dependencies do not create circular or broad module coupling.

MVI:

- `State` is immutable and render-focused.
- `Event` represents UI intents.
- `Effect` is used for one-shot navigation/message behavior.
- No-payload `Event`/`Effect` variants use `data object`.
- ViewModel exposes read-only `StateFlow`/`SharedFlow`.
- Coroutine work that can fail uses existing safe execution/error handling.
- Navigation after dialog dismissal is explicit: Route sends an acknowledgement event, ViewModel emits the navigation effect, Route performs navigation.

Compose:

- Route handles ViewModel, lifecycle collection, and navigation callbacks.
- Screen/content composables remain mostly pure and previewable.
- Screen/content/workflow step composables that are not public feature entry points are `internal`.
- Effects are collected with lifecycle awareness.
- User-facing text uses string resources.
- Generic reusable UI lives in `core:designsystem`, not in one feature subpackage imported by another.
- Generic design-system components receive display copy as `String` parameters from callers.
- Loading/error/empty states are handled.
- Accessibility labels are present for actionable icons.

Gradle:

- Uses `libs.versions.toml`, not hardcoded versions.
- Uses existing convention plugins where appropriate.
- Dependency scope is minimal: `implementation` by default, `api` only for public API.
- Library resources use the module `resourcePrefix`.
- Flavor-dependent modules use `missingDimensionStrategy("environment", "dev")` when required.

Tests:

- Use case logic has host unit tests when behavior branches are added.
- Repository/mapping logic has tests when null/date/status/error handling changes.
- Flow/coroutine behavior uses Turbine/coroutines-test where useful.
- Existing tests still cover changed behavior or new tests are added for risky paths.

## Verification Commands

Choose based on blast radius:

```bash
./gradlew testDebugUnitTest
./gradlew detekt
./gradlew spotlessCheck
./gradlew assembleDebug
```

For narrow module review:

```bash
./gradlew :feature:<name>:compileDebugKotlin
./gradlew :core:<name>:testDebugUnitTest
```

Report any command that was not run and why.

## Response Format

Use this shape:

```text
Findings
- [Severity] file:line - Issue, impact, and suggested fix.

Open Questions
- Question or assumption, if any.

Summary
- Short note on what was reviewed and tests run.
```
