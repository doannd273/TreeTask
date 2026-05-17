---
name: test-writing
description: Use when adding or updating TreeTask tests for use cases, repositories, mappers, Flow/coroutine behavior, ViewModels, and regression coverage with JUnit4, MockK, Truth, Turbine, and kotlinx-coroutines-test.
---

# Test Writing Skill

Use this skill when writing or updating tests in TreeTask.

## Read First

- Inspect nearby tests before adding new ones.
- Use `core/testing/src/main/java/com/doannd3/treetask/core/testing/util/MainDispatcherRule.kt` for ViewModel/coroutine tests that need `Dispatchers.Main`.
- Prefer focused tests that cover behavior and regression risk rather than broad implementation details.

## Test Stack

Use the project stack:

- JUnit4
- MockK
- Truth
- Turbine for Flow assertions
- `kotlinx-coroutines-test` and `runTest`
- `core:testing` for shared test utilities

## Placement

- Use case tests: `core/domain/src/test/...`
- Repository implementation tests: `core/data/src/test/...`
- Mapper tests: the module that owns the mapper, usually `core:data/src/test/...`
- Feature ViewModel tests: the feature module's `src/test/...` when feature test dependencies exist or are added intentionally.
- Avoid Android instrumented tests unless the user explicitly needs UI/device behavior.

## Naming

Use descriptive backtick test names:

```kotlin
@Test
fun `login with valid input calls repository and returns success`() = runTest {
}
```

Prefer behavior wording:

- `returns error when email is empty`
- `emits navigation effect when submit succeeds`
- `saves token when login succeeds`
- `does not call repository when already loading`

## Use Case Tests

Pattern:

```kotlin
class ExampleUseCaseTest {
    private val repository: ExampleRepository = mockk()
    private lateinit var useCase: ExampleUseCase

    @Before
    fun setUp() {
        useCase = ExampleUseCase(repository)
    }

    @Test
    fun `valid input calls repository`() = runTest {
        coEvery { repository.doWork("value") } returns ApiResult.Success(Unit)

        val result = useCase("value")

        assertThat(result).isInstanceOf(ApiResult.Success::class.java)
        coVerify(exactly = 1) { repository.doWork("value") }
    }
}
```

Cover validation branches, trimming/normalization, repository success, and repository error passthrough when relevant.

## Repository Tests

Repository tests should verify externally visible behavior:

- API request is called with expected values.
- Token/user/cache writes happen on success.
- Error results are returned or propagated correctly.
- Flow signals are transformed correctly.
- Mappers handle null/date/status fields safely.

Use relaxed mocks only for collaborators where exact calls are not the focus. Verify important side effects with `coVerify`.

## Flow Tests

Use Turbine:

```kotlin
flowUnderTest.test {
    assertThat(awaitItem()).isEqualTo(expected)
    cancelAndIgnoreRemainingEvents()
}
```

For never-ending flows such as `SharedFlow`, do not use `awaitComplete()`. Cancel explicitly.

## ViewModel Tests

When testing ViewModels:

- Use `MainDispatcherRule`.
- Trigger behavior through `onEvent(...)`.
- Assert `uiState.value` for state updates.
- Collect `effect` with Turbine when checking one-shot behavior.
- Verify use case calls only when important to behavior.
- Test duplicate-submit guards when loading state exists.

Example shape:

```kotlin
@get:Rule
val mainDispatcherRule = MainDispatcherRule()

@Test
fun `submit success emits navigate effect`() = runTest {
    coEvery { useCase() } returns ApiResult.Success(Unit)

    viewModel.effect.test {
        viewModel.onEvent(ExampleEvent.Submit)
        assertThat(awaitItem()).isEqualTo(ExampleEffect.NavigateNext)
    }
}
```

## Assertions

- Use Truth: `assertThat(value).isEqualTo(expected)`.
- Prefer checking specific fields over comparing large objects when only one behavior matters.
- When asserting `ApiResult.Error`, cast after type assertion and inspect `message` or `exception`.

## Verification Commands

Run the narrowest relevant test first:

```bash
./gradlew :core:domain:testDebugUnitTest
./gradlew :core:data:testDebugUnitTest
./gradlew :feature:<name>:testDebugUnitTest
```

For broader confidence:

```bash
./gradlew testDebugUnitTest
```

If Gradle cannot run because of missing local secrets, Firebase config, or environment setup, report the blocker clearly.
