# Coding Conventions

This document describes the conventions currently used in TreeTask. If this document and the current code disagree, follow the current code first and update the docs afterwards.

## Kotlin

- Use Kotlin 2.0.x with JVM target 17.
- Prefer immutable data: `val`, `data class`, and sealed class/object for state/event/effect.
- Use `data object` for sealed `Event`/`Effect` variants that carry no payload.
- Use Hilt constructor injection when a class needs dependencies.
- For constructor-injected Kotlin classes, keep the header normalized as `class Foo @Inject constructor(...)`.
  Do not intentionally split `class`, `@Inject`, and `constructor` across separate indentation levels.
- Prefer this member order when it improves scanability:
  - constructor dependencies in the class header
  - state/effect blocks, keeping each private backing property adjacent to its public exposed property
  - init
  - public API / event entry points
  - overrides
  - private implementation
  - companion object

  This is a readability guideline, not a strict auto-format or lint rule.
  Keep closely related logic together when that improves clarity.
- Use named arguments when calling service/repository/storage boundary methods with request or domain parameters.
  For request bodies, always name the body parameter, for example `request = LoginRequest(...)` or `body = ResetPasswordRequest(...)`.
- Build multi-field request/body DTOs before calling service methods.
  Keep the service call focused on the boundary call, for example:

  ```kotlin
  val request =
      CreateConversationRequest(
          type = ConversationType.PRIVATE.type,
          name = name,
          participantIds = otherUserIds,
      )

  val result = chatService.createConversation(request = request)
  ```

  Inline request construction is acceptable only for trivial one-field DTOs that stay on one readable line.
- In repositories, validate required success response data with one guard-clause style:
  `val data = result.data ?: return missingResponseDataError()`, then
  `val model = data.toModelOrNull() ?: return missingResponseDataError()`.
  Avoid separate `if (data == null)` blocks in mapper/response handling paths.
- In repositories, use `mapSuccessResult { ... }` to reuse only the repeated `ApiResult.Error` pass-through branch.
  Keep required-data validation, mapping, success messages, persistence, cache writes, and database transactions explicit inside the success lambda.
- Do not catch exceptions in UI with broad `catch (Exception)` if repository/use case boundaries already normalize errors with `ApiResult`.
- Use cases must validate all required input before calling repositories.
  Validate required strings, ids, lists, enum-like values, date formats, pagination, and any business preconditions owned by the use case.
  Normalize input there too, such as `trim()` and `distinct()`, then call the repository only with validated values.
  Invalid input must return `validationError(...)` and must not call the repository.
  Repositories validate backend response contracts after network/database calls.
- ViewModel coroutines that can fail should use `executeSafe { ... }` from `BaseViewModel`.
- Fire-and-forget effect emissions that cannot throw may use `viewModelScope.launch { _effect.emit(...) }`.
- Expose public flows as read-only types: `StateFlow`, `SharedFlow`, or `Flow`.
- Keep mutable flows private: `_uiState`, `_effect`.
- Package names follow module ownership:
  - `com.treestudio.treetask` for `app`
  - `com.doannd3.treetask.core.<module>` for `core`
  - `com.doannd3.treetask.feature.<feature>` for `feature`

## Naming

- Classes, interfaces, objects, enums, and composables use `PascalCase`.
- Functions, properties, parameters, and local variables use `camelCase`.
- Constants use `UPPER_SNAKE_CASE`.
- Boolean names should prefer readable prefixes such as `is`, `has`, `should`, or `can` when they describe state or capability.
- Backing properties should stay explicit and adjacent to the exposed property, for example `_uiState` next to `uiState`.
- Avoid vague names such as `data`, `item`, `model`, or `result` when a more specific domain name is available.
- Screen route composable: `<Name>Route`.
- Render composable: `<Name>Screen`, `<Name>Content`.
- Small component: domain-specific name such as `TaskItem`, `SearchTaskInput`.
- MVI contract: `<Name>State`, `<Name>Event`, `<Name>Effect`.
- ViewModel: `<Name>ViewModel`.
- Use case: verb phrase + `UseCase`, for example `GetTasksUseCase`, `ObserveCurrentUserUseCase`.
- Repository interface in `core:domain`: `<Thing>Repository`.
- Repository implementation in `core:data`: `<Thing>RepositoryImpl`.
- Mapper files live near implementation details: `TaskMapper.kt`, `UserMapper.kt`.
- Module resources must use a prefix:
  - `auth_`, `tasks_`, `chat_`, `stats_`, `profile_`
  - `common_`, `designsystem_`, `network_`, `database_`, etc.

## Gradle

- Use Kotlin DSL.
- Use the version catalog at `gradle/libs.versions.toml`.
- Do not hardcode dependency versions in module build files.
- Use convention plugins from `build-logic` when available:

```kotlin
plugins {
    alias(libs.plugins.treetask.android.library)
    alias(libs.plugins.treetask.android.compose)
    alias(libs.plugins.treetask.android.hilt)
    alias(libs.plugins.treetask.android.desugar)
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
}
```

- Use dependency scopes intentionally:
  - `implementation` by default.
  - `api` only when a type is part of the module public API.
  - `debugImplementation` for tooling/debug-only dependencies.
  - `testImplementation` for host unit tests.
  - `androidTestImplementation` for instrumented/Compose UI tests.
- Modules that consume flavored dependencies need `missingDimensionStrategy("environment", "dev")`.

## Formatting and Static Analysis

Spotless/ktlint formats Kotlin and Gradle Kotlin DSL.

TreeTask uses a pinned ktlint formatter version plus a curated rule override set in
`build-logic/convention/src/main/kotlin/AndroidSpotlessConventionPlugin.kt`.
Keep formatter policy centralized there instead of adding per-module overrides.

For the reusable formatter recipe and migration notes, see `docs/FORMATTER_CONVENTION.md`.

```bash
./gradlew spotlessCheck
./gradlew spotlessApply
./gradlew detekt
```

Before opening a PR or handing off meaningful changes:

```bash
./scripts/run-ci.sh
```

For docs-only changes, Gradle is not required. `git diff --check` is enough to catch whitespace issues.

## Compose

- Route composables wire ViewModel/navigation/effects.
- Route composables are the feature's public entry points; `Screen`, `Content`, and workflow step composables should be `internal` unless another module must call them.
- Screen/Content composables receive `state` and callbacks and should not inject dependencies.
- Use `collectAsStateWithLifecycle()` for state from ViewModel.
- Use `repeatOnLifecycle(Lifecycle.State.STARTED)` for effect collection.
- Do not call use cases or repositories from composables.
- Do not create feature-local global themes/palettes; use `core:designsystem`.
- Put generic reusable UI in `core:designsystem`, not in one feature subpackage and imported by another.
- Generic `core:designsystem` components receive display text as `String` parameters; feature callers resolve `stringResource`.
- Real user-facing text must live in `strings.xml` and `values-vi/strings.xml` when i18n is needed.
- Previews should use simple fake state and should not need network or DI.
- `contentDescription = null` is only for decorative icons. Action icons need meaningful descriptions.

## MVI

Contract example:

```kotlin
data class ExampleState(
    val isLoading: Boolean = false,
)

sealed class ExampleEvent {
    data object Refresh : ExampleEvent()
}

sealed class ExampleEffect {
    data class ShowErrorMessage(val message: UiText) : ExampleEffect()
}
```

Rules:

- State must not contain Android `Context`, `NavController`, repository, DAO, or service instances.
- Event should carry data, not UI callbacks, unless there is a strong reason.
- Effect is for one-shot behavior and should not be stored in State.
- Navigation after a success/error dialog should be explicit: Route sends an acknowledgement event, ViewModel emits the navigation effect, and Route performs navigation.
- Long-running loading can be rendered through State or global loading, following the existing screen pattern.
- Error messages from domain/data should be normalized into `UiText`.

## Data and Domain

- The domain layer defines interfaces/use cases and does not know Retrofit/Room/DataStore implementations.
- The data layer maps:
  - `Response -> Model`
  - `Entity -> Model`
  - `Model/Request -> Entity/Request`
- DTOs must not leave `core:network`/`core:data`.
- Entities must not leave `core:database`/`core:data`.
- Repository implementations return domain models or `ApiResult`.
- Feature modules should not depend directly on `core:network`, `core:database`, or `core:datastore` when a domain path exists.

## Error Handling

- Network/repository boundaries use `ApiResult.Success` or `ApiResult.Error`.
- ViewModels convert errors into `Effect.ShowErrorMessage(UiText)`.
- Unexpected coroutine exceptions use `BaseViewModel` to emit `baseErrorEffect`.
- User-facing fallback strings should use `UiText.StringResource`; do not hardcode Vietnamese text in ViewModels.

## Testing

- Use case tests live in the module that owns the use case, for example `core/domain/src/test`.
- Repository tests live in `core/data/src/test`.
- Shared rules/helpers live in `core:testing`, for example `MainDispatcherRule`.
- Prefer Turbine for Flow tests.
- Use `kotlinx-coroutines-test` for coroutine tests.
- Use MockK when mocks are needed.
- Test names should describe behavior, for example `login returns success when repository succeeds`.

Common commands:

```bash
./gradlew testDebugUnitTest
./gradlew :core:domain:testDebugUnitTest
./gradlew :core:data:testDebugUnitTest
```

## Documentation

- When adding new architecture/module conventions, update `AGENTS.md` and related docs.
- When debt is found and not fixed immediately, add it to `docs/ARCHITECTURE_DEBT.md`.
- When Gradle setup is repeated across modules, prefer moving it into a convention plugin in `build-logic` once the pattern is clear.
