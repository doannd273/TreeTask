package com.doannd3.treetask.feature.tasks.ui.home

import androidx.paging.PagingData
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.model.task.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class TasksState(
    val isLoadingSearch: Boolean = false,
    val isLoading: Boolean = false,
    val tasks: Flow<PagingData<Task>> = flowOf(PagingData.empty()),
    val searchQuery: String = "",
    val taskStatusSelected: TaskStatus? = null,
    val userId: String = "",
)

sealed class TasksEvent {
    data object Refresh : TasksEvent()

    data class SearchChanged(
        val searchQuery: String,
    ) : TasksEvent()

    data object SearchQueryClear : TasksEvent()

    data class FilterSelected(
        val taskStatusSelected: TaskStatus?,
    ) : TasksEvent()

    data class DeleteTask(val taskId: String) : TasksEvent()
}

sealed class TasksEffect {
    data class ShowErrorMessage(
        val message: UiText,
    ) : TasksEffect()
}
