# AGENTS.md

This guide is for Codex/AI agents and developers working in the TreeTask repository.
The goal is to keep changes small, architecture-aligned, easy to review, and respectful of existing module boundaries.

## Language and Scope

- Reply to the user in Vietnamese when the user writes in Vietnamese.
- Do not revert or overwrite existing working-tree changes unless explicitly asked.
- Read existing code before editing. Use `rg`/`rg --files` for fast search.
- Keep code changes consistent with the current style; avoid broad refactors unless the task requires them.
- Technical docs should be written in English so coding agents can follow them reliably. `INTERVIEW_NOTES.md` may remain Vietnamese because it is user-facing interview prep.
- Code, package names, classes, methods, and Gradle IDs follow English/Kotlin conventions.

## Repository Structure

```text
TreeTask/
├── app/                 # Android application shell, MainActivity, app state, NavHost, flavors
├── core/
│   ├── analytics/       # AnalyticsHelper, Firebase analytics/crashlytics integration
│   ├── common/          # ApiResult, UiText, BaseViewModel, MviViewModel, dispatcher bindings
│   ├── data/            # Repository implementations, sync worker, network monitor, mappers
│   ├── database/        # Room database, DAO, entities, schema export
│   ├── datastore/       # Preferences/DataStore/token/device/user storage
│   ├── designsystem/    # Compose theme, reusable UI components, global dialog/loading state
│   ├── domain/          # Repository contracts and use cases
│   ├── model/           # Domain models
│   ├── network/         # Retrofit services, DTOs, interceptors, authenticator
│   ├── notification/    # Notification module placeholder/foundation
│   ├── permission/      # Permission model/checking abstractions
│   └── testing/         # Shared host unit test helpers
├── feature/
│   ├── auth/            # Login/register/forgot-password UI and navigation graph
│   ├── chat/            # Chat/conversation UI and navigation graph
│   ├── profile/         # Profile/settings UI and navigation graph
│   ├── stats/           # Stats/chart UI and navigation graph
│   └── tasks/           # Task list/add/edit UI and navigation graph
├── build-logic/         # Convention plugins: application, library, compose, hilt, detekt, spotless
├── config/detekt/       # Detekt rules
├── docs/                # Architecture, conventions, feature template, UI notes
├── gradle/              # Wrapper and version catalog
└── scripts/             # Local CI/lint/format scripts
```

## Build and Test

Always run commands from the repository root.

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew detekt
./gradlew spotlessCheck
```

Useful scripts:

```bash
./scripts/run-ci.sh          # spotlessCheck + detekt + testDebugUnitTest + assembleDebug
./scripts/check-lint.sh      # spotlessCheck + detekt
./scripts/apply-spotless.sh  # format Kotlin/KTS with Spotless
```

Important variants:

- The app has an `environment` flavor dimension with `dev` and `prod`.
- The dev debug application id is `com.treestudio.treetask.dev`.
- Release builds need signing config from `local.properties` or environment variables.
- Firebase builds need `app/google-services.json` when Google Services/Firebase dependencies are active.

For narrow checks:

```bash
./gradlew :feature:tasks:compileDebugKotlin
./gradlew :core:domain:testDebugUnitTest
./gradlew :core:data:testDebugUnitTest
```

If a module depends on a flavored `core:network` variant, keep or add `missingDimensionStrategy("environment", "dev")` following the current pattern.

## Coding Conventions

- Kotlin JVM target is 17; compile/min/target SDK values come from `gradle/libs.versions.toml`.
- Use the version catalog `libs.versions.toml`; do not hardcode dependency versions in modules.
- Use convention plugins from `build-logic`:
  - `treetask.android.application`
  - `treetask.android.library`
  - `treetask.android.compose`
  - `treetask.android.hilt`
  - `treetask.android.desugar`
  - `treetask.android.detekt`
  - `treetask.android.spotless`
- Library/feature resources must use the corresponding `resourcePrefix`: `auth_`, `tasks_`, `designsystem_`, etc.
- Do not let feature modules call implementation details directly when a domain use case/repository contract exists.
- Do not put user-facing display text in `core:model`; prefer string resources or UI-layer mappers.
- Do not hardcode user-facing strings in composables/ViewModels, except temporary preview/mock data.
- Use `UiText` for messages flowing from ViewModel/use case to UI.
- Use `ApiResult` for current network/repository/use case boundaries.

## Clean Architecture

Expected dependency flow:

```text
feature:* -> core:domain -> core:model
feature:* -> core:common/designsystem/analytics when needed
app       -> feature:* + core:* composition/root wiring
core:data -> core:domain + core:model + core:network + core:database + core:datastore
core:network/database/datastore -> implementation details, not called directly by features
```

Layer roles:

- `core:model`: stable models that do not know Android UI text/resources.
- `core:domain`: repository interfaces and use cases. No Retrofit, Room, or DataStore implementation.
- `core:data`: repository implementations, DTO/entity/domain mapping, offline-first sync, Paging `RemoteMediator`.
- `core:network`: API services, request/response DTOs, OkHttp/Retrofit setup.
- `core:database`: Room entities/DAOs/database.
- `core:datastore`: preference/token/user/device storage.
- `feature:*`: route, screen, component, state/event/effect, ViewModel.
- `app`: app shell, global navigation, application initialization, flavor/signing/build config.

When adding a new use case:

1. Put the needed contract in `core:domain`.
2. Put the implementation in `core:data`.
3. Bind with Hilt in the appropriate data/network/database/datastore module.
4. Feature modules should inject only use cases, not Retrofit/DAO/DataStore directly.

## MVI Conventions

Each screen should use the existing file pattern:

```text
FeatureContract.kt     # State, Event, Effect
FeatureViewModel.kt    # BaseViewModel + MviViewModel<State, Event, Effect>
FeatureScreen.kt       # Route + Screen + Content
FeatureComponents.kt   # small composables, reusable within the feature
FeatureNavigation.kt   # route object, graph, navigate helpers
```

Rules:

- `State` is an immutable `data class` representing everything needed to render UI.
- `Event` is a sealed class/object representing actions from UI to ViewModel.
- `Effect` is a sealed class/object for one-shot events: navigate, snackbar/dialog, toast, error message.
- ViewModels expose `StateFlow<State>` and `SharedFlow<Effect>` through `MviViewModel`.
- UI calls `onEvent(...)`; composables must not call use cases/repositories directly.
- Route collects `uiState` with `collectAsStateWithLifecycle()`.
- Route collects `effect` inside `repeatOnLifecycle(Lifecycle.State.STARTED)`.
- Global loading/error goes through `LocalGlobalAppState` following the current pattern.
- ViewModel coroutines should use `executeSafe { ... }` when exceptions are possible.

## UI Conventions

- UI uses Jetpack Compose + Material 3.
- Components use theme tokens from `core:designsystem/theme`.
- Feature modules must not define their own global palette/theme.
- Route keeps wiring logic; `Screen`/`Content` should be mostly pure composables for easier previews/tests.
- Add a preview when creating new UI or changing layout significantly.
- Use `stringResource`/string resources for real user-facing text.
- Icons/vectors inside a feature must use the correct module resource prefix.
- Avoid adding new app-level state inside features; the app shell currently owns the bottom bar, offline banner, global dialogs, and global loading.

## Related Docs

- `docs/ARCHITECTURE.md`: module architecture, Clean Architecture, MVI.
- `docs/CODING_CONVENTIONS.md`: Kotlin/Gradle/Compose/testing conventions.
- `docs/FEATURE_TEMPLATE.md`: checklist and skeleton for adding features/screens.
- `docs/UI_GUIDELINES.md`: Compose/design system guidelines.
- `docs/INTERVIEW_NOTES.md`: Vietnamese interview notes for explaining the project.
- `docs/ARCHITECTURE_DEBT.md`: tracked architecture debt.
