package com.doannd3.treetask.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class TreeTaskColors(
    val success: Color,
    val onSuccess: Color,
)

internal val LightTreeTaskColors = TreeTaskColors(
    success = SuccessGreen40,
    onSuccess = Neutral100,
)

internal val DarkTreeTaskColors = TreeTaskColors(
    success = SuccessGreen80,
    onSuccess = Neutral10,
)

internal val LocalTreeTaskColors = staticCompositionLocalOf { LightTreeTaskColors }

val MaterialTheme.treeTaskColors: TreeTaskColors
    @Composable
    @ReadOnlyComposable
    get() = LocalTreeTaskColors.current
