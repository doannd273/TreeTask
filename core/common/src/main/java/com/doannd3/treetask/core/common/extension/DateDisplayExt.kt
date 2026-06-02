package com.doannd3.treetask.core.common.extension

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Instant.toDayMonth(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM")
    return this
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(formatter)
}

fun Instant?.toMonthDay(): String {
    if (this == null) return ""

    return atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(
            DateTimeFormatter.ofPattern(
                "MMM dd",
                Locale.getDefault(),
            ),
        )
}
