package com.doannd3.treetask.core.domain.usecase.chat

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.repository.ChatRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetMessagesUseCaseTest {
    private lateinit var getMessagesUseCase: GetMessagesUseCase
    private val chatRepository: ChatRepository = mockk()

    @Before
    fun setUp() {
        getMessagesUseCase = GetMessagesUseCase(chatRepository)
    }

    @Test
    fun `blank conversation id returns conversation id empty error`() =
        runTest {
            val result =
                getMessagesUseCase(
                    conversationId = "  ",
                    page = 1,
                    limit = 20,
                )

            assertStringResourceError(result, R.string.common_error_conversation_id_empty)
            coVerify(exactly = 0) { chatRepository.getMessages(any(), any(), any()) }
        }

    @Test
    fun `invalid page returns page invalid error`() =
        runTest {
            val result =
                getMessagesUseCase(
                    conversationId = "conversation-id",
                    page = 0,
                    limit = 20,
                )

            assertStringResourceError(result, R.string.common_error_page_invalid)
            coVerify(exactly = 0) { chatRepository.getMessages(any(), any(), any()) }
        }

    @Test
    fun `invalid limit returns limit invalid error`() =
        runTest {
            val result =
                getMessagesUseCase(
                    conversationId = "conversation-id",
                    page = 1,
                    limit = 101,
                )

            assertStringResourceError(result, R.string.common_error_limit_invalid)
            coVerify(exactly = 0) { chatRepository.getMessages(any(), any(), any()) }
        }

    @Test
    fun `valid input trims conversation id and calls repository`() =
        runTest {
            coEvery {
                chatRepository.getMessages(
                    conversationId = "conversation-id",
                    page = 1,
                    limit = 20,
                )
            } returns ApiResult.Success(data = emptyList())

            val result =
                getMessagesUseCase(
                    conversationId = "  conversation-id  ",
                    page = 1,
                    limit = 20,
                )

            assertThat(result).isInstanceOf(ApiResult.Success::class.java)
            coVerify(exactly = 1) {
                chatRepository.getMessages(
                    conversationId = "conversation-id",
                    page = 1,
                    limit = 20,
                )
            }
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
