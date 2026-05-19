Read `.codex/skills/compose-screen/SKILL.md` in full.
Then read `docs/UI_GUIDELINES.md`.

Target: $ARGUMENTS
(format: `<feature> <ScreenName>`, ví dụ: `tasks AddTask`)

Follow the skill:

1. Inspect screen gần nhất trong cùng feature trước khi viết code.
2. Tạo 4 files tại `feature/<feature>/src/main/java/.../ui/<screen>/`:
   - `<Screen>Contract.kt` — State (data class), Event (sealed), Effect (sealed)
   - `<Screen>ViewModel.kt` — @HiltViewModel, BaseViewModel, MviViewModel, executeSafe
   - `<Screen>Screen.kt` — Route (wires VM, lifecycle, effects) + Screen (pure composable)
   - `<Screen>Components.kt` — feature-local reusable composables
3. Route:
   - collectAsStateWithLifecycle() cho state
   - repeatOnLifecycle(STARTED) cho effect
   - Bridge baseErrorEffect → LocalGlobalAppState
4. Screen/Content: pure composable, không ref ViewModel/NavController/repository.
5. String resources: thêm cả EN (`res/values/strings.xml`) và VI (`res/values-vi/strings.xml`).
6. `@Preview` cho Screen/Content composable.
7. State đủ 4 case: loading, error, empty, success.
8. Accessibility: contentDescription cho actionable icons, null cho decorative.
9. Verify:
   ```
   ./gradlew :feature:<feature>:compileDebugKotlin
   ```
   Nếu broad changes:
   ```
   ./gradlew assembleDebug spotlessCheck detekt
   ```
