package com.doannd3.treetask.core.domain.usecase.task

import androidx.paging.PagingData
import com.doannd3.treetask.core.domain.repository.TaskRepository
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.model.task.TaskStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test

class GetTasksUseCaseTest {
    private lateinit var getTasksUseCase: GetTasksUseCase
    private val taskRepository: TaskRepository = mockk()

    @Before
    fun setUp() {
        getTasksUseCase = GetTasksUseCase(taskRepository)
    }

    @Test
    fun `blank user id does not call repository`() {
        getTasksUseCase(
            status = "",
            keyword = "",
            userId = "  ",
        )

        verify(exactly = 0) { taskRepository.getTasks(any(), any(), any()) }
    }

    @Test
    fun `invalid status does not call repository`() {
        getTasksUseCase(
            status = "blocked",
            keyword = "",
            userId = "user-id",
        )

        verify(exactly = 0) { taskRepository.getTasks(any(), any(), any()) }
    }

    @Test
    fun `valid input trims filters and calls repository`() {
        every {
            taskRepository.getTasks(
                status = TaskStatus.TODO.apiValue,
                keyword = "keyword",
                userId = "user-id",
            )
        } returns flowOf(PagingData.empty<Task>())

        getTasksUseCase(
            status = "  ${TaskStatus.TODO.apiValue}  ",
            keyword = "  keyword  ",
            userId = "  user-id  ",
        )

        verify(exactly = 1) {
            taskRepository.getTasks(
                status = TaskStatus.TODO.apiValue,
                keyword = "keyword",
                userId = "user-id",
            )
        }
    }
}
