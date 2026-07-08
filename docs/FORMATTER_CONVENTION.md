# Formatter Convention

This document records the Spotless + ktlint convention used in TreeTask and the reasoning behind it, so the same approach can be reused in other Kotlin/Android projects.

## Goal

Keep Kotlin formatting predictable in constructor-injected classes, especially ViewModels and UseCases, without letting formatter defaults produce overly deep indentation.

## Problem This Convention Solves

In Hilt-heavy codebases, a formatter can turn this:

```kotlin
@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val loginUseCase: LoginUseCase,
    ) : BaseViewModel(),
        MviViewModel<LoginState, LoginEvent, LoginEffect> {
        ...
    }
```

into a shape where the body becomes harder to read because `class`, `@Inject`, and `constructor` are split across separate levels.

TreeTask standardizes the header instead:

```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : BaseViewModel(), MviViewModel<LoginState, LoginEvent, LoginEffect> {
    ...
}
```

## TreeTask Baseline

- Spotless is the formatter entry point.
- ktlint is pinned explicitly instead of relying on the Spotless default.
- `.editorconfig` stays small and only contains broadly safe IDE-aligned settings.
- ktlint rule overrides live in one place: `build-logic/convention/src/main/kotlin/AndroidSpotlessConventionPlugin.kt`.
- `ktlint_standard_indent` stays enabled. This is important. Once class headers are normalized, indentation should still be recomputed by ktlint.

## Current TreeTask Rule Set

TreeTask currently uses:

- `ktlint_code_style = intellij_idea`
- trailing commas enabled in `.editorconfig`
- disabled ktlint rules:
  - `annotation`
  - `argument-list-wrapping`
  - `binary-expression-wrapping`
  - `blank-line-between-when-conditions`
  - `chain-method-continuation`
  - `class-signature`
  - `condition-wrapping`
  - `function-expression-body`
  - `function-literal`
  - `function-signature`
  - `max-line-length`
  - `multiline-loop`
  - `parameter-list-wrapping`
  - `property-wrapping`

The intent is not “disable everything”. The intent is to turn off the rules that most often fight readable constructor-heavy class headers while still letting ktlint handle indentation and baseline cleanup.

## Required Code Shape

When a class uses constructor injection, prefer this form:

```kotlin
class FooViewModel @Inject constructor(
    private val dependency: Dependency,
) : BaseViewModel(), MviViewModel<FooState, FooEvent, FooEffect> {
    ...
}
```

Do not intentionally write:

```kotlin
class FooViewModel
    @Inject
    constructor(...)
```

That older shape is the main trigger for deep indentation in this codebase.

## How To Reuse In Another Project

1. Pin a ktlint version explicitly.
2. Configure Spotless to call that version of ktlint.
3. Keep `.editorconfig` small and IDE-oriented.
4. Put ktlint rule overrides in build logic or one root Gradle file, not scattered per module.
5. Normalize injected class headers to `class Foo @Inject constructor(...)`.
6. Test the convention on a few representative files first:
   - one ViewModel
   - one UseCase
   - one multi-interface class
7. Only then apply it repo-wide.

## Minimal Portable Example

```kotlin
extensions.configure<SpotlessExtension> {
    kotlin {
        target("**/*.kt", "**/*.kts")
        ktlint("1.8.0").editorConfigOverride(
            mapOf(
                "ktlint_code_style" to "intellij_idea",
                "ktlint_standard_annotation" to "disabled",
                "ktlint_standard_class-signature" to "disabled",
                "ktlint_standard_function-signature" to "disabled",
                "ktlint_standard_parameter-list-wrapping" to "disabled",
                "ktlint_standard_max-line-length" to "disabled",
            ),
        )
    }
}
```

Use this as a starting point, not as a blind copy-paste. Different repos may need fewer or more overrides depending on whether they use Hilt, long generic signatures, MVI interfaces, or Compose-heavy function signatures.

## Trade-offs

- Pros:
  - more stable formatting for injected classes
  - better readability in ViewModel/UseCase-heavy modules
  - formatter behavior is centralized and versioned
- Cons:
  - deviates from strict ktlint defaults
  - needs periodic review when Spotless or ktlint versions change
  - still depends on code shape discipline; config alone is not enough

## Migration Advice

If another project already has many classes written in the old split form, do not expect rule changes alone to clean everything up. Do the migration in two steps:

1. update formatter configuration
2. rewrite constructor-injected headers to the normalized form

Without step 2, the repo can remain visually inconsistent even with the better rule set.
