package com.doannd3.treetask.core.domain.usecase.chat.realtime

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.repository.ChatRealtimeRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class StopChatConversationRealtimeUseCaseTest {
    private val chatRealtimeRepository: ChatRealtimeRepository = mockk()
    private lateinit var stopChatConversationRealtimeUseCase: StopChatConversationRealtimeUseCase

    @Before
    fun setUp() {
        stopChatConversationRealtimeUseCase =
            StopChatConversationRealtimeUseCase(chatRealtimeRepository)
    }

    @Test
    fun `blank conversation id returns conversation id empty error`() =
        runTest {
            val result = stopChatConversationRealtimeUseCase(conversationId = "  ")

            assertStringResourceError(result, R.string.common_error_conversation_id_empty)
            coVerify(exactly = 0) { chatRealtimeRepository.leaveConversation(any()) }
            coVerify(exactly = 0) { chatRealtimeRepository.disconnect() }
        }

    @Test
    fun `valid input leaves trimmed conversation and disconnects socket`() =
        runTest {
            coEvery {
                chatRealtimeRepository.leaveConversation(conversationId = CONVERSATION_ID)
            } returns ApiResult.Success(data = Unit)
            coEvery { chatRealtimeRepository.disconnect() } returns ApiResult.Success(data = Unit)

            val result =
                stopChatConversationRealtimeUseCase(
                    conversationId = "  $CONVERSATION_ID  ",
                )

            assertThat(result).isInstanceOf(ApiResult.Success::class.java)
            coVerify(exactly = 1) {
                chatRealtimeRepository.leaveConversation(conversationId = CONVERSATION_ID)
            }
            coVerify(exactly = 1) { chatRealtimeRepository.disconnect() }
        }

    @Test
    fun `leave error still disconnects socket and returns leave error`() =
        runTest {
            val leaveError = ApiResult.Error(message = UiText.DynamicString("Unable to leave"))
            coEvery {
                chatRealtimeRepository.leaveConversation(conversationId = CONVERSATION_ID)
            } returns leaveError
            coEvery { chatRealtimeRepository.disconnect() } returns ApiResult.Success(data = Unit)

            val result = stopChatConversationRealtimeUseCase(conversationId = CONVERSATION_ID)

            assertThat(result).isEqualTo(leaveError)
            coVerify(exactly = 1) {
                chatRealtimeRepository.leaveConversation(conversationId = CONVERSATION_ID)
            }
            coVerify(exactly = 1) { chatRealtimeRepository.disconnect() }
        }

    @Test
    fun `disconnect error is returned when leave succeeds`() =
        runTest {
            val disconnectError = ApiResult.Error(message = UiText.DynamicString("Unable to disconnect"))
            coEvery {
                chatRealtimeRepository.leaveConversation(conversationId = CONVERSATION_ID)
            } returns ApiResult.Success(data = Unit)
            coEvery { chatRealtimeRepository.disconnect() } returns disconnectError

            val result = stopChatConversationRealtimeUseCase(conversationId = CONVERSATION_ID)

            assertThat(result).isEqualTo(disconnectError)
            coVerify(exactly = 1) {
                chatRealtimeRepository.leaveConversation(conversationId = CONVERSATION_ID)
            }
            coVerify(exactly = 1) { chatRealtimeRepository.disconnect() }
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

    private companion object {
        const val CONVERSATION_ID = "conversation-id"
    }
}
