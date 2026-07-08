# Mapper Rules

This document defines the standard for mapping network/database models into domain models.

## Core Principle

Use nullable network DTOs defensively, but keep domain models clean.

```text
Network DTOs: nullable and tolerant of backend schema changes
Mappers: strict validation boundary
Repositories: convert invalid response data into ApiResult.Error
Domain models: non-null for required invariants
```

## Package

All data-layer mappers live in:

```text
core/data/src/main/java/com/doannd3/treetask/core/data/mapper/
```

Use `MapperExtensions.kt` only for small reusable primitive helpers, such as:

- `requiredStringOrNull()`
- `requiredInstantOrNull()`
- `requiredTaskStatusOrNull()`
- `requiredMessageTypeOrNull()`
- `mapOrNull { ... }`

Do not put business rules into shared mapper extensions.

## Naming

Use target-specific names.

```kotlin
fun UserResponse.toUserOrNull(): User?
fun TaskResponse.toTaskOrNull(): Task?
fun MessageResponse.toMessageOrNull(): Message?
fun ConversationResponse.toConversationOrNull(): Conversation?
```

Use non-null names only when the source already has trusted invariants.

```kotlin
fun TaskEntity.toTask(): Task
fun Task.toTaskEntity(): TaskEntity
```

Avoid vague names like `toDomain()` when the target model is not obvious.

## Required Fields

Required fields must fail the mapper when missing, blank, or invalid.

```kotlin
fun TaskResponse.toTaskOrNull(): Task? {
    val id = id.requiredStringOrNull() ?: return null
    val userId = userId.requiredStringOrNull() ?: return null
    val title = title.requiredStringOrNull() ?: return null
    val status = status.requiredTaskStatusOrNull() ?: return null
    val createdAt = createdAt.requiredInstantOrNull() ?: return null

    return Task(...)
}
```

Use guard clauses. Do not group nullable locals into one large `if` just to satisfy static analysis.

## Optional Fields

Optional fields may stay nullable or use a meaningful default.

```kotlin
description = description
avatar = avatar
phone = phone
name = name.orEmpty()
```

Never use `orEmpty()`, `Instant.now()`, fallback enum values, or fake IDs for required backend contract fields.

Backend enum-like values should be represented in `core:model`, such as `ConversationType` and `MessageType`, then mapped through strict helpers.

## Lists

If a list contains required domain items, one invalid item invalidates the whole response.

```kotlin
val tasks = taskResponses.mapOrNull { taskResponse ->
    taskResponse.toTaskOrNull()
} ?: return missingResponseDataError()
```

Do not use `filterNotNull()` for required response items because it silently drops corrupted data.

## Repository Handling

Repositories own conversion from invalid/missing response data into app errors.

```kotlin
val data = response.data ?: return missingResponseDataError()
val task = data.toTaskOrNull() ?: return missingResponseDataError()
```

Repository error helpers live in:

```text
core/data/src/main/java/com/doannd3/treetask/core/data/respository/RepositoryErrors.kt
```

Use `missingResponseDataError()` for invalid API response contracts in repositories that return `ApiResult`.

Use the same guard-clause shape for every required response invariant. Do not mix a separate `if (data == null)` block with Elvis guard clauses in repository mapping paths.

For repositories that return `ApiResult`, use `mapSuccessResult { ... }` to reuse only the repeated error pass-through branch.

Keep required-data validation, mapper validation, success messages, side effects, local persistence, cache updates, and database transactions explicit inside the success lambda. The helper must not hide repository policy.

Do not create user-facing copy in mapper or data-layer contract validation.

## Detekt

Mapper guard clauses are allowed to return early. `ReturnCount.max` is intentionally set high enough for mapper validation readability.

Static analysis should not force less readable mapper code.
