package com.doannd3.treetask.feature.chat.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.doannd3.treetask.feature.chat.R
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

internal sealed class ChatTimeDisplay {
    data class TimeToday(val hour: Int, val minute: Int) : ChatTimeDisplay()

    data object Yesterday : ChatTimeDisplay()

    data class ThisWeek(val dayOfWeek: DayOfWeek) : ChatTimeDisplay()

    data class FullDate(val day: Int, val month: Int, val year: Int) : ChatTimeDisplay()
}

internal fun Instant?.toChatTimeLabel(now: LocalDate = LocalDate.now(ZoneId.systemDefault())): ChatTimeDisplay? {
    if (this == null) return null
    val zoned = atZone(ZoneId.systemDefault())
    val date = zoned.toLocalDate()
    return when {
        date == now -> ChatTimeDisplay.TimeToday(zoned.hour, zoned.minute)
        date == now.minusDays(1) -> ChatTimeDisplay.Yesterday
        date.isAfter(now.minusDays(7)) -> ChatTimeDisplay.ThisWeek(date.dayOfWeek)
        else -> ChatTimeDisplay.FullDate(date.dayOfMonth, date.monthValue, date.year)
    }
}

@Composable
internal fun ChatTimeDisplay?.toDisplayString(): String =
    when (this) {
        null -> ""
        is ChatTimeDisplay.TimeToday -> "%02d:%02d".format(hour, minute)
        is ChatTimeDisplay.Yesterday -> stringResource(R.string.chat_time_yesterday)
        is ChatTimeDisplay.ThisWeek -> stringResource(dayOfWeek.toChatDayResId())
        is ChatTimeDisplay.FullDate -> "%02d/%02d/%04d".format(day, month, year)
    }

private fun DayOfWeek.toChatDayResId(): Int =
    when (this) {
        DayOfWeek.MONDAY -> R.string.chat_time_monday
        DayOfWeek.TUESDAY -> R.string.chat_time_tuesday
        DayOfWeek.WEDNESDAY -> R.string.chat_time_wednesday
        DayOfWeek.THURSDAY -> R.string.chat_time_thursday
        DayOfWeek.FRIDAY -> R.string.chat_time_friday
        DayOfWeek.SATURDAY -> R.string.chat_time_saturday
        DayOfWeek.SUNDAY -> R.string.chat_time_sunday
    }
