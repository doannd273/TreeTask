---
name: compose-screen
description: Use when adding or refactoring a Jetpack Compose screen in TreeTask, including repeated MVI Contract/ViewModel/Screen/Components files, lifecycle-aware state/effect collection, navigation callbacks, previews, and string resources.
---

# Compose Screen Skill

Use this skill to create or update a TreeTask Compose screen using the local MVI pattern.

## Before Editing

1. Inspect the closest existing screen in the same feature.
2. Prefer the current file layout:

```text
ui/<screen>/
├── <Screen>Contract.kt
├── <Screen>ViewModel.kt
├── <Screen>Screen.kt
└── <Screen>Components.kt
```

3. Keep Route wiring separate from pure render composables.
4. Do not add business logic to composables.

## Contract Pattern

Create:

- `<Screen>State`: immutable `data class`, all data needed to render UI.
- `<Screen>Event`: sealed class/object for user intents.
- `<Screen>Effect`: sealed class/object for one-shot behavior such as navigation or error message.

Use `UiText` for user-facing messages emitted from ViewModel.

## ViewModel Pattern

Use:

- `@HiltViewModel`
- constructor `@Inject`
- `BaseViewModel`
- `MviViewModel<State, Event, Effect>`
- private `MutableStateFlow`, public `StateFlow`
- private `MutableSharedFlow`, public `SharedFlow`
- `executeSafe { ... }` for coroutine work that can fail

ViewModel should inject use cases from `core:domain`, not services, DAO, or storage implementation.

## Route Pattern

Route composable should:

- Obtain ViewModel with `hiltViewModel()`.
- Collect state with `collectAsStateWithLifecycle()`.
- Render `<Screen>Screen(...)`.
- Collect `effect` inside `repeatOnLifecycle(Lifecycle.State.STARTED)`.
- Collect `baseErrorEffect` and bridge to `LocalGlobalAppState`.
- Reflect long-running loading through `LocalGlobalAppState` only when that matches existing screen behavior.
- Call navigation callbacks passed from the feature graph or app graph.

## Screen and Components

Screen/content composables should:

- Accept state and callbacks as parameters.
- Avoid `NavController`, repository, use case, or ViewModel dependency.
- Use `MaterialTheme` and components from `core:designsystem`.
- Put reusable feature-local UI in `<Screen>Components.kt`.
- Use `stringResource` for real text.
- Add EN and VI strings when adding user-facing copy.
- Add previews for new or meaningfully changed layouts.

## UI Checklist

- Loading, error, empty, and success states are represented.
- Action icons have meaningful `contentDescription`; decorative icons use `null`.
- Lists use stable keys.
- Paging screens handle refresh/append loading and retry errors.
- Text does not overflow common mobile widths.
- Resource names use the module prefix, for example `tasks_`, `auth_`, `profile_`.

## Verification

Run the narrowest meaningful check:

```bash
./gradlew :feature:<name>:compileDebugKotlin
```

For broad UI/app wiring changes:

```bash
./gradlew assembleDebug
./gradlew spotlessCheck
./gradlew detekt
```
