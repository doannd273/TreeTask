package com.doannd3.treetask.feature.tasks.ui.home

import androidx.paging.PagingData
import com.doannd3.treetask.core.domain.usecase.task.DeleteTaskUseCase
import com.doannd3.treetask.core.domain.usecase.task.GetTasksUseCase
import com.doannd3.treetask.core.domain.usecase.user.ObserveCurrentUserIdUseCase
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.model.task.TaskStatus
import com.doannd3.treetask.core.testing.util.MainDispatcherRule
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TasksViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getTasksUseCase: GetTasksUseCase = mockk()
    private val deleteTaskUseCase: DeleteTaskUseCase = mockk()
    private val observeCurrentUserIdUseCase: ObserveCurrentUserIdUseCase = mockk()

    private lateinit var viewModel: TasksViewModel

    @Before
    fun setUp() {
        every { observeCurrentUserIdUseCase() } returns flowOf(USER_ID)
        every {
            getTasksUseCase(
                status = any(),
                keyword = any(),
                userId = any(),
            )
        } returns flowOf(PagingData.empty<Task>())

        viewModel =
            TasksViewModel(
                getTasksUseCase = getTasksUseCase,
                deleteTaskUseCase = deleteTaskUseCase,
                observeCurrentUserIdUseCase = observeCurrentUserIdUseCase,
            )
    }

    @Test
    fun `search waits for debounce and only loads latest query`() =
        runTest {
            collectTasksPagingFlow()
            runCurrent()
            clearMocks(getTasksUseCase, answers = false, recordedCalls = true)

            viewModel.onEvent(TasksEvent.SearchChanged(searchQuery = "t"))
            advanceTimeBy(200L)
            viewModel.onEvent(TasksEvent.SearchChanged(searchQuery = "task"))
            advanceTimeBy(TasksViewModel.SEARCH_DEBOUNCE_MILLIS - 1L)
            runCurrent()

            verify(exactly = 0) {
                getTasksUseCase(
                    status = any(),
                    keyword = any(),
                    userId = any(),
                )
            }

            advanceTimeBy(1L)
            runCurrent()

            verify(exactly = 1) {
                getTasksUseCase(
                    status = "",
                    keyword = "task",
                    userId = USER_ID,
                )
            }
        }

    @Test
    fun `equivalent normalized query does not reload tasks`() =
        runTest {
            collectTasksPagingFlow()
            runCurrent()
            clearMocks(getTasksUseCase, answers = false, recordedCalls = true)

            viewModel.onEvent(TasksEvent.SearchChanged(searchQuery = "  task  "))
            advanceTimeBy(TasksViewModel.SEARCH_DEBOUNCE_MILLIS)
            runCurrent()

            verify(exactly = 1) {
                getTasksUseCase(
                    status = "",
                    keyword = "task",
                    userId = USER_ID,
                )
            }

            clearMocks(getTasksUseCase, answers = false, recordedCalls = true)
            viewModel.onEvent(TasksEvent.SearchChanged(searchQuery = "task"))
            advanceTimeBy(TasksViewModel.SEARCH_DEBOUNCE_MILLIS)
            runCurrent()

            verify(exactly = 0) {
                getTasksUseCase(
                    status = any(),
                    keyword = any(),
                    userId = any(),
                )
            }
        }

    @Test
    fun `filter selection reloads tasks without search debounce delay`() =
        runTest {
            collectTasksPagingFlow()
            runCurrent()
            clearMocks(getTasksUseCase, answers = false, recordedCalls = true)

            viewModel.onEvent(
                TasksEvent.FilterSelected(taskStatusSelected = TaskStatus.DONE),
            )
            runCurrent()

            verify(exactly = 1) {
                getTasksUseCase(
                    status = TaskStatus.DONE.apiValue,
                    keyword = "",
                    userId = USER_ID,
                )
            }
        }

    private fun TestScope.collectTasksPagingFlow() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.value.tasks.collect()
        }
    }

    private companion object {
        const val USER_ID = "user-1"
    }
}
