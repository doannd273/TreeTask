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

class StartChatConversationRealtimeUseCaseTest {
    private val chatRealtimeRepository: ChatRealtimeRepository = mockk()
    private lateinit var startChatConversationRealtimeUseCase: StartChatConversationRealtimeUseCase

    @Before
    fun setUp() {
        startChatConversationRealtimeUseCase =
            StartChatConversationRealtimeUseCase(chatRealtimeRepository)
    }

    @Test
    fun `blank conversation id returns conversation id empty error`() =
        runTest {
            val result = startChatConversationRealtimeUseCase(conversationId = "  ")

            assertStringResourceError(result, R.string.common_error_conversation_id_empty)
            coVerify(exactly = 0) { chatRealtimeRepository.connect() }
            coVerify(exactly = 0) { chatRealtimeRepository.joinConversation(any()) }
        }

    @Test
    fun `valid input connects and joins conversation id unchanged`() =
        runTest {
            coEvery { chatRealtimeRepository.connect() } returns ApiResult.Success(data = Unit)
            coEvery {
                chatRealtimeRepository.joinConversation(conversationId = RAW_CONVERSATION_ID)
            } returns ApiResult.Success(data = Unit)

            val result =
                startChatConversationRealtimeUseCase(
                    conversationId = RAW_CONVERSATION_ID,
                )

            assertThat(result).isInstanceOf(ApiResult.Success::class.java)
            coVerify(exactly = 1) { chatRealtimeRepository.connect() }
            coVerify(exactly = 1) {
                chatRealtimeRepository.joinConversation(conversationId = RAW_CONVERSATION_ID)
            }
        }

    @Test
    fun `connect error returns error and does not join conversation`() =
        runTest {
            val connectError = ApiResult.Error(message = UiText.DynamicString("Unable to connect"))
            coEvery { chatRealtimeRepository.connect() } returns connectError

            val result = startChatConversationRealtimeUseCase(conversationId = CONVERSATION_ID)

            assertThat(result).isEqualTo(connectError)
            coVerify(exactly = 1) { chatRealtimeRepository.connect() }
            coVerify(exactly = 0) { chatRealtimeRepository.joinConversation(any()) }
        }

    @Test
    fun `join error disconnects socket and returns join error`() =
        runTest {
            val joinError = ApiResult.Error(message = UiText.DynamicString("Unable to join"))
            coEvery { chatRealtimeRepository.connect() } returns ApiResult.Success(data = Unit)
            coEvery {
                chatRealtimeRepository.joinConversation(conversationId = CONVERSATION_ID)
            } returns joinError
            coEvery { chatRealtimeRepository.disconnect() } returns ApiResult.Success(data = Unit)

            val result = startChatConversationRealtimeUseCase(conversationId = CONVERSATION_ID)

            assertThat(result).isEqualTo(joinError)
            coVerify(exactly = 1) { chatRealtimeRepository.connect() }
            coVerify(exactly = 1) {
                chatRealtimeRepository.joinConversation(conversationId = CONVERSATION_ID)
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
        const val RAW_CONVERSATION_ID = "  conversation-id  "
    }
}
