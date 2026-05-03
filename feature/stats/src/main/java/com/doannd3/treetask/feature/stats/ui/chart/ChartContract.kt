package com.doannd3.treetask.feature.stats.ui.chart

data class ChartState(
    val isLoading: Boolean = false,
)

sealed class ChartEvent

sealed class ChartEffect
