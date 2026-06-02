package com.doannd3.treetask.feature.stats.ui

data class StatsState(
    val isLoading: Boolean = false,
)

sealed class StatsEvent

sealed class StatsEffect
