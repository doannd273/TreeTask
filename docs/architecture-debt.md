# Architecture Debt

This file tracks architecture issues found during module-by-module audit. These items are intentionally deferred until the dependency/build-logic audit is complete, so cleanup can stay small and safe.

## `core:model` owns UI-facing labels

- **Location**: `core/model/src/main/java/com/doannd3/treetask/core/model/task/TaskStatus.kt`
- **Issue**: `TaskStatus.label` stores Vietnamese UI text inside the model layer.
- **Impact**: Localization and UI wording become coupled to `core:model`, making model less reusable and harder to keep platform/presentation-neutral.
- **Target solution**: Keep only stable domain/API values in `TaskStatus`; move display label mapping to UI/resource layer, for example `TaskStatusUiMapper` or string resource mapping in feature/designsystem.
- **Priority**: Low
- **Status**: Deferred until audit is complete

## `core:common` mixes pure common and Android presentation concerns

- **Location**: `core/common/src/main/java/com/doannd3/treetask/core/common`
- **Issue**: `core:common` contains pure shared types plus Android/presentation-specific code such as `UiText`, `BaseViewModel`, Android resources, Lifecycle, Timber, and Hilt dispatcher bindings.
- **Impact**: Modules depending on `core:common` inherit Android/presentation dependencies even when they only need pure shared contracts like `ApiResult`.
- **Target solution**: Split responsibilities later, for example `core:common` for pure Kotlin contracts/utilities and `core:presentation` or `core:ui-common` for `UiText`, `BaseViewModel`, resources, and presentation helpers.
- **Priority**: Medium
- **Status**: Deferred until audit is complete

## `core:designsystem` owns app-level state

- **Location**: `core/designsystem/src/main/java/com/doannd3/treetask/core/designsystem/component/GlobalAppState.kt`
- **Issue**: `GlobalAppState` and `LocalGlobalAppState` live in the design system module and are consumed directly by `app` and feature screens for global dialog/loading orchestration.
- **Impact**: Feature modules become coupled to `core:designsystem` for app-level state, while the design system ideally stays focused on stateless UI foundation such as theme, colors, dialogs, loading, link text, click behavior, and debug overlay.
- **Target solution**: Keep this as-is for now. If the presentation layer grows, move app-level state/composition locals to a dedicated `core:presentation` or `core:ui-common` module and leave `core:designsystem` for reusable UI primitives/theme.
- **Priority**: Low
- **Status**: Deferred until UI/presentation boundaries need cleanup

## `feature:auth` duplicates route side-effect wiring

- **Location**: `feature/auth/src/main/java/com/doannd3/treetask/feature/auth/ui`
- **Issue**: Login, register, and forgot-password routes each manually collect feature effects, `baseErrorEffect`, and loading state, then bridge them to `LocalGlobalAppState`.
- **Impact**: Lifecycle/effect glue is repeated across auth screens and can drift as new screens add loading, dialog, or one-shot effect handling.
- **Target solution**: After the audit, consider a small presentation helper for lifecycle-aware effect collection and global loading/error bridging, or move this pattern into a shared `core:presentation`/`core:ui-common` layer.
- **Priority**: Low
- **Status**: Deferred until more feature screens are audited

## `feature:auth` contains hardcoded fallback error text

- **Location**: `feature/auth/src/main/java/com/doannd3/treetask/feature/auth/ui`
- **Issue**: Auth ViewModels fall back to `UiText.DynamicString("Lỗi không xác định")` when a use case returns `ApiResult.Error` without a message.
- **Impact**: User-facing text bypasses string resources and the default English locale, making localization inconsistent.
- **Target solution**: Add a shared `common_error_unknown` or feature-level `auth_error_unknown` string resource and use `UiText.StringResource`.
- **Priority**: Low
- **Status**: Deferred until i18n cleanup

## Android instrumented test utilities are not separated yet

- **Location**: `core/testing/build.gradle.kts`
- **Issue**: `core:testing` is currently closer to a host unit test helper module, but Android instrumented test dependencies/utilities may be needed later.
- **Impact**: If Android test dependencies such as `androidx.test.ext:junit`, Espresso, Compose UI test, or Hilt testing are added to `core:testing`, all unit-test consumers may inherit heavier Android test dependencies unnecessarily.
- **Target solution**: Keep `core:testing` focused on host unit test utilities. When shared instrumented test helpers are needed, create a dedicated `core:android-testing` module and/or a `treetask.android.instrumented.test` convention plugin.
- **Priority**: Low
- **Status**: Deferred until Android test scope grows

## `core:domain` depends on datastore without source usage

- **Location**: `core/domain/build.gradle.kts`
- **Issue**: `core:domain` declares `implementation(projects.core.datastore)` but domain source does not import datastore contracts or implementation types.
- **Impact**: Domain layer is coupled to local storage infrastructure in Gradle even though the code does not need it, weakening clean architecture boundaries.
- **Target solution**: Remove the datastore dependency from `core:domain` if compilation/tests pass. If domain needs user/session state later, expose that through domain repository/use-case contracts rather than depending on datastore directly.
- **Priority**: Medium
- **Status**: Candidate cleanup after module audit

## `feature:tasks` reads user storage directly

- **Location**: `feature/tasks/src/main/java/com/doannd3/treetask/feature/tasks/ui/home/TasksViewModel.kt`
- **Issue**: `TasksViewModel` injects `UserStorage` from `core:datastore` to read the current user profile.
- **Impact**: Feature UI layer knows local storage implementation details, making it harder to test and to change storage/session strategy later.
- **Target solution**: Introduce a domain-facing use case or repository method such as `ObserveCurrentUserUseCase`/`GetCurrentUserIdUseCase`, implemented in `core:data`, then remove `feature:tasks -> core:datastore`.
- **Priority**: Medium
- **Status**: Deferred until task/domain cleanup

## `core:network` depends directly on datastore

- **Location**: `core/network/src/main/java/com/doannd3/treetask/core/network`
- **Issue**: Network interceptors/authenticator depend directly on `TokenStorage` and `DeviceStorage` from `core:datastore`.
- **Impact**: Network transport becomes coupled to the current local storage implementation, making token/device-id strategy harder to change and increasing the dependency surface of `core:network`.
- **Target solution**: Introduce narrow network-facing contracts such as `TokenProvider`, `TokenUpdater`, and `DeviceIdProvider`; bind datastore-backed implementations outside or at the DI boundary so `core:network` no longer needs to know datastore implementation details.
- **Priority**: Medium
- **Status**: Deferred until dependency cleanup after audit

## `core:network` blocks OkHttp threads for token reads

- **Location**: `core/network/src/main/java/com/doannd3/treetask/core/network/auth/AuthInterceptor.kt`
- **Location**: `core/network/src/main/java/com/doannd3/treetask/core/network/auth/AuthAuthenticator.kt`
- **Issue**: Token access and refresh logic use `runBlocking` inside OkHttp interceptor/authenticator paths.
- **Impact**: This is acceptable short-term, but it can block OkHttp dispatcher threads during token/device-id reads or refresh operations, especially when many requests run in parallel.
- **Target solution**: Keep the current implementation until performance issues appear, then consider cached synchronous token/device-id access, a dedicated blocking-safe token data source, or a tighter auth session component that avoids repeated Flow collection inside interceptors.
- **Priority**: Medium
- **Status**: Deferred until network performance/auth cleanup
