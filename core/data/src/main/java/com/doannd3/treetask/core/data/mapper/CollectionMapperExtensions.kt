package com.doannd3.treetask.core.data.mapper

internal inline fun <T, R> Iterable<T>.mapOrNull(transform: (T) -> R?): List<R>? {
    return map { item ->
        transform(item) ?: return null
    }
}
