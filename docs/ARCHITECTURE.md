# Architecture

TreeTask is a modular, offline-first Android application built with Clean Architecture and MVI. The current architecture is inspired by Now in Android: a thin app shell, independent feature modules, and core modules split by responsibility.

## Tech Stack

- Kotlin 2.0, JVM 17
- Jetpack Compose, Material 3
- Hilt, Kotlin Coroutines, Flow
- Navigation Compose with typed route objects using Kotlin Serialization in feature navigation
- Retrofit, OkHttp, kotlinx.serialization
- Room, Paging 3, RemoteMediator
- Preferences DataStore and encrypted/token storage
- Firebase Analytics, Crashlytics, Performance
- Detekt, Spotless, JUnit4, MockK, Truth, Turbine, Coroutines Test

## Module Map

```text
app
├── feature:auth
├── feature:tasks
├── feature:chat
├── feature:stats
├── feature:profile
└── core modules

feature:*
├── core:domain
├── core:common
├── core:designsystem
├── core:model
└── optional feature-specific libraries

core:data
├── core:domain
├── core:model
├── core:network
├── core:database
└── core:datastore
```

## Layer Responsibilities

| Layer | Module | Responsibility |
| --- | --- | --- |
| App shell | `app` | `MainActivity`, `TreeTaskApp`, global navigation, bottom bar, offline banner, app initialization, flavors/signing |
| Presentation | `feature:*` | Route, screen, component, MVI contract, ViewModel, feature navigation graph |
| Domain | `core:domain` | Repository contracts and use cases |
| Model | `core:model` | Pure domain models, enums, and value objects shared across layers |
| Data | `core:data` | Repository implementations, mappers, offline-first sync, workers, network monitor implementation |
| Network | `core:network` | Retrofit services, request/response DTOs, OkHttp interceptors/authenticator |
| Local DB | `core:database` | Room database, DAO, entities, schema export |
| Local prefs | `core:datastore` | Preferences DataStore, token/user/device storage |
| Shared presentation/common | `core:common` | `ApiResult`, `UiText`, `BaseViewModel`, `MviViewModel`, dispatcher DI, lightweight app-facing contracts such as `NetworkMonitor` |
| Design system | `core:designsystem` | Theme, reusable Compose components, global dialog/loading primitives |
| Testing | `core:testing` | Shared host unit test rules/helpers |

## Dependency Rule

Main rule: dependencies point inward and should not leak implementation details outward.

```text
UI -> Domain -> Model
Data -> Domain + Model
Data -> Network/Database/Datastore
App -> Feature + Core composition
```

Avoid:

- Feature modules injecting `TaskService`, `TaskDao`, `TokenStorage`, or `UserStorage` when a domain use case can be used instead.
- `core:domain` depending on implementations such as Retrofit, Room, or DataStore.
- `core:model` containing UI labels, Android resources, or localization concerns.
- `core:network` returning DTOs directly to UI.

Prefer:

- Feature modules injecting use cases from `core:domain`.
- Data layer mapping DTO/entity types into domain models.
- UI messages flowing through `UiText` and string resources.
- Hilt bindings living in the module that owns the implementation.

## Request/Data Flow

Task list example:

```text
TasksScreen
-> TasksEvent.SearchChanged / FilterSelected
-> TasksViewModel
-> GetTasksUseCase
-> TaskRepository
-> TaskRepositoryImpl
-> Pager + TaskRemoteMediator
-> TaskService + TaskDao
-> TaskEntity/TaskResponse mapped to Task
-> StateFlow<TasksState>
-> Compose UI render
```

Auth flow:

```text
LoginScreen
-> LoginEvent.SubmitLogin
-> LoginViewModel
-> LoginUseCase
-> AuthRepository
-> AuthRepositoryImpl
-> AuthService + TokenStorage/UserStorage
-> LoginEffect.NavigateToHome or ShowErrorMessage
```

## MVI Pattern

Each screen uses three concepts:

- `State`: immutable render data that is stable enough for Compose.
- `Event`: user intents/actions sent from UI to ViewModel.
- `Effect`: one-shot side effects such as navigation, dialog/snackbar, or error messages.

Use `data object` for no-payload `Event` and `Effect` variants. Route composables should be the public UI entry points; `Screen`, `Content`, and workflow step composables should stay `internal` unless another module must call them.

File pattern:

```text
ui/<screen>/
├── <Screen>Contract.kt
├── <Screen>ViewModel.kt
├── <Screen>Screen.kt
└── <Screen>Components.kt
```

ViewModel:

```kotlin
@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val exampleUseCase: ExampleUseCase,
) : BaseViewModel(),
    MviViewModel<ExampleState, ExampleEvent, ExampleEffect> {
    private val _uiState = MutableStateFlow(ExampleState())
    override val uiState = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<ExampleEffect>()
    override val effect = _effect.asSharedFlow()

    override fun onEvent(event: ExampleEvent) {
        when (event) {
            ExampleEvent.Submit -> submit()
        }
    }
}
```

Route composable:

- Obtain the ViewModel with `hiltViewModel()`.
- Collect state with `collectAsStateWithLifecycle()`.
- Collect effects with `repeatOnLifecycle(Lifecycle.State.STARTED)`.
- Bridge global loading/error through `LocalGlobalAppState`.
- For dialog dismissal that should navigate, send an acknowledgement event to the ViewModel, let the ViewModel emit a navigation effect, then execute navigation in the Route.

## Navigation

- The app module owns `TreeTaskNavHost`.
- Each feature exposes its graph and navigation helpers from `feature/<name>/navigation`.
- Top-level destinations are managed by `app/navigation/TopLevelDestination.kt`.
- The auth graph clears its back stack after successful login/register so users cannot navigate back to auth screens.
- Success/error acknowledgement behavior belongs in the ViewModel as events/effects; Route lambdas should not own hidden business navigation decisions.

## Shared UI Components

- Generic reusable Compose components belong in `core:designsystem`.
- Feature-specific components stay inside the owning feature.
- Avoid importing generic UI from one feature subpackage into another; move it to `core:designsystem` once reuse is real.
- Generic design-system components receive display copy as `String` parameters. Feature callers resolve `stringResource` so design system components do not depend on feature resources.

## Offline-First

The task list currently uses `Pager` + `RemoteMediator`:

- Network loads from `TaskService`.
- Local cache is stored in Room.
- UI observes `PagingData<Task>`.
- Background sync is handled by `SyncWorker`.

When expanding offline-first behavior:

- The repository decides the source of truth.
- DAO/entity types must not leak into feature modules.
- Query/filter cache should be scoped by `userId`, `status`, and `keyword`.
- Add migrations/schema updates when Room entities change.

## App Startup and Global State

- `TreeTaskApplication` and initializers are responsible for app-level service initialization.
- `MainViewModel` chooses `startDestination` based on token/profile cache.
- `TreeTaskApp` renders the offline banner, bottom bar, global loading, and global dialogs.
- Session expiration is observed at the app/root layer and routes the user back to the auth graph.

## Architecture Debt

Known debt is tracked in `docs/ARCHITECTURE_DEBT.md`. When a task touches related debt:

- If the fix is small, safe, and in scope, it may be handled.
- If it is broad or changes large boundaries, document it and avoid surprise refactors.
- When adding a new module, avoid repeating debt already listed there.
