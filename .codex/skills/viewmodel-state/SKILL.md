---
name: viewmodel-state
description: Use when creating or refactoring TreeTask MVI ViewModel state, events, effects, loading/error handling, StateFlow/SharedFlow exposure, and BaseViewModel integration.
---

# ViewModel State Skill

Use this skill when adding or changing a TreeTask MVI ViewModel, especially repeated `State`/`Event`/`Effect` and `BaseViewModel` wiring.

## Read First

- Inspect the closest existing ViewModel and contract in the same feature.
- Check `core/common/src/main/java/com/doannd3/treetask/core/common/MviViewModel.kt`.
- Check `core/common/src/main/java/com/doannd3/treetask/core/common/BaseViewModel.kt`.
- Keep the implementation aligned with the feature's existing route/effect collection style.

## Contract Rules

Each screen should have a contract file:

```text
<Screen>Contract.kt
```

Use:

- `<Screen>State`: immutable `data class` with all render data.
- `<Screen>Event`: sealed class/object for UI intents.
- `<Screen>Effect`: sealed class/object for one-shot outputs such as navigation or error message.

Guidelines:

- Use `data object` for no-payload `Event`/`Effect` variants.
- State should not hold `Context`, `NavController`, repositories, services, DAO, or storage implementation.
- Event should carry data from UI; avoid putting callbacks in events unless necessary.
- Effect should not be persisted in State.
- Use `UiText` for user-facing messages emitted by ViewModel.
- Keep server/runtime messages as `UiText.DynamicString`; prefer `UiText.StringResource` for app-defined fallback messages.

## ViewModel Shape

Use this shape unless the existing feature has a stronger local pattern:

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
            ExampleEvent.Submit -> submit()
        }
    }

    override fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }
}
```

## State Updates

- Use `_uiState.update { it.copy(...) }`.
- Guard duplicate submits with `if (uiState.value.isLoading) return`.
- Prefer deriving data in Flow pipelines when state depends on multiple inputs.
- Use `distinctUntilChanged()` before expensive downstream work when observing state slices.
- For Paging, keep the current project pattern: expose `Flow<PagingData<Model>>` in state only when the surrounding feature already does this.

## Coroutine and Error Handling

- Use `executeSafe { ... }` for coroutine work that can throw.
- Simple fire-and-forget effect emissions that cannot throw may use `viewModelScope.launch { _effect.emit(...) }`.
- Update loading state before and after long-running work.
- Let `BaseViewModel` handle unexpected coroutine exceptions through `baseErrorEffect`.
- Convert expected domain/data errors into feature effects:

```kotlin
when (result) {
    is ApiResult.Success -> {
        _effect.emit(ExampleEffect.NavigateNext)
    }
    is ApiResult.Error -> {
        _effect.emit(ExampleEffect.ShowErrorMessage(result.message ?: fallbackMessage))
    }
}
```

For navigation after a success/error dialog, keep the decision explicit in the ViewModel: Route sends an acknowledgement event, ViewModel emits the navigation effect, and Route executes navigation.

## Dependency Rules

- Inject use cases from `core:domain`.
- Do not inject Retrofit services, Room DAOs, DataStore implementations, or Android `Context` into feature ViewModels unless the existing architecture explicitly requires it.
- If the ViewModel needs data that currently lives in storage/network, add or use a domain-facing use case instead.

## Route Expectations

The route should:

- Collect `uiState` with `collectAsStateWithLifecycle()`.
- Collect `effect` using `repeatOnLifecycle(Lifecycle.State.STARTED)`.
- Collect `baseErrorEffect` and bridge it to `LocalGlobalAppState`.
- Reflect `state.isLoading` in global loading only when that is the screen's current pattern.

## Verification

For ViewModel-only changes:

```bash
./gradlew :feature:<name>:compileDebugKotlin
```

When behavior branches were added, add or update tests and run:

```bash
./gradlew testDebugUnitTest
```
