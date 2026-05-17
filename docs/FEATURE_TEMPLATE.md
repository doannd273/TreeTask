# Feature Template

Use this checklist when adding a new feature or a large new screen to an existing feature.

## 1. Create a New Feature Module

Add the module to `settings.gradle.kts`:

```kotlin
include(":feature:example")
```

`feature/example/build.gradle.kts`:

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

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.designsystem)
    implementation(projects.core.model)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
}
```

Keep only dependencies that are actually used. If the feature does not use typed navigation routes/serialization, remove `kotlin.serialization`.

## 2. Package Layout

```text
feature/example/src/main/java/com/doannd3/treetask/feature/example/
├── navigation/
│   └── ExampleNavigation.kt
└── ui/
    └── example/
        ├── ExampleContract.kt
        ├── ExampleViewModel.kt
        ├── ExampleScreen.kt
        └── ExampleComponents.kt
```

Resources:

```text
feature/example/src/main/res/
├── values/strings.xml
├── values-vi/strings.xml
└── drawable/example_*.xml
```

## 3. Contract

```kotlin
package com.doannd3.treetask.feature.example.ui.example

import com.doannd3.treetask.core.common.UiText

data class ExampleState(
    val isLoading: Boolean = false,
)

sealed class ExampleEvent {
    data object Refresh : ExampleEvent()
    data object PrimaryActionClick : ExampleEvent()
}

sealed class ExampleEffect {
    data class ShowErrorMessage(val message: UiText) : ExampleEffect()
    data object NavigateBack : ExampleEffect()
}
```

## 4. ViewModel

```kotlin
@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val exampleUseCase: ExampleUseCase,
) : BaseViewModel(),
    MviViewModel<ExampleState, ExampleEvent, ExampleEffect> {

    private val _uiState = MutableStateFlow(ExampleState())
    override val uiState: StateFlow<ExampleState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<ExampleEffect>()
    override val effect: SharedFlow<ExampleEffect> = _effect.asSharedFlow()

    override fun onEvent(event: ExampleEvent) {
        when (event) {
            ExampleEvent.Refresh -> refresh()
            ExampleEvent.PrimaryActionClick -> submit()
        }
    }

    private fun refresh() {
        executeSafe {
            _uiState.update { it.copy(isLoading = true) }
            // Call use case here.
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun submit() {
        executeSafe {
            _effect.emit(ExampleEffect.NavigateBack)
        }
    }

    override fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }
}
```

## 5. Screen

```kotlin
@Composable
fun ExampleRoute(
    viewModel: ExampleViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val globalAppState = LocalGlobalAppState.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    ExampleScreen(
        state = state,
        onEvent = viewModel::onEvent,
    )

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    ExampleEffect.NavigateBack -> onBack()
                    is ExampleEffect.ShowErrorMessage -> {
                        globalAppState.showError(effect.message.asString(context))
                    }
                }
            }
        }
    }

    LaunchedEffect(viewModel.baseErrorEffect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.baseErrorEffect.collect { message ->
                globalAppState.showError(message.asString(context))
            }
        }
    }

    LaunchedEffect(state.isLoading) {
        if (state.isLoading) globalAppState.showLoading() else globalAppState.hideLoading()
    }
}

@Composable
fun ExampleScreen(
    state: ExampleState,
    onEvent: (ExampleEvent) -> Unit,
) {
    ExampleContent(
        state = state,
        onEvent = onEvent,
    )
}
```

## 6. Navigation

```kotlin
@Serializable
data object ExampleGraphDestination

@Serializable
data object ExampleRouteDestination

fun NavController.navigateToExample(navOptions: NavOptions? = null) {
    navigate(ExampleRouteDestination, navOptions)
}

fun NavGraphBuilder.exampleGraph(
    onBack: () -> Unit,
) {
    navigation<ExampleGraphDestination>(
        startDestination = ExampleRouteDestination,
    ) {
        composable<ExampleRouteDestination> {
            ExampleRoute(onBack = onBack)
        }
    }
}
```

Then wire the graph in `app/src/main/java/com/treestudio/treetask/navigation/TreeTaskNavHost.kt` and add the feature dependency to `app/build.gradle.kts`.

## 7. Domain/Data Checklist

If the feature needs new data:

- Add a model to `core:model` if the model is shared across layers.
- Add a repository contract to `core:domain/repository`.
- Add a use case to `core:domain/usecase/<area>`.
- Add DTO/service types to `core:network` if a new API is needed.
- Add entity/DAO/migration to `core:database` if local caching is needed.
- Add repository implementation/mapper to `core:data`.
- Bind the implementation in the appropriate Hilt module.
- Feature modules should inject use cases only.

## 8. Test Checklist

- Use case tests in `core:domain/src/test`.
- Repository implementation tests in `core:data/src/test`.
- ViewModel tests when state/effect logic has important branches.
- Mapper tests when mapping has complex date/status/null handling.
- Compose UI tests only when the UI workflow is important and stable.

## 9. Done Checklist

- `./gradlew spotlessCheck`
- `./gradlew detekt`
- `./gradlew testDebugUnitTest`
- `./gradlew assembleDebug`
- String resources have EN/VI entries when user-facing.
- Resource prefix matches the module.
- No unused dependencies are added.
- Clean Architecture dependency direction is preserved.
- Docs are updated if the feature adds a new convention.
