package com.doannd3.treetask.core.designsystem.util

import android.os.SystemClock
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

@Composable
fun rememberDebouncedClick(
    intervalMillis: Long = 600L,
    onClick: () -> Unit,
): () -> Unit {
    var lastClickTime by remember { mutableLongStateOf(0) }
    val currentOnClick by rememberUpdatedState(onClick)

    return {
        val now = SystemClock.elapsedRealtime()
        if (now - lastClickTime >= intervalMillis) {
            lastClickTime = now
            currentOnClick()
        }
    }
}

fun Modifier.debouncedClickable(
    enabled: Boolean = true,
    intervalMillis: Long = 600L,
    onClick: () -> Unit,
): Modifier = composed {
    val debouncedClick = rememberDebouncedClick(intervalMillis, onClick)
    clickable(enabled = enabled, onClick = debouncedClick)
}
