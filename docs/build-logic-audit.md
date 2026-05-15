# Build Logic Audit

This file tracks repeated Gradle setup and cleanup candidates found during module-by-module audit.

## Move To Convention Candidates

### `core:model`

- `coreLibraryDesugaring(libs.desugar.jdk.libs)`
    - Reason: Required because module uses `java.time.Instant`.
    - Candidate plugin: `treetask.android.desugar`
    - Decision: Defer until more modules are audited.

### `core:common`

- Hilt + KSP
    - Reason: Required because module provides dispatcher bindings.
    - Candidate plugin: `treetask.android.hilt`
    - Decision: Move after auditing all Hilt modules.

- `coreLibraryDesugaring(libs.desugar.jdk.libs)`
    - Reason: Required because module uses `java.time`.
    - Candidate plugin: `treetask.android.desugar`
    - Decision: Defer until more modules are audited.

### `core:analytics`

- Hilt + KSP
    - Reason: Required because module binds `AnalyticsHelper` and provides Firebase dependencies through Hilt.
    - Candidate plugin: `treetask.android.hilt`
    - Decision: Move after auditing all Hilt modules.

- Firebase Analytics + Crashlytics runtime dependencies
    - Reason: Required by `FirebaseAnalyticsHelper`.
    - Candidate plugin: none for now.
    - Decision: Keep explicit in `core:analytics`; do not move to a generic convention until more Firebase modules exist.

### `core:designsystem`

- Compose UI foundation setup
    - Reason: Module is the Compose design system/UI foundation and exposes Compose-based public API such as theme, colors, dialogs, loading, link text, debounce click, and debug overlay.
    - Candidate plugin: `treetask.android.compose`
    - Decision: Move repeated Compose plugin/build-feature/dependency setup into a convention after more Compose modules are audited.

### `feature:auth`

- Compose feature UI setup
    - Reason: Auth screens/components are Compose-based and use `@Preview`.
    - Candidate plugin: `treetask.android.compose`
    - Decision: Covered by the planned shared Compose convention.

- Hilt + KSP
    - Reason: Auth ViewModels use `@HiltViewModel` and constructor `@Inject`.
    - Candidate plugin: `treetask.android.hilt`
    - Decision: Move after auditing all Hilt modules.

- Kotlin Serialization for typed Navigation routes
    - Reason: `AuthNavigation.kt` uses `@Serializable` route objects with typed `navigation`/`composable` APIs.
    - Candidate plugin: none yet; possible future navigation/feature convention.
    - Decision: Keep explicit until more feature navigation modules are audited.

### `core:database`

- Room database setup
    - Reason: Module owns Room database, entities, DAO, schema export, and Room KSP compiler setup.
    - Candidate plugin: `treetask.android.room`
    - Decision: Move after auditing all Room/database consumers.

- Hilt + KSP
    - Reason: Module provides `TreeTaskDatabase`, `TaskDao`, and `TaskRemoteKeysDao` through Hilt.
    - Candidate plugin: `treetask.android.hilt`
    - Decision: Move after auditing all Hilt modules.

### `core:datastore`

- Hilt + KSP
    - Reason: Module binds storage contracts and provides encrypted shared preferences through Hilt.
    - Candidate plugin: `treetask.android.hilt`
    - Decision: Move after auditing all Hilt modules.

- DataStore + Security Crypto setup
    - Reason: Module is the only current owner of preferences DataStore and encrypted token storage.
    - Candidate plugin: none for now.
    - Decision: Keep explicit in `core:datastore`; create a dedicated convention only if more modules use DataStore/security crypto.

### `core:network`

- Hilt + KSP
    - Reason: Module provides Retrofit services, OkHttp clients, interceptors, and authenticator through Hilt.
    - Candidate plugin: `treetask.android.hilt`
    - Decision: Move after auditing all Hilt modules.

- Kotlin Serialization
    - Reason: Module owns serializable API envelope models and uses Retrofit Kotlin Serialization converter.
    - Candidate plugin: `treetask.android.serialization`
    - Decision: Create only if more modules need Kotlin Serialization plugin/runtime setup.

- Network environment flavors + `BuildConfig.BASE_URL`
    - Reason: Module defines `environment` flavors and reads base URLs from `local.properties`.
    - Candidate plugin: `treetask.android.environment`
    - Decision: Defer until app/data/network variant setup is reviewed together to avoid hiding flavor coupling.

- Debug network tooling
    - Reason: Module wires Chucker, OkHttp logging, and debug-only network inspection.
    - Candidate plugin: none for now; possible future `treetask.android.network`.
    - Decision: Keep explicit in `core:network` until more network modules exist.

### `core:data`

- Hilt + KSP
    - Reason: Module binds domain repositories, network monitor, and Hilt-injected repository implementations.
    - Candidate plugin: `treetask.android.hilt`
    - Decision: Move after auditing all Hilt modules.

- Hilt WorkManager setup
    - Reason: Module owns `SyncWorker` with `@HiltWorker` and depends on WorkManager integration.
    - Candidate plugin: `treetask.android.hilt.work`
    - Decision: Keep separate from the base Hilt convention because not every Hilt module owns workers.

- `coreLibraryDesugaring(libs.desugar.jdk.libs)`
    - Reason: Module maps task date/time values through `java.time` helpers from `core:common`.
    - Candidate plugin: `treetask.android.desugar`
    - Decision: Move after desugar usage is confirmed across all audited modules.

- Environment variant strategy
    - Reason: Module directly depends on flavored `core:network` and currently declares `missingDimensionStrategy("environment", "dev")`.
    - Candidate plugin: `treetask.android.environment`
    - Decision: Defer until app/data/network variant ownership is reviewed together.

- AndroidX Core KTX usage
    - Reason: Module imports `androidx.core.content.getSystemService` in the network monitor implementation.
    - Candidate plugin: none for now.
    - Decision: Prefer a direct module dependency or replace the extension usage; do not move AndroidX Core KTX into a broad convention unless many modules truly need it.

### Planned convention plugins

- `treetask.android.compose`
    - Apply plugins: `org.jetbrains.kotlin.plugin.compose`.
    - Enable `android.buildFeatures.compose = true`.
    - Add dependencies: Compose BOM, UI, UI graphics, Material3, tooling preview, and debug-only UI tooling.
    - Current candidates: `core:designsystem`, `feature:auth`, `feature:chat`, `feature:profile`, `feature:stats`, `feature:tasks`.
    - Keep module-specific Compose dependencies such as Navigation, Lifecycle Compose, Hilt Navigation Compose, Paging Compose, or Coil Compose explicit unless a narrower convention emerges.

- `treetask.android.hilt`
    - Apply plugins: `com.google.dagger.hilt.android`, `com.google.devtools.ksp`.
    - Add dependencies: `implementation(libs.hilt.android)`, `ksp(libs.hilt.compiler)`.
    - Current candidates: `core:common`, `core:analytics`, `core:database`, `core:datastore`, `core:network`, `core:data`, `feature:auth`.
    - Do not apply to modules without Hilt annotations or generated Hilt code.

- `treetask.android.hilt.work`
    - Apply base Hilt convention if it is not already applied.
    - Add dependencies: `implementation(libs.work.runtime.ktx)`, `implementation(libs.androidx.hilt.work)`, and the required AndroidX Hilt compiler when configured in the version catalog.
    - Current candidates: `core:data`.
    - Keep worker scheduling policies and app startup wiring explicit in the owning app/data module.

- `treetask.android.room`
    - Apply plugins: `com.google.devtools.ksp` if not already applied by another convention.
    - Add dependencies: `implementation(libs.room.runtime)`, `implementation(libs.room.ktx)`, optional `implementation(libs.room.paging)`, and `ksp(libs.room.compiler)`.
    - Configure Room schema export: `ksp { arg("room.schemaLocation", "$projectDir/schemas") }`.
    - Current candidates: `core:database`.
    - Keep `room.paging` optional if future Room modules do not expose `PagingSource`.

- `treetask.android.serialization`
    - Apply plugin: `org.jetbrains.kotlin.plugin.serialization`.
    - Add dependency: `implementation(libs.kotlinx.serialization.json)` only when the module uses serialization runtime directly.
    - Current candidates: `core:network`, `feature:auth`.
    - Keep Retrofit converter or Navigation-specific dependencies explicit unless repeated across multiple modules.

- `treetask.android.environment`
    - Configure shared `environment` flavor dimension and `dev`/`prod` product flavors.
    - Optionally provide helpers for safe quoted `buildConfigField` string values.
    - Current candidates: `core:network`, `core:data`, app module variant setup, and any module that directly consumes flavored dependencies.
    - Decision: Defer until variant ownership is clear.

- `treetask.android.desugar`
    - Enable `android.compileOptions.isCoreLibraryDesugaringEnabled = true`.
    - Add dependency: `coreLibraryDesugaring(libs.desugar.jdk.libs)`.
    - Current candidates: `core:model`, `core:common`, `core:data`.
    - Apply only to modules using `java.time` or APIs that require core library desugaring.

- `treetask.android.unit.test`
    - Add dependencies: `testImplementation(projects.core.testing)` or direct `junit`, `mockk`, `truth`, `turbine`, `kotlinx-coroutines-test`.
    - Candidate modules: modules with host unit tests such as `core:domain`, `core:data`, `app`.
    - Decision: Defer until test dependency cleanup is complete.

## Cleanup Candidates

### `core:permission`

- Hilt + KSP
    - Reason: Module does not use `@Inject`, `@Module`, `@InstallIn`, `@HiltViewModel`, or generated Hilt code.
    - Decision: Remove `alias(libs.plugins.hilt)`, `alias(libs.plugins.ksp)`, `api(libs.hilt.android)`, and `ksp(libs.hilt.compiler)`.

- `implementation(libs.timber)`
    - Reason: Module does not call `Timber`.
    - Decision: Remove dependency.

- `buildFeatures.buildConfig = true`
    - Reason: Module does not use generated `BuildConfig`.
    - Decision: Disable/remove this build feature.

### `core:testing`

- Hilt + KSP
    - Reason: Module does not use Hilt annotations or generated Hilt code.
    - Decision: Remove `alias(libs.plugins.hilt)`, `alias(libs.plugins.ksp)`, `api(libs.hilt.android)`, and `ksp(libs.hilt.compiler)`.

- `api(projects.core.model)`
    - Reason: `core:testing` source does not use model types.
    - Decision: Remove dependency if downstream unit tests still compile.

- `api(libs.androidx.junit)` and `api(libs.androidx.espresso.core)`
    - Reason: These are instrumented Android test dependencies, while `core:testing` currently only contains host unit test utilities.
    - Decision: Remove from `core:testing`; keep Android test dependencies in `androidTestImplementation` or a future `core:android-testing`.

### `core:analytics`

- `implementation(libs.timber)`
    - Reason: Module does not call `Timber`.
    - Decision: Remove dependency.

- Unused `Context` parameter in `provideFirebaseCrashlytics`
    - Reason: `FirebaseCrashlytics.getInstance()` does not use `Context`.
    - Decision: Remove parameter from provider method.

### `core:designsystem`

- `implementation(libs.androidx.core.ktx)`
    - Reason: No `androidx.core.*` usage found in `core:designsystem` sources.
    - Decision: Remove if present and verify with `./gradlew --no-configuration-cache :core:designsystem:compileDebugKotlin`. Current Gradle file does not declare this dependency.

- Compose tooling visibility
    - Reason: Tooling preview is only used by internal `@Preview` functions, and full Compose UI tooling should stay debug-only to avoid leaking tooling to consumers.
    - Decision: Use `implementation(libs.androidx.compose.ui.tooling.preview)` while previews remain internal and `debugImplementation(libs.androidx.compose.ui.tooling)`; avoid `api`/`debugApi` for these tooling dependencies.

### `feature:auth`

- `implementation(projects.core.model)`
    - Reason: No direct `core:model` types are used by auth sources; the current auth use cases return `ApiResult<Unit>`.
    - Decision: Remove if `./gradlew --no-configuration-cache :feature:auth:compileDebugKotlin` passes.

- `implementation(projects.core.analytics)`
    - Reason: Auth sources do not use `AnalyticsHelper`, `AnalyticsEvent`, or analytics extension functions directly.
    - Decision: Remove from `feature:auth`; keep app-level analytics wiring in `app`.

- `implementation(libs.androidx.lifecycle.viewmodel.compose)`
    - Reason: Auth screens use `hiltViewModel()` from `androidx.hilt.navigation.compose`, not `viewModel()` from Lifecycle ViewModel Compose.
    - Decision: Remove if `./gradlew --no-configuration-cache :feature:auth:compileDebugKotlin` passes.

- `implementation(libs.timber)`
    - Reason: Auth sources do not call `Timber`.
    - Decision: Remove dependency.

- `defaultConfig.missingDimensionStrategy("environment", "dev")`
    - Reason: `feature:auth` does not directly depend on a flavored module such as `core:network`; the strategy may be leftover from feature-module scaffolding.
    - Decision: Remove only after verifying auth still resolves all variants with `./gradlew --no-configuration-cache :feature:auth:compileDebugKotlin`.

### `feature:*`

- `implementation(projects.core.analytics)`
    - Reason: Feature sources currently do not use `AnalyticsHelper`, `AnalyticsEvent`, or analytics extension functions directly.
    - Decision: Candidate remove from feature modules; keep `core:analytics` dependency in `app` for app-level analytics/session wiring.
