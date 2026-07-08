package com.doannd3.treetask.core.domain.usecase.task

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.repository.TaskRepository
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.model.task.TaskStatus
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant

class GetTaskByIdUseCaseTest {
    private lateinit var getTaskByIdUseCase: GetTaskByIdUseCase
    private val taskRepository: TaskRepository = mockk()

    @Before
    fun setUp() {
        getTaskByIdUseCase = GetTaskByIdUseCase(taskRepository)
    }

    @Test
    fun `blank task id returns task id empty error`() =
        runTest {
            val result = getTaskByIdUseCase(taskId = "  ")

            assertStringResourceError(result, R.string.common_error_task_id_empty)
            coVerify(exactly = 0) { taskRepository.getTaskById(any()) }
        }

    @Test
    fun `valid task id trims id and calls repository`() =
        runTest {
            val task = createTask()
            coEvery {
                taskRepository.getTaskById(taskId = "task-id")
            } returns ApiResult.Success(data = task)

            val result = getTaskByIdUseCase(taskId = "  task-id  ")

            assertThat(result).isInstanceOf(ApiResult.Success::class.java)
            coVerify(exactly = 1) { taskRepository.getTaskById(taskId = "task-id") }
        }

    private fun assertStringResourceError(
        result: ApiResult<*>,
        expectedResId: Int,
    ) {
        assertThat(result).isInstanceOf(ApiResult.Error::class.java)
        val errorResult = result as ApiResult.Error
        assertThat(errorResult.message).isInstanceOf(UiText.StringResource::class.java)
        val stringResource = errorResult.message as UiText.StringResource
        assertThat(stringResource.resId).isEqualTo(expectedResId)
    }

    private fun createTask(): Task =
        Task(
            id = "task-id",
            userId = "user-id",
            title = "Title",
            description = "Description",
            status = TaskStatus.TODO,
            dueDate = Instant.parse("2026-05-31T00:00:00Z"),
            createdAt = Instant.parse("2026-05-01T00:00:00Z"),
            updatedAt = Instant.parse("2026-05-01T00:00:00Z"),
        )
}
