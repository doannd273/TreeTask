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
- Treat `CLAUDE.md` and `.claude/` as read-only reference material for Codex work; never edit them unless the user explicitly asks to modify Claude-specific files.

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
- Use `data object` for no-payload `Event`/`Effect` variants.
- ViewModels expose `StateFlow<State>` and `SharedFlow<Effect>` through `MviViewModel`.
- UI calls `onEvent(...)`; composables must not call use cases/repositories directly.
- Route collects `uiState` with `collectAsStateWithLifecycle()`.
- Route collects `effect` inside `repeatOnLifecycle(Lifecycle.State.STARTED)`.
- Global loading/error goes through `LocalGlobalAppState` following the current pattern.
- ViewModel coroutines should use `executeSafe { ... }` when exceptions are possible.
- Fire-and-forget effect emissions that cannot throw may use `viewModelScope.launch { _effect.emit(...) }`.
- Navigation after dialogs/success acknowledgement should stay ViewModel-driven: Route sends an acknowledgement event, ViewModel emits a navigation effect, and Route executes navigation.

## UI Conventions

- UI uses Jetpack Compose + Material 3.
- Components use theme tokens from `core:designsystem/theme`.
- Feature modules must not define their own global palette/theme.
- Route keeps wiring logic; `Screen`/`Content` should be mostly pure composables for easier previews/tests.
- Feature `Route` composables are the public entry points; `Screen`, `Content`, and workflow step composables that are not exported outside the feature should be `internal`.
- Add a preview when creating new UI or changing layout significantly.
- Use `stringResource`/string resources for real user-facing text.
- Generic reusable UI belongs in `core:designsystem`; avoid sharing components across feature subpackages when the component is not feature-specific.
- Generic `core:designsystem` components should receive display text as `String` parameters and should not read feature string resources internally.
- Icons/vectors inside a feature must use the correct module resource prefix.
- Avoid adding new app-level state inside features; the app shell currently owns the bottom bar, offline banner, global dialogs, and global loading.

## Current Handoff - Auth Reset Password and ApiResult Follow-up

Completed:

- `ApiResult.Success` now supports nullable `data` and optional backend success `message`.
- `ApiResult.Error` separates HTTP status, backend business code, mobile app error code, backend display message, and exception:
  - `statusCode`: HTTP status such as 400, 401, 500.
  - `backendErrorCode`: backend business code parsed from response `code`.
  - `appErrorCode`: mobile-defined error signal such as `MISSING_RESPONSE_DATA`.
  - `message`: backend display message only.
- `ApiResultCall` now preserves nullable success `data`, parses backend `code` into `backendErrorCode`, and keeps backend `message` for display.
- `AppErrorCode.MISSING_RESPONSE_DATA` and `MissingResponseDataException` were added for internal contract errors.
- Repositories now return `appErrorCode = AppErrorCode.MISSING_RESPONSE_DATA` plus `MissingResponseDataException()` when required response data is absent, instead of creating display copy in the data layer.
- Auth ViewModels use `ApiResult.Error.toDisplayMessage(...)` so backend `message` is preferred, then `appErrorCode` mapping, then localized fallback resources.
- Login/register/forgot-password unknown-error fallbacks use `common_error_unknown` resources instead of hardcoded Vietnamese text.
- Register and forgot/reset-password success flows now show backend success `message` when it exists and fall back to localized client resources when the backend omits a success message.
- `UserService.changePassword()` currently returns `ApiResult<Unit>` because the flow relies on backend `message`, not a response data model.
- Reset-password domain flow now validates email, OTP, and new password before calling the repository.
- Forgot-password UI now supports the two-step flow:
  - `EmailInput`: user enters email and requests OTP.
  - `ResetInput`: user enters OTP and new password, can resend OTP, and confirms reset.
- Forgot-password back behavior is aligned across the top app bar and Android system back:
  - from `ResetInput`, back returns to `EmailInput`;
  - from `EmailInput`, back exits to login as before.
- A reusable `OtpInput` composable was added to `core:designsystem` for 6-digit OTP entry. It is controlled by the caller and emits `onOtpComplete` when all digits are filled.
- Forgot-password/register/login components were reviewed and preview coverage was added where useful.
- Detekt config now ignores `@Preview` functions for `UnusedPrivateMember` and `TooManyFunctions`, because Compose previews are IDE/tooling entry points.
- Architecture debt was documented and then resolved for:
  - `Accept-Language` being device-locale backed instead of app-language backed.
  - nullable user response mapping hiding missing required user data.
- A focused `core:common` unit test now covers `ApiResult.Error.toDisplayMessage(...)` priority:
  - backend display `message`;
  - app-defined `appErrorCode` mapping;
  - caller-provided fallback.
- Additional `ApiResult` regression coverage for network parsing, repository missing-data handling, and auth ViewModel transitions is tracked in `docs/ARCHITECTURE_DEBT.md` and intentionally deferred for a dedicated testing pass.
- `Accept-Language` now defaults to `en` through a network header provider instead of `Locale.getDefault()`.
- User response mapping now validates required user payload fields and returns `MISSING_RESPONSE_DATA` before saving token/profile state when required data is absent.

Current status by area:

- `core:common`
  - Contains `ApiResult` error/success model updates and `common_error_otp_empty` resources.
  - Owns the lightweight `NetworkMonitor` contract used by app-level online/offline state.
  - Includes `ApiResultTest` coverage for display-message priority.
  - Depends on `core:testing` for host unit test assertions.
- `core:network`
  - Supports the auth API contract needed by the reset-password flow.
  - `Accept-Language` is provided through `AcceptLanguageProvider`, with `DefaultAcceptLanguageProvider` returning `en`.
  - Future app-language/DataStore-backed language support can replace the provider implementation without changing `CommonHeaderInterceptor`.
- `core:data`
  - Repositories use `appErrorCode = AppErrorCode.MISSING_RESPONSE_DATA` plus `MissingResponseDataException()` for missing required response data.
  - `UserResponse.toUserOrNull()` validates required `id`, `fullName`, and `email` fields before auth/profile repositories save user state.
  - `ConnectivityManagerNetworkMonitor` now uses framework `Context.getSystemService(Context.CONNECTIVITY_SERVICE)` instead of AndroidX Core KTX's `getSystemService` extension.
  - Keeps the Android `ConnectivityManagerNetworkMonitor` implementation and Hilt binding, while the `NetworkMonitor` contract lives in `core:common`.
- `core:domain`
  - `ResetPasswordUseCase` validates email, OTP, and password, then delegates to the auth repository.
  - Unit tests were added/updated for auth use cases.
  - No longer depends on `core:datastore`; domain remains limited to model/common/paging plus test dependencies.
- `core:designsystem`
  - Shared auth-friendly primitives include `OtpInput`, `EmailInput`, `PasswordInput`, and `CommonHeader` when they are generic enough for reuse.
  - Generic input/header components accept display text from callers instead of owning feature copy.
  - `OtpInput` is implemented with previews.
  - `:core:designsystem:compileDebugKotlin` passed.
- `feature:auth`
  - Login/register/forgot-password use resource-based fallbacks for display messages.
  - Register success no longer depends on a non-null backend success `message`.
  - Forgot-password step state, resend OTP, reset success navigation, stale reset-field clearing, and Android system back handling are implemented.
  - `feature:auth` depends on `androidx.activity.compose` for `BackHandler`.
- `config/detekt`
  - Preview-related false positives are ignored for private preview functions and preview count.
- `docs/ARCHITECTURE_DEBT.md`
  - Accept-Language, user-response mapping, `core:domain` datastore dependency, and `core:data` transitive AndroidX Core dependency debt items are marked resolved.
  - Deferred `ApiResult` regression coverage remains tracked for a dedicated testing pass.

Verification status:

- Passed earlier during the ApiResult refactor:
  - `:core:common:compileDebugKotlin`
  - `:core:network:compileDevDebugKotlin`
  - `:core:data:compileDebugKotlin`
- Passed after the latest auth/reset-password UI changes:
  - `:core:designsystem:compileDebugKotlin`
  - `:feature:auth:compileDebugKotlin`
  - `:feature:auth:detekt`
  - `:feature:auth:spotlessCheck`
- Passed during the focused `ApiResultTest` follow-up:
  - `./gradlew :core:common:testDebugUnitTest --tests "*ApiResultTest"`
- Passed during core/feature build verification:
  - all runtime `core:*` compile tasks, including `:core:network:compileDevDebugKotlin`;
  - all `feature:*` compile tasks from `:feature:auth` through `:feature:tasks`.
- Passed after the Accept-Language and UserResponse validation cleanup:
  - `./gradlew :core:network:compileDevDebugKotlin :core:data:compileDebugKotlin :feature:auth:compileDebugKotlin :core:network:spotlessCheck :core:data:spotlessCheck`
- Passed after removing the unused `core:domain -> core:datastore` dependency:
  - `./gradlew :core:domain:compileDebugKotlin :core:domain:testDebugUnitTest :app:compileDevDebugKotlin`
- Passed after removing the transitive AndroidX Core KTX dependency risk from `core:data`:
  - `./gradlew :core:data:compileDebugKotlin :core:data:spotlessCheck :app:compileDevDebugKotlin`
- Passed after moving the `NetworkMonitor` contract from `core:data` to `core:common`:
  - `./gradlew :core:common:compileDebugKotlin :core:data:compileDebugKotlin :app:compileDevDebugKotlin`
  - `./gradlew :core:common:spotlessCheck :core:data:spotlessCheck`
- Developer reported the planned app verification steps completed after the planning pass:
  - `:app:compileDevDebugKotlin`
  - `assembleDebug`
  - `detekt`
  - `spotlessCheck`
  - manual forgot-password smoke flow
  - Because those commands were run outside this handoff update, rerun them before merge if exact terminal output is required.

Next steps:

- Keep the deferred testing pass scoped and tracked in `docs/ARCHITECTURE_DEBT.md`:
  - backend error body `code` -> `backendErrorCode`
  - backend `message` display priority
  - nullable success `data`
  - `MISSING_RESPONSE_DATA` app error path
  - invalid login/register/getProfile user payloads do not save token/profile state
  - email submit moves to `ResetInput`
  - resend OTP calls forgot-password flow, not reset-password flow
  - reset success emits `ResetPasswordSuccess` even when backend success `message` is absent
  - `BackToEmailInput` clears OTP, new password, and password visibility
- Before merge, rerun the exact app/static checks if the latest terminal output is not available in the handoff:
  `./gradlew :app:compileDevDebugKotlin assembleDebug detekt spotlessCheck`
- Next cleanup candidates:
  - replace raw auth UI color constants with Material 3 semantic color tokens.
  - add the deferred auth/domain/ViewModel regression tests tracked in `docs/ARCHITECTURE_DEBT.md`.

Important decisions:

- Backend owns display copy for backend responses; mobile should display backend `message` when present.
- Client fallback success strings are still required because a successful backend response may omit `message`; successful behavior such as navigation must not depend on display copy being present.
- Mobile owns behavior decisions through `backendErrorCode`, `statusCode`, and `appErrorCode`; do not branch on display `message`.
- Mobile-defined/internal errors must use `appErrorCode` and `exception`, not backend `message`.
- `backendErrorCode` remains a `String?` so unknown backend codes do not break older app versions.
- Localized client fallback copy must use string resources such as `common_error_unknown`, not `UiText.DynamicString` with hardcoded language text.
- `ApiResultCall` must not replace missing success `data` with `Unit`; nullable data should remain visible to repository/use-case code.
- Forgot-password remains one route with internal `step` state instead of separate navigation destinations, because email/reset input are one workflow and should share local state without route arguments.
- `ResetInput` back returns to `EmailInput` because it is a workflow step, not a separate app destination.
- `OtpInput` lives in `core:designsystem` as a controlled reusable UI component; feature modules own validation, submission, resend, and navigation behavior.
- Shared generic inputs/headers live in `core:designsystem` when they are not auth-specific; feature modules pass labels/copy in from their own resources.
- Navigation decisions after success dialogs live in the ViewModel through acknowledgement events and navigation effects; Route should not hide navigation behavior inside dialog `onDismiss` lambdas.
- `Accept-Language` belongs behind a synchronous provider because OkHttp interceptors are synchronous; future DataStore language support should update a cached provider value rather than blocking in the interceptor.
- Invalid required user response data is a contract error and must be surfaced as `appErrorCode = MISSING_RESPONSE_DATA`; repositories should validate before saving token/profile state.
- Domain must not depend on storage implementations such as DataStore. If domain needs session/user state later, expose it through domain repository/use-case contracts.
- Prefer framework APIs over adding dependencies when the framework API is sufficient; for example, `ConnectivityManagerNetworkMonitor` uses `Context.getSystemService(Context.CONNECTIVITY_SERVICE)` directly.
- App-level online/offline state should depend on the lightweight `core:common` `NetworkMonitor` contract; Android connectivity implementation details stay in `core:data`.
- `@Preview` functions may stay private because they are not runtime entry points. Detekt ignores them to avoid noisy false positives.

## Related Docs

- `docs/ARCHITECTURE.md`: module architecture, Clean Architecture, MVI.
- `docs/CODING_CONVENTIONS.md`: Kotlin/Gradle/Compose/testing conventions.
- `docs/FEATURE_TEMPLATE.md`: checklist and skeleton for adding features/screens.
- `docs/UI_GUIDELINES.md`: Compose/design system guidelines.
- `docs/INTERVIEW_NOTES.md`: Vietnamese interview notes for explaining the project.
- `docs/ARCHITECTURE_DEBT.md`: tracked architecture debt.
