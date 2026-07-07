package com.doannd3.treetask.core.domain.usecase.chat

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.repository.ChatRepository
import com.doannd3.treetask.core.model.chat.Message
import com.doannd3.treetask.core.model.chat.MessageType
import com.doannd3.treetask.core.model.user.User
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant

class SendMessageUseCaseTest {
    private lateinit var sendMessageUseCase: SendMessageUseCase
    private val chatRepository: ChatRepository = mockk()

    @Before
    fun setUp() {
        sendMessageUseCase = SendMessageUseCase(chatRepository)
    }

    @Test
    fun `blank conversation id returns conversation id empty error`() =
        runTest {
            val result =
                sendMessageUseCase(
                    conversationId = "  ",
                    content = "Hello",
                )

            assertStringResourceError(result, R.string.common_error_conversation_id_empty)
            coVerify(exactly = 0) { chatRepository.sendMessage(any(), any()) }
        }

    @Test
    fun `blank content returns message content empty error`() =
        runTest {
            val result =
                sendMessageUseCase(
                    conversationId = "conversation-id",
                    content = "  ",
                )

            assertStringResourceError(result, R.string.common_error_message_content_empty)
            coVerify(exactly = 0) { chatRepository.sendMessage(any(), any()) }
        }

    @Test
    fun `valid input keeps conversation id trims content and calls repository`() =
        runTest {
            val message = createMessage()
            coEvery {
                chatRepository.sendMessage(
                    conversationId = RAW_CONVERSATION_ID,
                    content = "Hello",
                )
            } returns ApiResult.Success(data = message)

            val result =
                sendMessageUseCase(
                    conversationId = RAW_CONVERSATION_ID,
                    content = "  Hello  ",
                )

            assertThat(result).isInstanceOf(ApiResult.Success::class.java)
            coVerify(exactly = 1) {
                chatRepository.sendMessage(
                    conversationId = RAW_CONVERSATION_ID,
                    content = "Hello",
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

    private fun createMessage(): Message =
        Message(
            id = "message-id",
            conversationId = "conversation-id",
            user =
            User(
                id = "user-id",
                email = "user@treetask.com",
                fullName = "Doan ND",
                avatar = null,
                phone = null,
            ),
            type = MessageType.TEXT,
            content = "Hello",
            createdAt = Instant.parse("2026-05-31T00:00:00Z"),
        )

    private companion object {
        const val RAW_CONVERSATION_ID = "  conversation-id  "
    }
}
