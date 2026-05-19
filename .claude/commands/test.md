Read `.codex/skills/test-writing/SKILL.md` in full.

Target: $ARGUMENTS
(ví dụ: `LoginUseCase`, `feature:auth LoginViewModel`, `core:data AuthRepository login`)

Follow the skill:

1. Read `core/testing/src/main/java/com/doannd3/treetask/core/testing/util/MainDispatcherRule.kt` trước.
2. Inspect existing tests trong cùng module trước khi viết mới.
3. Dùng đúng test stack: JUnit4, MockK, Truth, Turbine, kotlinx-coroutines-test, core:testing.
4. Đặt test đúng module:
   - Use case tests → `core/domain/src/test/...`
   - Repository/mapper tests → `core/data/src/test/...`
   - ViewModel tests → `feature/<name>/src/test/...`
5. Tên test: backtick, mô tả behavior (không phải implementation).
   Ví dụ: `` `login with valid input calls repository and returns success` ``
6. ViewModel tests: MainDispatcherRule, trigger qua `onEvent(...)`, assert `uiState.value`, Turbine cho `effect`.
7. Flow tests: dùng Turbine; không gọi `awaitComplete()` trên `SharedFlow`.
8. Dùng Truth: `assertThat(value).isEqualTo(expected)`.
9. Cover: success path, error path, validation branches, duplicate-submit guard khi có.
10. Verify narrow nhất trước:
    ```
    ./gradlew :core:domain:testDebugUnitTest
    ./gradlew :core:data:testDebugUnitTest
    ./gradlew :feature:<name>:testDebugUnitTest
    ```
    Report rõ nếu có blocker (Firebase config, local secrets thiếu).
