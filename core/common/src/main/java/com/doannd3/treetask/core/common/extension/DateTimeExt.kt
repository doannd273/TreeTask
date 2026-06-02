package com.doannd3.treetask.core.common.extension

import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun String?.toInstantOrNow(): Instant =
    try {
        this?.let { Instant.parse(it) } ?: Instant.now()
    } catch (e: DateTimeParseException) {
        Timber.e(e, "Invalid instant format: $this")
        Instant.now()
    }

fun String?.toInstantOrNull(): Instant? =
    try {
        this?.let { Instant.parse(it) }
    } catch (e: DateTimeParseException) {
        Timber.e(e, "Invalid instant format: $this")
        null
    }

fun Long.toInstant(): Instant = Instant.ofEpochMilli(this)

fun Instant.toLong() = toEpochMilli()

fun Instant?.toYmd(): String {
    if (this == null) return ""
    return this.atOffset(ZoneOffset.UTC)
        .toLocalDate()
        .toString() // "yyyy-MM-dd"
}

fun Long?.toYmdDate(): String {
    if (this == null) return ""
    return Instant.ofEpochMilli(this).atOffset(ZoneOffset.UTC).toLocalDate().toString()
}

fun Long?.toDmyDate(): String {
    if (this == null) return ""
    return Instant.ofEpochMilli(this)
        .atOffset(ZoneOffset.UTC)
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}

fun String?.ymdToEpochMillis(): Long? {
    if (this.isNullOrBlank()) return null

    return runCatching {
        LocalDate
            .parse(this)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    }.getOrNull()
}

fun String?.ymdToDmy(): String {
    if (this.isNullOrBlank()) return ""

    return runCatching {
        LocalDate
            .parse(this)
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }.getOrDefault("")
}
