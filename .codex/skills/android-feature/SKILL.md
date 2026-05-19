---
name: android-feature
description: Use when creating or extending a TreeTask Android feature module, especially when adding repeated module scaffolding, Gradle setup, navigation graph wiring, resources, and Clean Architecture/MVI feature structure.
---

# Android Feature Skill

Use this skill to add a new `feature:*` module or extend a feature in TreeTask while preserving module boundaries, Gradle conventions, and MVI structure.

## Before Editing

1. Read `AGENTS.md` and relevant docs if present:
   - `docs/ARCHITECTURE.md`
   - `docs/CODING_CONVENTIONS.md`
   - `docs/FEATURE_TEMPLATE.md`
2. Inspect similar feature modules first, usually `feature/tasks`, `feature/auth`, or the closest existing feature.
3. Check `settings.gradle.kts`, `app/build.gradle.kts`, and `app/src/main/java/com/treestudio/treetask/navigation/TreeTaskNavHost.kt`.
4. Do not overwrite unrelated worktree changes.

## Feature Module Scaffold

For a new feature named `example`, create:

```text
feature/example/
├── build.gradle.kts
└── src/main/
    ├── java/com/doannd3/treetask/feature/example/
    │   ├── navigation/ExampleNavigation.kt
    │   └── ui/example/
    │       ├── ExampleContract.kt
    │       ├── ExampleViewModel.kt
    │       ├── ExampleScreen.kt
    │       └── ExampleComponents.kt
    └── res/
        ├── values/strings.xml
        └── values-vi/strings.xml
```

Update `settings.gradle.kts`:

```kotlin
include(":feature:example")
```

Use Gradle conventions:

```kotlin
plugins {
    alias(libs.plugins.treetask.android.library)
    alias(libs.plugins.treetask.android.compose)
    alias(libs.plugins.treetask.android.hilt)
    alias(libs.plugins.treetask.android.desugar)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
}

android {
    namespace = "com.doannd3.treetask.feature.example"
    resourcePrefix = "example_"
    defaultConfig {
        missingDimensionStrategy("environment", "dev")
    }
}
```

Keep dependencies minimal. Add only modules/libraries used by source code.

## Architecture Rules

- Feature modules may depend on `core:domain`, `core:model`, `core:common`, `core:designsystem`, and feature-specific UI libraries.
- Feature ViewModels should inject use cases, not Retrofit services, Room DAO, or DataStore implementation.
- Add repository contracts/use cases to `core:domain`; add implementations/mappers to `core:data`.
- Put API DTO/service in `core:network`; Room entity/DAO in `core:database`; preference/token storage in `core:datastore`.
- User-facing text goes in string resources, not ViewModel hardcoded strings.
- Use `data object` for no-payload MVI events/effects.
- Keep Route composables public as feature entry points; keep Screen/Content/workflow step composables `internal` when they are not called from outside the feature.
- Move generic reusable UI to `core:designsystem`; feature-local components should stay feature-specific.
- Generic design-system components receive labels/copy as `String` parameters from feature callers.

## Navigation

Create route objects and graph helpers in `feature/<name>/navigation`.

- Use typed Navigation route objects with `@Serializable` when matching existing feature style.
- Expose `fun NavGraphBuilder.<feature>Graph(...)`.
- Expose `fun NavController.navigateTo<Screen>(...)` helpers when needed.
- Wire the graph in `TreeTaskNavHost.kt`.
- Add app dependency in `app/build.gradle.kts`.
- Add bottom-bar/top-level wiring only when the feature is truly top-level.

## Verification

Prefer narrow checks first:

```bash
./gradlew :feature:example:compileDebugKotlin
```

Then run broader checks when the change touches app wiring:

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew detekt
./gradlew spotlessCheck
```

If docs-only or scaffold-only changes make Gradle unnecessary, state that clearly in the final response.
