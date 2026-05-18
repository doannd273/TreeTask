Read `.codex/skills/android-feature/SKILL.md` in full.
Then read `docs/FEATURE_TEMPLATE.md` and `docs/ARCHITECTURE.md`.

Feature to scaffold: $ARGUMENTS

Follow every step in the skill and feature template:

1. Create `feature/<name>/build.gradle.kts` với convention plugins, namespace, resourcePrefix, missingDimensionStrategy.
2. Register `:feature:<name>` trong `settings.gradle.kts`.
3. Create package layout:
   - `navigation/<Name>Navigation.kt`
   - `ui/<name>/<Name>Contract.kt`
   - `ui/<name>/<Name>ViewModel.kt`
   - `ui/<name>/<Name>Screen.kt`
   - `ui/<name>/<Name>Components.kt`
4. Add EN + VI string resource stubs tại `res/values/strings.xml` và `res/values-vi/strings.xml`.
5. Wire navigation graph vào `app/.../TreeTaskNavHost.kt`.
6. Add feature dependency vào `app/build.gradle.kts`.
7. Verify:
   ```
   ./gradlew :feature:<name>:compileDebugKotlin
   ```
   Nếu chạm app wiring:
   ```
   ./gradlew assembleDebug
   ```

Do NOT add unused dependencies. Do NOT hardcode versions — dùng `libs.versions.toml`.
