Read `.codex/skills/viewmodel-state/SKILL.md` in full.

Target: $ARGUMENTS
(format: `<feature> <ScreenName>`, ví dụ: `auth Login`)

Follow the skill:

1. Read `MviViewModel.kt` và `BaseViewModel.kt` trong `core/common` trước.
2. Inspect ViewModel gần nhất trong cùng feature module.
3. Contract file `<Screen>Contract.kt`:
   - `<Screen>State`: immutable data class, toàn bộ render data kể cả `isLoading`.
   - `<Screen>Event`: sealed class cho UI intents.
   - `<Screen>Effect`: sealed class cho one-shot behavior (navigate, snackbar, toast, error).
   - Dùng `UiText` cho messages; `UiText.StringResource` cho app-defined fallback copy.
4. ViewModel:
   - `@HiltViewModel`, `@Inject constructor`, inject use cases từ `core:domain` only.
   - Extend `BaseViewModel()`, implement `MviViewModel<State, Event, Effect>`.
   - Private `MutableStateFlow`/`MutableSharedFlow`; public `StateFlow`/`SharedFlow`.
   - Coroutine work có thể throw → dùng `executeSafe { ... }`.
   - Guard duplicate submit: `if (uiState.value.isLoading) return`.
   - Handle `ApiResult.Success` / `ApiResult.Error` → emit effects cho cả hai path.
5. KHÔNG inject Retrofit, DAO, DataStore, Android Context vào ViewModel.
6. Verify:
   ```
   ./gradlew :feature:<feature>:compileDebugKotlin
   ```
   Nếu thêm behavior branches:
   ```
   ./gradlew :feature:<feature>:testDebugUnitTest
   ```
