package com.treestudio.treetask.ui.debug

private enum class AppEnvironment(
    val key: String,
    val badgeValue: String?,
) {
    Dev(
        key = "DEV",
        badgeValue = "DEV",
    ),
    Prod(
        key = "PROD",
        badgeValue = "PROD",
    ),
    Unknown(
        key = "UNKNOWN",
        badgeValue = null,
    ),
}

private fun parseEnvironment(raw: String): AppEnvironment =
    AppEnvironment.entries.firstOrNull { it.key == raw.uppercase() } ?: AppEnvironment.Unknown

fun buildDebugOverlayUiState(
    env: String,
    isDebug: Boolean,
): DebugOverlayUiState {
    val environment = parseEnvironment(env)
    val label =
        when (environment) {
            AppEnvironment.Dev -> if (isDebug) "${environment.badgeValue} • DEBUG" else environment.badgeValue
            AppEnvironment.Prod -> if (isDebug) "${environment.badgeValue} • DEBUG" else null
            AppEnvironment.Unknown -> null
        }

    return DebugOverlayUiState(
        label = label,
        show = label != null,
    )
}
