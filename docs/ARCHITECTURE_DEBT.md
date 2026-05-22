# Architecture Debt

This file tracks architecture issues and tech debt found during module-by-module audits. These items are intentionally recorded for later cleanup so each cleanup can stay small, safe, and reviewable.

## `core:model` Owns UI-Facing Labels

- **Location**: `core/model/src/main/java/com/doannd3/treetask/core/model/task/TaskStatus.kt`
- **Issue**: `TaskStatus.label` stores Vietnamese UI text inside the model layer.
- **Impact**: Localization and UI wording become coupled to `core:model`, making the model less reusable and harder to keep platform/presentation-neutral.
- **Target solution**: Keep only stable domain/API values in `TaskStatus`; move display label mapping to the UI/resource layer, for example `TaskStatusUiMapper` or string resource mapping in feature/designsystem.
- **Priority**: Low
- **Status**: Deferred until audit is complete

## `core:common` Mixes Pure Common and Android Presentation Concerns

- **Location**: `core/common/src/main/java/com/doannd3/treetask/core/common`
- **Issue**: `core:common` contains pure shared types plus Android/presentation-specific code such as `UiText`, `BaseViewModel`, Android resources, Lifecycle, Timber, and Hilt dispatcher bindings.
- **Impact**: Modules depending on `core:common` inherit Android/presentation dependencies even when they only need pure shared contracts like `ApiResult`.
- **Target solution**: Split responsibilities later, for example `core:common` for pure Kotlin contracts/utilities and `core:presentation` or `core:ui-common` for `UiText`, `BaseViewModel`, resources, and presentation helpers.
- **Priority**: Medium
- **Status**: Deferred until audit is complete

## `core:designsystem` Owns App-Level State

- **Location**: `core/designsystem/src/main/java/com/doannd3/treetask/core/designsystem/component/GlobalAppState.kt`
- **Issue**: `GlobalAppState` and `LocalGlobalAppState` live in the design system module and are consumed directly by `app` and feature screens for global dialog/loading orchestration.
- **Impact**: Feature modules become coupled to `core:designsystem` for app-level state, while the design system ideally stays focused on stateless UI foundations such as theme, colors, dialogs, loading, link text, click behavior, and debug overlay.
- **Target solution**: Keep this as-is for now. If the presentation layer grows, move app-level state/composition locals to a dedicated `core:presentation` or `core:ui-common` module and leave `core:designsystem` for reusable UI primitives/theme.
- **Priority**: Low
- **Status**: Deferred until UI/presentation boundaries need cleanup

## `feature:auth` Duplicates Route Side-Effect Wiring

- **Location**: `feature/auth/src/main/java/com/doannd3/treetask/feature/auth/ui`
- **Issue**: Login, register, and forgot-password routes each manually collect feature effects, `baseErrorEffect`, and loading state, then bridge them to `LocalGlobalAppState`.
- **Impact**: Lifecycle/effect glue is repeated across auth screens and can drift as new screens add loading, dialog, or one-shot effect handling.
- **Target solution**: After the audit, consider a small presentation helper for lifecycle-aware effect collection and global loading/error bridging, or move this pattern into a shared `core:presentation`/`core:ui-common` layer.
- **Priority**: Low
- **Status**: Deferred until more feature screens are audited

## Auth UI Uses Raw Color Constants Instead of Material 3 Semantic Tokens

- **Location**: `feature/auth/src/main/java/com/doannd3/treetask/feature/auth/ui/login/LoginComponents.kt`
- **Location**: `feature/auth/src/main/java/com/doannd3/treetask/feature/auth/ui/register/RegisterComponents.kt`
- **Location**: `feature/auth/src/main/java/com/doannd3/treetask/feature/auth/ui/forgotpassword/ForgotPasswordComponents.kt`
- **Location**: `core/designsystem/src/main/java/com/doannd3/treetask/core/designsystem/component/OtpInput.kt`
- **Issue**: Auth UI components previously used raw color constants such as `Purple40`, `Black`, `Gray`, and `White` for text, backgrounds, and cursor color instead of `MaterialTheme.colorScheme` semantic tokens.
- **Impact**: Auth UI was effectively locked to a light-mode palette. Dark theme, dynamic color, and future theme adjustments could render incorrect contrast or visually inconsistent surfaces.
- **Target solution**: Replace raw constants with semantic tokens such as `MaterialTheme.colorScheme.primary`, `onBackground`, `onSurfaceVariant`, `surface`, and matching cursor/error colors. Verify the login, register, forgot-password, and OTP states in light and dark theme.
- **Resolution**: Login/register/forgot-password components and `OtpInput` now use `MaterialTheme.colorScheme` semantic tokens for text, links, borders, cursor, error, and disabled states.
- **Priority**: Medium
- **Status**: Resolved

## `feature:auth` Contains Hardcoded Fallback Error Text

- **Location**: `feature/auth/src/main/java/com/doannd3/treetask/feature/auth/ui`
- **Issue**: Auth ViewModels fall back to a hardcoded Vietnamese unknown-error `UiText.DynamicString` when a use case returns `ApiResult.Error` without a message.
- **Impact**: User-facing text bypasses string resources and the default English locale, making localization inconsistent.
- **Target solution**: Add a shared `common_error_unknown` or feature-level `auth_error_unknown` string resource and use `UiText.StringResource`.
- **Priority**: Low
- **Status**: Deferred until i18n cleanup

## Android Instrumented Test Utilities Are Not Separated Yet

- **Location**: `core/testing/build.gradle.kts`
- **Issue**: `core:testing` is currently closer to a host unit test helper module, but Android instrumented test dependencies/utilities may be needed later.
- **Impact**: If Android test dependencies such as `androidx.test.ext:junit`, Espresso, Compose UI test, or Hilt testing are added to `core:testing`, all unit-test consumers may inherit heavier Android test dependencies unnecessarily.
- **Target solution**: Keep `core:testing` focused on host unit test utilities. When shared instrumented test helpers are needed, create a dedicated `core:android-testing` module and/or a `treetask.android.instrumented.test` convention plugin.
- **Priority**: Low
- **Status**: Deferred until Android test scope grows

## ApiResult Contract Regression Coverage Was Incomplete

- **Location**: `core/common/src/main/java/com/doannd3/treetask/core/common/ApiResult.kt`
- **Location**: `core/network/src/main/java/com/doannd3/treetask/core/network/util/ApiResultCall.kt`
- **Location**: `core/data/src/main/java/com/doannd3/treetask/core/data/respository`
- **Location**: `feature/auth/src/main/java/com/doannd3/treetask/feature/auth/ui`
- **Issue**: The new `ApiResult` contract initially had common-layer coverage for display-message priority and forgot-password ViewModel behavior coverage, but network parsing and repository missing-data handling were not fully covered by focused unit tests.
- **Impact**: Regressions in backend `code` parsing, nullable success `data`, `MISSING_RESPONSE_DATA`, or invalid user payload side effects could slip through compile/build checks.
- **Target solution**: Add focused tests for `ApiResultCall` backend `code` -> `backendErrorCode`, nullable success `data`, repository missing required response data, and invalid login/register/getProfile user payloads not saving token/profile state.
- **Resolution**: Added `ApiResultCallTest` coverage for backend error-body `code` -> `backendErrorCode`, nullable success `data`, and backend message/code mapping on unsuccessful API bodies. Added repository tests for missing auth/profile response data and invalid login/register/getProfile user payloads, verifying `MISSING_RESPONSE_DATA` plus no token/profile save side effects.
- **Priority**: Medium
- **Status**: Resolved

## Auth Validation-Fail Tests Do Not Verify Repository Is Not Called

- **Location**: `core/domain/src/test/java/com/doannd3/treetask/core/domain/usecase/auth/LoginUseCaseTest.kt`
- **Location**: `core/domain/src/test/java/com/doannd3/treetask/core/domain/usecase/auth/RegisterUseCaseTest.kt`
- **Location**: `core/domain/src/test/java/com/doannd3/treetask/core/domain/usecase/auth/ForgotPasswordUseCaseTest.kt`
- **Issue**: Login/register validation-fail tests previously asserted the validation error but did not verify that `AuthRepository` was not called. `ResetPasswordUseCaseTest` already used the stronger `coVerify(exactly = 0)` pattern.
- **Impact**: A future regression could have called the repository after local validation failed, while these tests continued to pass if they only asserted the returned validation error.
- **Target solution**: Add `coVerify(exactly = 0)` assertions for the relevant repository calls in validation-fail tests, matching the reset-password use-case test pattern.
- **Resolution**: Added `coVerify(exactly = 0)` assertions to login empty-email/password, register empty-email/full-name/password, and forgot-password empty-email validation-fail tests.
- **Priority**: Low
- **Status**: Resolved

## Auth ViewModel State and Effect Coverage Is Missing

- **Location**: `feature/auth/src/main/java/com/doannd3/treetask/feature/auth/ui`
- **Issue**: Forgot-password ViewModel now has focused tests for the critical multi-step flow, but login/register ViewModel behavior tests are still minimal/skeleton-only.
- **Impact**: Compile checks can still miss regressions in login/register loading, success navigation/dialog effects, and error message emission.
- **Target solution**: Keep the forgot-password ViewModel tests focused on state/effect behavior. Add login/register ViewModel tests when those flows are refactored or when their loading/effect behavior changes.
- **Resolution**: Added `ForgotPasswordViewModelTest` coverage for email submit moving to `ResetInput`, resend OTP calling forgot-password flow, reset success emitting `ResetPasswordSuccess`, `BackToEmailInput` clearing reset fields, and double-submit being blocked while loading.
- **Priority**: Low
- **Status**: Partially resolved - forgot-password covered; login/register behavior tests deferred

## `core:domain` Depended on Datastore Without Source Usage

- **Location**: `core/domain/build.gradle.kts`
- **Issue**: `core:domain` previously declared `implementation(projects.core.datastore)` but domain source did not import datastore contracts or implementation types.
- **Impact**: Domain layer is coupled to local storage infrastructure in Gradle even though the code does not need it, weakening Clean Architecture boundaries.
- **Target solution**: Remove the datastore dependency from `core:domain` if compilation/tests pass. If domain needs user/session state later, expose that through domain repository/use-case contracts rather than depending on datastore directly.
- **Resolution**: Removed the unused `core:domain -> core:datastore` Gradle dependency. Domain now depends only on model/common/paging plus test dependencies.
- **Priority**: Medium
- **Status**: Resolved

## `core:network` Depends Directly on Datastore

- **Location**: `core/network/src/main/java/com/doannd3/treetask/core/network`
- **Issue**: Network interceptors/authenticator depend directly on `TokenStorage` and `DeviceStorage` from `core:datastore`.
- **Impact**: Network transport becomes coupled to the current local storage implementation, making token/device-id strategy harder to change and increasing the dependency surface of `core:network`.
- **Target solution**: Introduce narrow network-facing contracts such as `TokenProvider`, `TokenUpdater`, and `DeviceIdProvider`; bind datastore-backed implementations outside or at the DI boundary so `core:network` no longer needs to know datastore implementation details.
- **Priority**: Medium
- **Status**: Deferred until dependency cleanup after audit

## `core:network` Blocks OkHttp Threads for Token Reads

- **Location**: `core/network/src/main/java/com/doannd3/treetask/core/network/auth/AuthInterceptor.kt`
- **Location**: `core/network/src/main/java/com/doannd3/treetask/core/network/auth/AuthAuthenticator.kt`
- **Issue**: Token access and refresh logic use `runBlocking` inside OkHttp interceptor/authenticator paths.
- **Impact**: This is acceptable short-term, but it can block OkHttp dispatcher threads during token/device-id reads or refresh operations, especially when many requests run in parallel.
- **Target solution**: Keep the current implementation until performance issues appear, then consider cached synchronous token/device-id access, a dedicated blocking-safe token data source, or a tighter auth session component that avoids repeated Flow collection inside interceptors.
- **Priority**: Medium
- **Status**: Deferred until network performance/auth cleanup

## `core:network` Accept-Language Was Not App-Language Backed

- **Location**: `core/network/src/main/java/com/doannd3/treetask/core/network/interceptor/CommonHeaderInterceptor.kt`
- **Issue**: `CommonHeaderInterceptor` previously set `Accept-Language` from `Locale.getDefault().language`, while the API contract should default to `en` and eventually use an app-controlled language source.
- **Impact**: Backend display messages can vary with the device locale instead of the app's selected/default language, making error/success copy harder to reason about, test, and keep consistent across environments.
- **Target solution**: Introduce a narrow language provider for network headers with `en` as the default. Later, back that provider with DataStore or a cached app-language source without coupling the interceptor directly to UI settings.
- **Resolution**: `AcceptLanguageProvider` and `DefaultAcceptLanguageProvider` now provide a stable default `en`, and `CommonHeaderInterceptor` reads `Accept-Language` from the provider instead of `Locale.getDefault()`.
- **Priority**: Medium
- **Status**: Resolved

## `core:data` Relied on a Transitive AndroidX Core Dependency

- **Location**: `core/data/build.gradle.kts`
- **Location**: `core/data/src/main/java/com/doannd3/treetask/core/data/util/ConnectivityManagerNetworkMonitor.kt`
- **Issue**: `ConnectivityManagerNetworkMonitor` previously imported `androidx.core.content.getSystemService`, but `core:data` did not declare `androidx.core:core-ktx` directly.
- **Impact**: Compilation may currently pass because another dependency leaks AndroidX Core transitively, but future dependency cleanup/convention changes can break `core:data` unexpectedly.
- **Target solution**: Either add a direct `implementation(libs.androidx.core.ktx)` dependency to `core:data`, or replace the extension usage with the framework API so the module no longer needs AndroidX Core KTX.
- **Resolution**: Replaced the AndroidX Core KTX `getSystemService` extension with the framework `Context.getSystemService(Context.CONNECTIVITY_SERVICE)` API, so `core:data` no longer relies on a transitive AndroidX Core KTX dependency for `ConnectivityManagerNetworkMonitor`.
- **Priority**: Low
- **Status**: Resolved

## `core:data` Task Paging Cache Is Not Query-Scoped

- **Location**: `core/data/src/main/java/com/doannd3/treetask/core/data/respository/TaskRepositoryImpl.kt`
- **Location**: `core/data/src/main/java/com/doannd3/treetask/core/data/respository/TaskRemoteMediator.kt`
- **Issue**: `TaskRepositoryImpl.getTasks()` receives `status` and `keyword`, but the local `PagingSource` reads only by `userId`; remote keys are also keyed by task id rather than by user/filter/query.
- **Impact**: Search/filter state can become inconsistent with cached data, and remote mediator keys can collide when the same task list is loaded under different query/status combinations.
- **Target solution**: Scope the local task query and remote keys by `userId`, `status`, and `keyword`, or introduce a query-scoped paging cache strategy before expanding task filters/search.
- **Priority**: Medium
- **Status**: Deferred until task module/data paging cleanup

## `core:data` User Mapping Hid Missing Required User Data

- **Location**: `core/data/src/main/java/com/doannd3/treetask/core/data/model/UserMapper.kt`
- **Location**: `core/data/src/main/java/com/doannd3/treetask/core/data/respository/AuthRepositoryImpl.kt`
- **Location**: `core/data/src/main/java/com/doannd3/treetask/core/data/respository/UserRepositoryImpl.kt`
- **Issue**: `UserResponse?.toUser()` previously accepted a nullable response and mapped missing user fields to empty strings, so auth/profile flows could persist an invalid domain user instead of surfacing a contract error.
- **Impact**: Missing or malformed backend user payloads become silent data corruption, which makes downstream profile/session bugs harder to diagnose.
- **Target solution**: Replace nullable user mapping with explicit required-field validation in repositories. When required user data is absent, return `ApiResult.Error(appErrorCode = AppErrorCode.MISSING_RESPONSE_DATA, exception = MissingResponseDataException())` instead of creating an empty domain user.
- **Resolution**: `UserResponse.toUserOrNull()` now requires a non-null response and returns `null` when required `id`, `fullName`, or `email` fields are blank or missing. Auth/profile repositories convert invalid required user payloads into `MISSING_RESPONSE_DATA` errors before saving token/profile state.
- **Priority**: Medium
- **Status**: Resolved

## `NetworkMonitor` Contract Was Exposed From `core:data`

- **Location**: `core/common/src/main/java/com/doannd3/treetask/core/common/network/NetworkMonitor.kt`
- **Location**: `core/data/src/main/java/com/doannd3/treetask/core/data/util/ConnectivityManagerNetworkMonitor.kt`
- **Location**: `app/src/main/java/com/treestudio/treetask/MainViewModel.kt`
- **Issue**: The `app` module previously depended on `core:data` to consume the `NetworkMonitor` contract even though the app only needed an online/offline signal.
- **Impact**: App-level state was coupled to the data implementation module, making it harder to remove or refactor `core:data` dependencies independently.
- **Target solution**: Move the `NetworkMonitor` interface to a smaller contract module such as `core:common`, `core:network-monitor`, or future `core:presentation`, while keeping `ConnectivityManagerNetworkMonitor` implementation and Hilt binding in an Android implementation module.
- **Resolution**: Moved `NetworkMonitor` to `core:common.network`. `MainViewModel` now imports the lightweight contract from `core:common`, while `ConnectivityManagerNetworkMonitor` and the Hilt binding remain in `core:data`.
- **Priority**: Medium
- **Status**: Resolved
