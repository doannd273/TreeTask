package com.doannd3.treetask.feature.chat.ui.detail

import app.cash.turbine.test
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.usecase.chat.GetMessagesUseCase
import com.doannd3.treetask.core.domain.usecase.chat.SendMessageUseCase
import com.doannd3.treetask.core.domain.usecase.chat.realtime.ObserveChatRealtimeEventsUseCase
import com.doannd3.treetask.core.domain.usecase.chat.realtime.StartChatConversationRealtimeUseCase
import com.doannd3.treetask.core.domain.usecase.chat.realtime.StopChatConversationRealtimeUseCase
import com.doannd3.treetask.core.domain.usecase.user.ObserveCurrentUserIdUseCase
import com.doannd3.treetask.core.model.chat.ChatRealtimeEvent
import com.doannd3.treetask.core.model.chat.Message
import com.doannd3.treetask.core.model.chat.MessageType
import com.doannd3.treetask.core.model.user.User
import com.doannd3.treetask.core.testing.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class ChatDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeCurrentUserIdUseCase: ObserveCurrentUserIdUseCase = mockk()
    private val getMessagesUseCase: GetMessagesUseCase = mockk()
    private val sendMessageUseCase: SendMessageUseCase = mockk()
    private val startChatConversationRealtimeUseCase: StartChatConversationRealtimeUseCase = mockk()
    private val stopChatConversationRealtimeUseCase: StopChatConversationRealtimeUseCase = mockk()
    private val observeChatRealtimeEventsUseCase: ObserveChatRealtimeEventsUseCase = mockk()
    private val realtimeEvents = MutableSharedFlow<ChatRealtimeEvent>(extraBufferCapacity = 16)

    private lateinit var viewModel: ChatDetailViewModel

    @Before
    fun setUp() {
        every { observeCurrentUserIdUseCase() } returns flowOf(CURRENT_USER_ID)
        every { observeChatRealtimeEventsUseCase() } returns realtimeEvents
        viewModel =
            ChatDetailViewModel(
                observerUserIdUseCase = observeCurrentUserIdUseCase,
                getMessagesUseCase = getMessagesUseCase,
                sendMessageUseCase = sendMessageUseCase,
                startChatConversationRealtimeUseCase = startChatConversationRealtimeUseCase,
                stopChatConversationRealtimeUseCase = stopChatConversationRealtimeUseCase,
                observerChatRealtimeEventsUseCase = observeChatRealtimeEventsUseCase,
            )
    }

    @Test
    fun `new realtime message for current conversation upserts timeline`() =
        runTest {
            val existingMessage =
                createMessage(
                    id = "message-1",
                    content = "Old",
                    createdAt = Instant.parse("2026-06-24T09:00:00Z"),
                )
            val realtimeMessage =
                createMessage(
                    id = "message-2",
                    content = "Realtime",
                    createdAt = Instant.parse("2026-06-24T10:00:00Z"),
                )

            coEvery {
                getMessagesUseCase(CONVERSATION_ID, ChatDetailViewModel.DEFAULT_PAGE, ChatDetailViewModel.DEFAULT_LIMIT)
            } returns ApiResult.Success(data = listOf(existingMessage))

            viewModel.onEvent(ChatDetailEvent.LoadMessages(CONVERSATION_ID))
            advanceUntilIdle()

            realtimeEvents.emit(ChatRealtimeEvent.NewMessage(realtimeMessage))
            advanceUntilIdle()

            assertThat(viewModel.uiState.value.messages)
                .containsExactly(existingMessage, realtimeMessage)
                .inOrder()
        }

    @Test
    fun `new realtime message for other conversation is ignored`() =
        runTest {
            val currentMessage = createMessage("message-1", "Current", Instant.parse("2026-06-24T09:00:00Z"))
            val otherMessage =
                createMessage("message-2", "Other", Instant.parse("2026-06-24T10:00:00Z"))
                    .copy(conversationId = "other-conversation")

            coEvery {
                getMessagesUseCase(CONVERSATION_ID, ChatDetailViewModel.DEFAULT_PAGE, ChatDetailViewModel.DEFAULT_LIMIT)
            } returns ApiResult.Success(data = listOf(currentMessage))

            viewModel.onEvent(ChatDetailEvent.LoadMessages(CONVERSATION_ID))
            advanceUntilIdle()

            realtimeEvents.emit(ChatRealtimeEvent.NewMessage(otherMessage))
            advanceUntilIdle()

            assertThat(viewModel.uiState.value.messages).containsExactly(currentMessage)
        }

    @Test
    fun `duplicate realtime message replaces existing message without duplicate`() =
        runTest {
            val oldMessage = createMessage("message-1", "Old", Instant.parse("2026-06-24T09:00:00Z"))
            val updatedMessage = oldMessage.copy(content = "Updated")

            coEvery {
                getMessagesUseCase(CONVERSATION_ID, ChatDetailViewModel.DEFAULT_PAGE, ChatDetailViewModel.DEFAULT_LIMIT)
            } returns ApiResult.Success(data = listOf(oldMessage))

            viewModel.onEvent(ChatDetailEvent.LoadMessages(CONVERSATION_ID))
            advanceUntilIdle()

            realtimeEvents.emit(ChatRealtimeEvent.NewMessage(updatedMessage))
            advanceUntilIdle()

            assertThat(viewModel.uiState.value.messages).containsExactly(updatedMessage)
            assertThat(viewModel.uiState.value.messages.map { it.id }).containsNoDuplicates()
        }

    @Test
    fun `typing started for current conversation from other user shows indicator`() =
        runTest {
            stubLoadMessages()
            viewModel.onEvent(ChatDetailEvent.LoadMessages(conversationId = CONVERSATION_ID))
            advanceUntilIdle()

            realtimeEvents.emit(
                ChatRealtimeEvent.TypingStarted(
                    conversationId = CONVERSATION_ID,
                    userId = OTHER_USER_ID,
                ),
            )
            advanceUntilIdle()

            assertThat(viewModel.uiState.value.typingUserId).isEqualTo(OTHER_USER_ID)
        }

    @Test
    fun `typing started from current user is ignored`() =
        runTest {
            stubLoadMessages()
            viewModel.onEvent(ChatDetailEvent.LoadMessages(conversationId = CONVERSATION_ID))
            advanceUntilIdle()

            realtimeEvents.emit(
                ChatRealtimeEvent.TypingStarted(
                    conversationId = CONVERSATION_ID,
                    userId = CURRENT_USER_ID,
                ),
            )
            advanceUntilIdle()

            assertThat(viewModel.uiState.value.typingUserId).isNull()
        }

    @Test
    fun `typing started from other conversation is ignored`() =
        runTest {
            stubLoadMessages()
            viewModel.onEvent(ChatDetailEvent.LoadMessages(conversationId = CONVERSATION_ID))
            advanceUntilIdle()

            realtimeEvents.emit(
                ChatRealtimeEvent.TypingStarted(
                    conversationId = OTHER_CONVERSATION_ID,
                    userId = OTHER_USER_ID,
                ),
            )
            advanceUntilIdle()

            assertThat(viewModel.uiState.value.typingUserId).isNull()
        }

    @Test
    fun `typing stopped clears matching typing user`() =
        runTest {
            stubLoadMessages()
            viewModel.onEvent(ChatDetailEvent.LoadMessages(conversationId = CONVERSATION_ID))
            advanceUntilIdle()

            realtimeEvents.emit(
                ChatRealtimeEvent.TypingStarted(
                    conversationId = CONVERSATION_ID,
                    userId = OTHER_USER_ID,
                ),
            )
            advanceUntilIdle()
            realtimeEvents.emit(
                ChatRealtimeEvent.TypingStopped(
                    conversationId = CONVERSATION_ID,
                    userId = OTHER_USER_ID,
                ),
            )
            advanceUntilIdle()

            assertThat(viewModel.uiState.value.typingUserId).isNull()
        }

    @Test
    fun `stop realtime clears typing state`() =
        runTest {
            stubLoadMessages()
            coEvery {
                stopChatConversationRealtimeUseCase(conversationId = CONVERSATION_ID)
            } returns ApiResult.Success(data = Unit)

            viewModel.onEvent(ChatDetailEvent.LoadMessages(conversationId = CONVERSATION_ID))
            advanceUntilIdle()
            realtimeEvents.emit(
                ChatRealtimeEvent.TypingStarted(
                    conversationId = CONVERSATION_ID,
                    userId = OTHER_USER_ID,
                ),
            )
            advanceUntilIdle()

            viewModel.onEvent(ChatDetailEvent.StopRealtime(conversationId = CONVERSATION_ID))
            advanceUntilIdle()

            assertThat(viewModel.uiState.value.typingUserId).isNull()
        }

    @Test
    fun `load messages success updates state in display order`() =
        runTest {
            val olderMessage =
                createMessage(
                    id = "message-older",
                    content = "Older message",
                    createdAt = Instant.parse("2026-06-24T09:00:00Z"),
                )
            val newerMessage =
                createMessage(
                    id = "message-newer",
                    content = "Newer message",
                    createdAt = Instant.parse("2026-06-24T10:00:00Z"),
                )

            coEvery {
                getMessagesUseCase(
                    conversationId = CONVERSATION_ID,
                    page = ChatDetailViewModel.DEFAULT_PAGE,
                    limit = ChatDetailViewModel.DEFAULT_LIMIT,
                )
            } returns ApiResult.Success(data = listOf(newerMessage, olderMessage))

            viewModel.onEvent(ChatDetailEvent.LoadMessages(conversationId = CONVERSATION_ID))
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertThat(state.conversationId).isEqualTo(CONVERSATION_ID)
            assertThat(state.messages).containsExactly(olderMessage, newerMessage).inOrder()
            assertThat(state.hasInitialLoadError).isFalse()
            assertThat(state.isLoading).isFalse()
        }

    @Test
    fun `load messages error emits error effect and sets initial error when list is empty`() =
        runTest {
            val errorMessage = UiText.DynamicString("Unable to load messages")

            coEvery {
                getMessagesUseCase(
                    conversationId = CONVERSATION_ID,
                    page = ChatDetailViewModel.DEFAULT_PAGE,
                    limit = ChatDetailViewModel.DEFAULT_LIMIT,
                )
            } returns ApiResult.Error(message = errorMessage)

            viewModel.effect.test {
                viewModel.onEvent(ChatDetailEvent.LoadMessages(conversationId = CONVERSATION_ID))
                advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(ChatDetailEffect.ShowErrorMessage(errorMessage))
                val state = viewModel.uiState.value
                assertThat(state.hasInitialLoadError).isTrue()
                assertThat(state.messages).isEmpty()
                assertThat(state.isLoading).isFalse()

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `start realtime success keeps conversation id unchanged`() =
        runTest {
            coEvery {
                startChatConversationRealtimeUseCase(conversationId = RAW_CONVERSATION_ID)
            } returns ApiResult.Success(data = Unit)

            viewModel.onEvent(
                ChatDetailEvent.StartRealtime(conversationId = RAW_CONVERSATION_ID),
            )
            advanceUntilIdle()

            coVerify(exactly = 1) {
                startChatConversationRealtimeUseCase(conversationId = RAW_CONVERSATION_ID)
            }
        }

    @Test
    fun `start realtime error emits error effect`() =
        runTest {
            val errorMessage = UiText.DynamicString("Unable to connect realtime")
            coEvery {
                startChatConversationRealtimeUseCase(conversationId = CONVERSATION_ID)
            } returns ApiResult.Error(message = errorMessage)

            viewModel.effect.test {
                viewModel.onEvent(ChatDetailEvent.StartRealtime(conversationId = CONVERSATION_ID))
                advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(ChatDetailEffect.ShowErrorMessage(errorMessage))
                coVerify(exactly = 1) {
                    startChatConversationRealtimeUseCase(conversationId = CONVERSATION_ID)
                }

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `stop realtime leaves conversation and disconnects socket`() =
        runTest {
            coEvery {
                startChatConversationRealtimeUseCase(conversationId = CONVERSATION_ID)
            } returns ApiResult.Success(data = Unit)
            coEvery {
                stopChatConversationRealtimeUseCase(conversationId = CONVERSATION_ID)
            } returns ApiResult.Success(data = Unit)

            viewModel.onEvent(ChatDetailEvent.StartRealtime(conversationId = CONVERSATION_ID))
            advanceUntilIdle()
            viewModel.onEvent(ChatDetailEvent.StopRealtime(conversationId = CONVERSATION_ID))
            advanceUntilIdle()

            coVerify(exactly = 1) {
                stopChatConversationRealtimeUseCase(conversationId = CONVERSATION_ID)
            }
        }

    @Test
    fun `send blank message does not call send message use case`() =
        runTest {
            viewModel.onEvent(ChatDetailEvent.MessageChanged(message = "   "))

            viewModel.onEvent(ChatDetailEvent.SendMessageClicked)
            advanceUntilIdle()

            coVerify(exactly = 0) { sendMessageUseCase(any(), any()) }
        }

    @Test
    fun `send message success clears draft stops sending and upserts message in display order`() =
        runTest {
            val olderMessage =
                createMessage(
                    id = "message-older",
                    content = "Older message",
                    createdAt = Instant.parse("2026-06-24T09:00:00Z"),
                )
            val existingSentMessage =
                createMessage(
                    id = "message-sent",
                    content = "Existing server copy",
                    createdAt = Instant.parse("2026-06-24T10:00:00Z"),
                )
            val sentMessage =
                existingSentMessage.copy(content = "Sent message")

            coEvery {
                getMessagesUseCase(
                    conversationId = CONVERSATION_ID,
                    page = ChatDetailViewModel.DEFAULT_PAGE,
                    limit = ChatDetailViewModel.DEFAULT_LIMIT,
                )
            } returns ApiResult.Success(data = listOf(existingSentMessage, olderMessage))
            coEvery {
                sendMessageUseCase(
                    conversationId = CONVERSATION_ID,
                    content = "Sent message",
                )
            } returns ApiResult.Success(data = sentMessage)

            viewModel.onEvent(ChatDetailEvent.LoadMessages(conversationId = CONVERSATION_ID))
            advanceUntilIdle()
            viewModel.onEvent(ChatDetailEvent.MessageChanged(message = "Sent message"))

            viewModel.onEvent(ChatDetailEvent.SendMessageClicked)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertThat(state.draftMessage).isEmpty()
            assertThat(state.isSending).isFalse()
            assertThat(state.hasInitialLoadError).isFalse()
            assertThat(state.messages).containsExactly(olderMessage, sentMessage).inOrder()
            assertThat(state.messages.map { it.id }).containsNoDuplicates()
            coVerify(exactly = 1) {
                sendMessageUseCase(
                    conversationId = CONVERSATION_ID,
                    content = "Sent message",
                )
            }
        }

    @Test
    fun `send message error keeps draft stops sending and emits error effect`() =
        runTest {
            val errorMessage = UiText.DynamicString("Unable to send message")

            coEvery {
                getMessagesUseCase(
                    conversationId = CONVERSATION_ID,
                    page = ChatDetailViewModel.DEFAULT_PAGE,
                    limit = ChatDetailViewModel.DEFAULT_LIMIT,
                )
            } returns ApiResult.Success(data = emptyList())
            coEvery {
                sendMessageUseCase(
                    conversationId = CONVERSATION_ID,
                    content = "Message that fails",
                )
            } returns ApiResult.Error(message = errorMessage)

            viewModel.onEvent(ChatDetailEvent.LoadMessages(conversationId = CONVERSATION_ID))
            advanceUntilIdle()
            viewModel.onEvent(ChatDetailEvent.MessageChanged(message = "Message that fails"))

            viewModel.effect.test {
                viewModel.onEvent(ChatDetailEvent.SendMessageClicked)
                advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(ChatDetailEffect.ShowErrorMessage(errorMessage))
                val state = viewModel.uiState.value
                assertThat(state.draftMessage).isEqualTo("Message that fails")
                assertThat(state.isSending).isFalse()

                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun stubLoadMessages(messages: List<Message> = emptyList()) {
        coEvery {
            getMessagesUseCase(
                conversationId = CONVERSATION_ID,
                page = ChatDetailViewModel.DEFAULT_PAGE,
                limit = ChatDetailViewModel.DEFAULT_LIMIT,
            )
        } returns ApiResult.Success(data = messages)
    }

    private fun createMessage(
        id: String,
        content: String,
        createdAt: Instant,
    ): Message =
        Message(
            id = id,
            conversationId = CONVERSATION_ID,
            user =
            User(
                id = CURRENT_USER_ID,
                email = "doan@treetask.com",
                fullName = "Doan ND",
                avatar = null,
                phone = null,
            ),
            type = MessageType.TEXT,
            content = content,
            createdAt = createdAt,
        )

    private companion object {
        const val CONVERSATION_ID = "conversation-id"
        const val OTHER_CONVERSATION_ID = "other-conversation-id"
        const val RAW_CONVERSATION_ID = "  conversation-id  "
        const val CURRENT_USER_ID = "current-user-id"
        const val OTHER_USER_ID = "other-user-id"
    }
}
