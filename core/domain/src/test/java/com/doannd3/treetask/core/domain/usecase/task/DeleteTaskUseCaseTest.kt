package com.doannd3.treetask.core.domain.usecase.task

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.repository.TaskRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeleteTaskUseCaseTest {
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase
    private val taskRepository: TaskRepository = mockk()

    @Before
    fun setUp() {
        deleteTaskUseCase = DeleteTaskUseCase(taskRepository)
    }

    @Test
    fun `blank task id returns task id empty error`() =
        runTest {
            val result = deleteTaskUseCase(taskId = "  ")

            assertStringResourceError(result, R.string.common_error_task_id_empty)
            coVerify(exactly = 0) { taskRepository.deleteTask(any()) }
        }

    @Test
    fun `valid task id trims id and calls repository`() =
        runTest {
            coEvery {
                taskRepository.deleteTask(taskId = "task-id")
            } returns ApiResult.Success(data = Unit)

            val result = deleteTaskUseCase(taskId = "  task-id  ")

            assertThat(result).isInstanceOf(ApiResult.Success::class.java)
            coVerify(exactly = 1) { taskRepository.deleteTask(taskId = "task-id") }
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
}
