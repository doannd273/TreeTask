package com.doannd3.treetask.core.data.mapper

import com.doannd3.treetask.core.common.extension.toInstantOrNull
import com.doannd3.treetask.core.model.chat.MessageType
import com.doannd3.treetask.core.model.task.TaskStatus
import java.time.Instant

internal fun String?.requiredStringOrNull(): String? = this?.trim()?.takeIf { it.isNotEmpty() }

internal fun String?.optionalString(): String = this.orEmpty()

internal fun String?.requiredInstantOrNull(): Instant? = requiredStringOrNull()?.toInstantOrNull()

internal fun String?.requiredTaskStatusOrNull(): TaskStatus? {
    return TaskStatus.entries.firstOrNull { taskStatus ->
        taskStatus.apiValue == this
    }
}

internal fun String?.requiredMessageTypeOrNull(): MessageType? {
    return MessageType.entries.firstOrNull { messageType ->
        messageType.type == this
    }
}

internal inline fun <T, R> Iterable<T>.mapOrNull(transform: (T) -> R?): List<R>? {
    return map { item ->
        transform(item) ?: return null
    }
}
