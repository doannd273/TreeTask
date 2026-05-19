Read `.codex/skills/code-review/SKILL.md` in full.

Scope: $ARGUMENTS
(Để trống = working-tree changes; cung cấp tên branch để so với main/develop)

Thực hiện review pass theo skill:

1. Gather changes:
   ```
   git status --short && git diff --stat && git diff
   ```
   Nếu có branch: `git diff main...<branch>`

2. Inspect touched files với `rg` khi cần thêm context.

3. Chạy TreeTask Review Checklist từ skill:
   - **Architecture**: feature modules inject use cases, không inject services/DAOs/DataStore; domain clean; DTOs/entities đúng layer; không circular coupling.
   - **MVI**: State immutable; Event là UI intents; Effect là one-shot; StateFlow/SharedFlow là read-only; `executeSafe` được dùng.
   - **Compose**: Route handle VM/lifecycle/navigation; Screen/Content pure; effects lifecycle-aware; string resources; loading/error/empty states handled; accessibility labels.
   - **Gradle**: version catalog; convention plugins; dependency scope minimal; resourcePrefix; missingDimensionStrategy khi cần.
   - **Tests**: behavior branches có tests; Flow/coroutine behavior tested với Turbine/coroutines-test.

4. Chạy verification — report rõ lệnh nào NOT run và lý do:
   ```
   ./gradlew detekt
   ./gradlew spotlessCheck
   ./gradlew testDebugUnitTest
   ```

5. Trả kết quả theo format:
   ```
   Findings
   - [Severity] file:line — Issue, impact, suggested fix.

   Open Questions
   - Assumptions cần clarify.

   Summary
   - Reviewed scope, commands run, residual risk.
   ```

Không thay đổi code trừ khi được yêu cầu.
