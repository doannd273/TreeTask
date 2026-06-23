package com.doannd3.treetask.core.domain.usecase.chat

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.repository.ChatRepository
import com.doannd3.treetask.core.model.chat.Conversation
import com.doannd3.treetask.core.model.chat.ConversationType
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CreatePrivateConversationUseCaseTest {
    private lateinit var createPrivateConversationUseCase: CreatePrivateConversationUseCase
    private val chatRepository: ChatRepository = mockk()

    @Before
    fun setUp() {
        createPrivateConversationUseCase = CreatePrivateConversationUseCase(chatRepository)
    }

    @Test
    fun `blank other user id returns other user id empty error`() =
        runTest {
            val result = createPrivateConversationUseCase(otherUserId = "  ")

            assertStringResourceError(result, R.string.common_error_other_user_id_empty)
            coVerify(exactly = 0) { chatRepository.createPrivateConversation(any()) }
        }

    @Test
    fun `valid input trims other user id before calling repository`() =
        runTest {
            val conversation = createConversation()
            coEvery {
                chatRepository.createPrivateConversation(otherUserId = "user-id")
            } returns ApiResult.Success(data = conversation)

            val result = createPrivateConversationUseCase(otherUserId = "  user-id  ")

            assertThat(result).isInstanceOf(ApiResult.Success::class.java)
            coVerify(exactly = 1) {
                chatRepository.createPrivateConversation(otherUserId = "user-id")
            }
        }

    private fun assertStringResourceError(
        result: ApiResult<Conversation>,
        expectedResId: Int,
    ) {
        assertThat(result).isInstanceOf(ApiResult.Error::class.java)
        val errorResult = result as ApiResult.Error
        assertThat(errorResult.message).isInstanceOf(UiText.StringResource::class.java)
        val stringResource = errorResult.message as UiText.StringResource
        assertThat(stringResource.resId).isEqualTo(expectedResId)
    }

    private fun createConversation(): Conversation =
        Conversation(
            id = "conversation-id",
            type = ConversationType.PRIVATE,
            name = "Direct chat",
            creatorId = "creator-id",
            participants = emptyList(),
            lastMessage = null,
            lastMessageAt = null,
        )
}
