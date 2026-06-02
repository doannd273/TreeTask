package com.doannd3.treetask.feature.stats.ui

import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.model.stats.TaskStats

data class StatsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val taskStats: TaskStats? = null,
    val hasInitialLoadError: Boolean = false,
) {
    val isEmpty: Boolean get() = taskStats?.total == 0
}

sealed class StatsEvent {
    data object Refresh : StatsEvent()
}

sealed class StatsEffect {
    data class ShowErrorMessage(
        val message: UiText,
    ) : StatsEffect()
}
