# CLAUDE.md

Guide for Claude Code working in the TreeTask Android repository.
Keep changes small, architecture-aligned, easy to review, and consistent with existing module boundaries.

## Language and Scope

- Reply in Vietnamese when the user writes in Vietnamese.
- Do not revert or overwrite existing working-tree changes unless explicitly asked.
- Read existing code before editing. Use `rg`/`rg --files` for fast search.
- Keep code changes consistent with the current style; avoid broad refactors unless the task requires them.
- Technical docs in English. `INTERVIEW_NOTES.md` may remain Vietnamese (user-facing interview prep).
- Code, package names, classes, methods, and Gradle IDs follow English/Kotlin conventions.
- **Never auto-edit `.codex/`, `AGENTS.md`, or any file under `docs/` without explicit user instruction.** These are manually curated reference files.

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

Always run from the repository root.

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

Narrow checks:

```bash
./gradlew :feature:tasks:compileDebugKotlin
./gradlew :core:domain:testDebugUnitTest
./gradlew :core:data:testDebugUnitTest
```

Variants:

- `environment` flavor dimension: `dev` and `prod`.
- Dev debug app ID: `com.treestudio.treetask.dev`.
- Release builds need signing config from `local.properties` or env vars.
- Firebase builds need `app/google-services.json`.
- If a module depends on a flavored `core:network` variant, add `missingDimensionStrategy("environment", "dev")`.

## Coding Conventions

- Kotlin JVM target 17; SDK values from `gradle/libs.versions.toml`.
- Use the version catalog `libs.versions.toml`; do not hardcode dependency versions.
- Use convention plugins from `build-logic`: `treetask.android.application`, `treetask.android.library`, `treetask.android.compose`, `treetask.android.hilt`, `treetask.android.desugar`, `treetask.android.detekt`, `treetask.android.spotless`.
- Library/feature resources must use the correct `resourcePrefix`: `auth_`, `tasks_`, `designsystem_`, etc.
- Do not let feature modules call implementation details directly when a domain use case/repository contract exists.
- Do not put user-facing display text in `core:model`; prefer string resources or UI-layer mappers.
- Do not hardcode user-facing strings in composables/ViewModels (except preview/mock data).
- Use `UiText` for messages flowing from ViewModel/use case to UI.
- Use `ApiResult` for network/repository/use case boundaries.

## Clean Architecture

Dependency flow:

```text
feature:* -> core:domain -> core:model
feature:* -> core:common/designsystem/analytics when needed
app       -> feature:* + core:* composition/root wiring
core:data -> core:domain + core:model + core:network + core:database + core:datastore
core:network/database/datastore -> implementation details, not called directly by features
```

Layer roles:

- `core:model`: stable models, no Android UI text/resources.
- `core:domain`: repository interfaces and use cases. No Retrofit, Room, or DataStore implementation.
- `core:data`: repository implementations, DTO/entity/domain mapping, offline-first sync.
- `core:network`: API services, request/response DTOs, OkHttp/Retrofit setup.
- `core:database`: Room entities/DAOs/database.
- `core:datastore`: preference/token/user/device storage.
- `feature:*`: route, screen, component, state/event/effect, ViewModel.
- `app`: app shell, global navigation, application initialization, flavor/signing/build config.

When adding a new use case:

1. Put the contract in `core:domain`.
2. Put the implementation in `core:data`.
3. Bind with Hilt in the appropriate data/network/database/datastore module.
4. Feature modules inject only use cases, not Retrofit/DAO/DataStore directly.

## MVI Conventions

Screen file pattern:

```text
FeatureContract.kt     # State, Event, Effect
FeatureViewModel.kt    # BaseViewModel + MviViewModel<State, Event, Effect>
FeatureScreen.kt       # Route + Screen + Content
FeatureComponents.kt   # small composables reusable within the feature
FeatureNavigation.kt   # route object, graph, navigate helpers
```

Rules:

- `State`: immutable `data class` for everything needed to render UI.
- `Event`: sealed class/object for UI → ViewModel actions.
- `Effect`: sealed class/object for one-shot events (navigate, snackbar, toast, error).
- Use `data object` (not `object`) for Event/Effect variants with no payload — better `toString()` for debugging.
- ViewModels expose `StateFlow<State>` and `SharedFlow<Effect>` through `MviViewModel`.
- UI calls `onEvent(...)`. Composables must not call use cases/repositories directly.
- Route collects `uiState` with `collectAsStateWithLifecycle()`.
- Route collects `effect` inside `repeatOnLifecycle(Lifecycle.State.STARTED)`.
- Global loading/error goes through `LocalGlobalAppState`.
- ViewModel coroutines use `executeSafe { ... }` when exceptions are possible (network, DB).
- Use `viewModelScope.launch { _effect.emit(...) }` for fire-and-forget effect emission that never throws; `executeSafe` is unnecessary overhead here.
- Navigation after a success dialog: Route passes `onDismiss = { viewModel.onEvent(XxxAcknowledged) }` → ViewModel handles → emits `NavigateToX` effect → Route executes. This keeps navigation decisions in ViewModel, not Route.

## UI Conventions

- Jetpack Compose + Material 3.
- Components use theme tokens from `core:designsystem/theme`.
- Feature modules must not define their own global palette/theme.
- Route keeps wiring logic; `Screen`/`Content` should be mostly pure composables.
- `Screen`, `Content`, and step composables not exported outside the feature module must be `internal`.
- Add a preview when creating new UI or changing layout significantly. `@Preview` functions may be `private`.
- Use `stringResource`/string resources for real user-facing text.
- Icons/vectors inside a feature must use the correct module resource prefix.
- Shared generic UI components (inputs, headers, etc.) belong in `core:designsystem`, not in feature subpackages. Cross-subpackage imports within a feature for shared UI is a smell — move to `core:designsystem`.
- Generic reusable components in `core:designsystem` receive display text as a `label: String` parameter; do not hardcode string resources inside `core:designsystem`.
- Avoid adding new app-level state inside features; the app shell owns bottom bar, offline banner, global dialogs, and global loading.

## Key Architectural Decisions

- Backend owns display copy for backend responses; display backend `message` when present.
- Client fallback success strings are required because a successful backend response may omit `message`; behavior (e.g. navigation) must not depend on display copy being present.
- Mobile owns behavior decisions through `backendErrorCode`, `statusCode`, and `appErrorCode`. Do not branch on display `message`.
- Mobile-defined/internal errors use `appErrorCode` and `exception`, not backend `message`.
- `backendErrorCode` is `String?` so unknown backend codes do not break older app versions.
- Localized client fallback copy must use string resources such as `common_error_unknown`, not `UiText.DynamicString` with hardcoded text.
- `ApiResultCall` must not replace missing success `data` with `Unit`; nullable data should remain visible to repository/use-case code.
- Repositories return `appErrorCode = AppErrorCode.MISSING_RESPONSE_DATA` plus `MissingResponseDataException()` when required response data is absent.
- Domain must not depend on storage implementations such as DataStore. Expose session/user state through domain repository/use-case contracts.
- `Accept-Language` is behind a synchronous provider (`AcceptLanguageProvider`); future DataStore-backed language support updates a cached provider value instead of blocking in the interceptor.
- Prefer framework APIs over adding dependencies (e.g. use `Context.getSystemService(Context.CONNECTIVITY_SERVICE)` directly).
- Forgot-password stays one route with internal `step` state (not separate destinations) to share local state without route arguments.
- Navigation decisions after dialogs/success states must live in ViewModel (explicit `NavigateToX` effect), not as implicit `onDismiss` lambdas in Route. This allows future branching (e.g., onboarding) without touching Route.

## Current State — Auth / ApiResult

### Completed

- `ApiResult.Success` supports nullable `data` and optional backend success `message`.
- `ApiResult.Error` separates: `statusCode` (HTTP), `backendErrorCode` (backend business code), `appErrorCode` (mobile-defined signal), `message` (backend display only), `exception`.
- `AppErrorCode.MISSING_RESPONSE_DATA` and `MissingResponseDataException` added for internal contract errors.
- Auth ViewModels use `ApiResult.Error.toDisplayMessage(...)`: backend `message` → `appErrorCode` mapping → localized fallback.
- `Accept-Language` defaults to `en` via `DefaultAcceptLanguageProvider`.
- `UserResponse.toUserOrNull()` validates required `id`, `fullName`, `email` before saving token/profile state.
- **Designsystem refactor**: `EmailInput`, `PasswordInput`, `CommonHeader` extracted to `core:designsystem`. Removed cross-subpackage coupling within `feature:auth`. Obsolete `auth_ic_visibility*.xml` and auth-specific string keys removed.
- **Visibility**: `Screen`, `Content`, step composables across `login`, `register`, `forgotpassword` are `internal`.
- **Navigation ownership**: `Register` and `ForgotPassword` both apply `XxxAcknowledged → NavigateToX` pattern. Navigation decisions are ViewModel-owned.
- **ResendOtp fix**: OTP field is cleared before re-submitting email.
- Forgot-password two-step flow: `EmailInput` step → `ResetInput` step. Back from `ResetInput` returns to `EmailInput`; back from `EmailInput` exits to login.
- Reusable composables in `core:designsystem`: `OtpInput` (6-digit OTP), `EmailInput` (takes `label: String`), `PasswordInput` (visibility toggle, takes `label: String`), `CommonHeader` (back button + title, takes `title: String`).
- Auth `Screen`/`Content`/step composables are `internal`; only the Route function is public.

### Pending / Known Issues

- [ ] **[Medium]** Delete dead resource `feature/auth/src/main/res/drawable/auth_ic_back_left.xml` — both headers that used it were removed.
- [ ] **[Low]** Make `ForgotPasswordPreview` and `ForgotPasswordResetPreview` in `ForgotPasswordScreen.kt` `private`.
- [ ] **[Low]** Add newline at EOF to `core/designsystem/src/main/res/values/strings.xml` (Spotless will flag).
- [ ] **[Low]** Deferred regression tests tracked in `docs/ARCHITECTURE_DEBT.md`.

### Verification before merging

```bash
./gradlew :feature:auth:compileDebugKotlin
./gradlew :app:compileDevDebugKotlin assembleDebug detekt spotlessCheck
```

### Key Decisions

| Decision | Reasoning |
|---|---|
| `XxxAcknowledged → NavigateToX` pattern | Route không được own navigation decision; ViewModel phải emit explicit effect để dễ branching sau này (e.g., onboarding) |
| `data object` cho no-payload Event/Effect | Better `toString()` debugging; Kotlin 1.9+ feature |
| `viewModelScope.launch` cho effect emit | `executeSafe` chỉ dùng khi block có thể throw; `_effect.emit()` không throw |
| Generic inputs vào `core:designsystem` | Cross-subpackage import trong feature là coupling smell; ownership rõ ràng hơn |
| `label: String` param cho designsystem components | `core:designsystem` không sở hữu feature-specific string resources |

## Related Docs

- `docs/ARCHITECTURE.md`: module architecture, Clean Architecture, MVI.
- `docs/CODING_CONVENTIONS.md`: Kotlin/Gradle/Compose/testing conventions.
- `docs/FEATURE_TEMPLATE.md`: checklist and skeleton for adding features/screens.
- `docs/UI_GUIDELINES.md`: Compose/design system guidelines.
- `docs/INTERVIEW_NOTES.md`: Vietnamese interview notes for explaining the project.
- `docs/ARCHITECTURE_DEBT.md`: tracked architecture debt.
