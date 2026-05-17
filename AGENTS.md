# AGENTS.md

Hướng dẫn này dành cho Codex/AI agent và developer khi làm việc trong repo TreeTask.
Mục tiêu là giữ thay đổi nhỏ, đúng kiến trúc, dễ review, và không phá các ranh giới module đã có.

## Ngôn ngữ và phạm vi

- Trao đổi với người dùng bằng tiếng Việt khi người dùng dùng tiếng Việt.
- Không revert hoặc ghi đè thay đổi có sẵn trong working tree nếu không được yêu cầu rõ.
- Ưu tiên đọc code hiện tại trước khi sửa. Dùng `rg`/`rg --files` để tìm kiếm nhanh.
- Khi sửa code, giữ đúng style hiện có; không refactor lan rộng nếu task không yêu cầu.
- File docs có thể viết tiếng Việt. Code, package, class, method, Gradle id giữ theo tiếng Anh/Kotlin convention.

## Cấu trúc repo

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
├── docs/                # Architecture, convention, feature template, UI notes
├── gradle/              # Wrapper and version catalog
└── scripts/             # Local CI/lint/format scripts
```

## Build và test

Luôn chạy từ repo root.

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew detekt
./gradlew spotlessCheck
```

Command tiện dụng:

```bash
./scripts/run-ci.sh          # spotlessCheck + detekt + testDebugUnitTest + assembleDebug
./scripts/check-lint.sh      # spotlessCheck + detekt
./scripts/apply-spotless.sh  # format Kotlin/KTS bằng Spotless
```

Variant quan trọng:

- App có flavor dimension `environment` với `dev` và `prod`.
- Debug app id của dev là `com.treestudio.treetask.dev`.
- Release cần signing config từ `local.properties` hoặc environment variables.
- Firebase cần `app/google-services.json` khi build các phần phụ thuộc Google Services/Firebase.

Khi cần kiểm tra hẹp:

```bash
./gradlew :feature:tasks:compileDebugKotlin
./gradlew :core:domain:testDebugUnitTest
./gradlew :core:data:testDebugUnitTest
```

Nếu module phụ thuộc flavor của `core:network`, giữ hoặc thêm `missingDimensionStrategy("environment", "dev")` theo pattern hiện tại.

## Coding convention

- Kotlin JVM target là 17; compile/min/target SDK lấy từ `gradle/libs.versions.toml`.
- Dùng version catalog `libs.versions.toml`; không hardcode version dependency trong module.
- Dùng convention plugins trong `build-logic`:
  - `treetask.android.application`
  - `treetask.android.library`
  - `treetask.android.compose`
  - `treetask.android.hilt`
  - `treetask.android.desugar`
  - `treetask.android.detekt`
  - `treetask.android.spotless`
- Resource của library/feature phải có `resourcePrefix` tương ứng: `auth_`, `tasks_`, `designsystem_`, v.v.
- Không để feature module gọi thẳng implementation chi tiết nếu đã có domain use case/repository contract.
- Không đưa text hiển thị người dùng vào `core:model`; ưu tiên string resources hoặc mapper ở UI layer.
- Không hardcode user-facing string trong composable/ViewModel, trừ dữ liệu mock preview tạm thời.
- Dùng `UiText` cho message đi từ ViewModel/use case lên UI.
- Dùng `ApiResult` cho boundary network/repository/use case hiện tại.

## Clean Architecture

Luồng phụ thuộc mong muốn:

```text
feature:* -> core:domain -> core:model
feature:* -> core:common/designsystem/analytics khi cần
app       -> feature:* + core:* composition/root wiring
core:data -> core:domain + core:model + core:network + core:database + core:datastore
core:network/database/datastore -> implementation detail, không bị feature gọi trực tiếp
```

Vai trò từng layer:

- `core:model`: model ổn định, không biết Android UI text/resource.
- `core:domain`: repository interface và use case. Không chứa Retrofit, Room, DataStore implementation.
- `core:data`: triển khai repository, mapping DTO/entity/domain, offline-first sync, Paging `RemoteMediator`.
- `core:network`: API service, request/response DTO, OkHttp/Retrofit setup.
- `core:database`: Room entity/DAO/database.
- `core:datastore`: preference/token/user/device storage.
- `feature:*`: route, screen, component, state/event/effect, ViewModel.
- `app`: app shell, global navigation, application init, flavor/signing/build config.

Khi thêm use case mới:

1. Đặt contract cần thiết ở `core:domain`.
2. Đặt implementation ở `core:data`.
3. Bind bằng Hilt trong module data/network/database/datastore phù hợp.
4. Feature chỉ inject use case, không inject Retrofit/DAO/DataStore trực tiếp.

## MVI convention

Mỗi màn hình nên dùng bộ file theo pattern hiện có:

```text
FeatureContract.kt     # State, Event, Effect
FeatureViewModel.kt    # BaseViewModel + MviViewModel<State, Event, Effect>
FeatureScreen.kt       # Route + Screen + Content
FeatureComponents.kt   # composable nhỏ, reusable trong feature
FeatureNavigation.kt   # route object, graph, navigate helpers
```

Quy tắc:

- `State` là immutable `data class`, đại diện toàn bộ UI state cần render.
- `Event` là sealed class/object, đại diện hành động từ UI vào ViewModel.
- `Effect` là sealed class/object, dùng cho one-shot event: navigate, snackbar/dialog, toast, error message.
- ViewModel expose `StateFlow<State>` và `SharedFlow<Effect>` thông qua `MviViewModel`.
- UI gọi `onEvent(...)`; không gọi use case/repository trực tiếp trong composable.
- Route collect `uiState` bằng `collectAsStateWithLifecycle()`.
- Route collect `effect` trong `repeatOnLifecycle(Lifecycle.State.STARTED)`.
- Loading/error global đi qua `LocalGlobalAppState` như pattern hiện tại.
- Coroutine trong ViewModel nên đi qua `executeSafe { ... }` khi có rủi ro exception.

## UI convention

- UI dùng Jetpack Compose + Material 3.
- Component dùng theme từ `core:designsystem/theme`.
- Feature không tự định nghĩa palette/theme toàn cục.
- Route giữ logic wiring; `Screen`/`Content` ưu tiên pure composable để preview/test dễ hơn.
- Mỗi màn hình có preview khi tạo UI mới hoặc sửa layout đáng kể.
- Dùng `stringResource`/resource string cho text thật.
- Icon/vector trong feature phải có prefix resource đúng module.
- Tránh đặt app-level state mới trong feature; app shell đang quản lý bottom bar, offline banner, global dialog/loading.

## Docs liên quan

- `docs/ARCHITECTURE.md`: kiến trúc module, clean architecture, MVI.
- `docs/CODING_CONVENTIONS.md`: Kotlin/Gradle/Compose/testing conventions.
- `docs/FEATURE_TEMPLATE.md`: checklist và skeleton khi thêm feature/screen.
- `docs/UI_GUIDELINES.md`: guideline Compose/design system.
- `docs/INTERVIEW_NOTES.md`: ghi chú giải thích project trong phỏng vấn.
- `docs/ARCHITECTURE_DEBT.md`: debt kiến trúc đang được theo dõi.
