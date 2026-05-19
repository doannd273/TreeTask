package com.doannd3.treetask.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme =
    lightColorScheme(
        primary = Purple40,
        onPrimary = Neutral100,
        primaryContainer = Purple90,
        onPrimaryContainer = Purple10,
        secondary = PurpleGrey40,
        onSecondary = Neutral100,
        secondaryContainer = PurpleGrey90,
        onSecondaryContainer = PurpleGrey30,
        tertiary = Pink40,
        onTertiary = Neutral100,
        tertiaryContainer = Pink90,
        onTertiaryContainer = Pink30,
        error = Error40,
        errorContainer = Error90,
        background = Neutral99,
        onBackground = Neutral10,
        surface = Neutral99,
        onSurface = Neutral10,
        onSurfaceVariant = NeutralVariant30,
        outline = NeutralVariant50,
        outlineVariant = NeutralVariant90,
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = Purple80,
        onPrimary = Purple20,
        primaryContainer = Purple30,
        onPrimaryContainer = Purple90,
        secondary = PurpleGrey80,
        onSecondary = PurpleGrey30,
        secondaryContainer = PurpleGrey30,
        onSecondaryContainer = PurpleGrey90,
        tertiary = Pink80,
        onTertiary = Pink30,
        tertiaryContainer = Pink30,
        onTertiaryContainer = Pink90,
        error = Error80,
        errorContainer = Error40,
        background = Neutral10,
        onBackground = Neutral90,
        surface = Neutral10,
        onSurface = Neutral90,
        onSurfaceVariant = NeutralVariant80,
        outline = NeutralVariant60,
        outlineVariant = NeutralVariant30,
    )

@Composable
fun TreeTaskTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme

            else -> LightColorScheme
        }

    val treeTaskColors = if (darkTheme) DarkTreeTaskColors else LightTreeTaskColors

    CompositionLocalProvider(LocalTreeTaskColors provides treeTaskColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
