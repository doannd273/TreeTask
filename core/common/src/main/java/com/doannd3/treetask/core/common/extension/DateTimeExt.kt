package com.doannd3.treetask.core.common.extension

import timber.log.Timber
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun Instant.toDayMonth(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM")
    return this
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(formatter)
}

fun String?.toInstantOrNow(): Instant {
    return try {
        this?.let { Instant.parse(it) } ?: Instant.now()
    } catch (e: DateTimeParseException) {
        Timber.e(e, "Invalid instant format: $this")
        Instant.now()
    }
}

fun Long.toInstant(): Instant {
    return Instant.ofEpochMilli(this)
}

fun Instant.toLong() = toEpochMilli()
