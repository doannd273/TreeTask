package com.doannd3.treetask.feature.tasks.ui.home

import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.model.task.TaskStatus

data class TasksState(
    val page: Int = 1,
    val isLoadingSearch: Boolean = false,
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val searchQuery: String = "",
    val taskStatusSelected: TaskStatus? = null,
)

sealed class TasksEvent {
    data class SearchChanged(val searchQuery: String) : TasksEvent()
    data object SearchQueryClear : TasksEvent()
    data class FilterSelected(val taskStatusSelected: TaskStatus?) : TasksEvent()
}

sealed class TasksEffect {
    data class ShowErrorMessage(val message: UiText) : TasksEffect()
}