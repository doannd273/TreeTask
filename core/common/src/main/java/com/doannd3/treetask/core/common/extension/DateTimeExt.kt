package com.doannd3.treetask.core.common.extension

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
    } catch (e: Exception) {
        Instant.now()
    }
}
