package com.doannd3.treetask.feature.chat.ui.detail

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.doannd3.treetask.core.common.asString
import com.doannd3.treetask.core.designsystem.component.CommonHeader
import com.doannd3.treetask.core.designsystem.component.message.AppDialogType
import com.doannd3.treetask.core.designsystem.component.message.AppMessage
import com.doannd3.treetask.core.designsystem.component.message.AppMessageDialogHost
import com.doannd3.treetask.core.designsystem.component.message.AppMessageId
import com.doannd3.treetask.core.designsystem.component.message.rememberAppMessageHostState
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.core.model.chat.Message
import com.doannd3.treetask.core.model.chat.MessageType
import com.doannd3.treetask.core.model.user.User
import com.doannd3.treetask.feature.chat.R
import kotlinx.coroutines.awaitCancellation
import java.time.Instant

@Composable
fun ChatDetailRoute(
    viewModel: ChatDetailViewModel = hiltViewModel(),
    conversationId: String,
    onBackClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val messageHostState = rememberAppMessageHostState()
    val currentContext by rememberUpdatedState(context)
    val currentOnBackClick by rememberUpdatedState(onBackClick)

    ChatDetailScreen(
        state = state,
        onBackClick = { viewModel.onEvent(ChatDetailEvent.BackClick) },
        onMessageChange = { viewModel.onEvent(ChatDetailEvent.MessageChanged(message = it)) },
        onSendClick = { viewModel.onEvent(ChatDetailEvent.SendMessageClicked) },
        onRefresh = { viewModel.onEvent(ChatDetailEvent.Refresh) },
    )

    AppMessageDialogHost(
        state = messageHostState,
        onAcknowledged = {},
    )

    LaunchedEffect(conversationId, viewModel) {
        viewModel.onEvent(ChatDetailEvent.LoadMessages(conversationId = conversationId))
    }

    LaunchedEffect(conversationId, lifecycleOwner, viewModel) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.onEvent(ChatDetailEvent.StartRealtime(conversationId = conversationId))

            try {
                awaitCancellation()
            } finally {
                viewModel.onEvent(ChatDetailEvent.StopRealtime(conversationId = conversationId))
            }
        }
    }

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is ChatDetailEffect.ShowErrorMessage -> {
                        messageHostState.enqueue(
                            AppMessage(
                                id = ChatDetailMessageIds.Error,
                                message = effect.message.asString(currentContext),
                                type = AppDialogType.Error,
                            ),
                        )
                    }

                    ChatDetailEffect.NavigateBack -> {
                        currentOnBackClick()
                    }
                }
            }
        }
    }

    LaunchedEffect(viewModel.baseErrorEffect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.baseErrorEffect.collect { message ->
                messageHostState.enqueue(
                    AppMessage(
                        id = ChatDetailMessageIds.Error,
                        message = message.asString(currentContext),
                        type = AppDialogType.Error,
                    ),
                )
            }
        }
    }
}

@Composable
internal fun ChatDetailScreen(
    state: ChatDetailState,
    onBackClick: () -> Unit,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onRefresh: () -> Unit,
) {
    val messages = state.messages
    val isLoading = state.isLoading
    val isRefreshing = state.isRefreshing
    val hasInitialLoadError = state.hasInitialLoadError
    val isTyping = state.typingUserId != null
    val currentUserId = state.currentUserId
    val draftMessage = state.draftMessage
    val isSending = state.isSending

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CommonHeader(
                title = stringResource(R.string.chat_detail_title),
                onNavigateBack = onBackClick,
            )
        },
        bottomBar = {
            ChatMessageComposer(
                message = draftMessage,
                isSending = isSending,
                onMessageChange = onMessageChange,
                onSendClick = onSendClick,
            )
        },
    ) { paddingValues ->
        ChatDetailContent(
            messages = messages,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            hasInitialLoadError = hasInitialLoadError,
            isTyping = isTyping,
            currentUserId = currentUserId,
            onRetry = onRefresh,
            modifier = Modifier.padding(paddingValues = paddingValues),
        )
    }
}

@AppPreviewLightDark
@Composable
private fun ChatDetailScreenPreview() {
    TreeTaskTheme {
        ChatDetailScreen(
            state =
            ChatDetailState(
                conversationId = "conversation-preview",
                currentUserId = "user-doan",
                messages = chatDetailPreviewMessages(),
                draftMessage = "Can you review this task?",
                isSending = false,
            ),
            onBackClick = {},
            onMessageChange = {},
            onSendClick = {},
            onRefresh = {},
        )
    }
}

@Composable
internal fun ChatDetailContent(
    messages: List<Message>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    hasInitialLoadError: Boolean,
    isTyping: Boolean,
    currentUserId: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        isLoading && messages.isEmpty() -> {
            ChatDetailLoadingState(modifier = modifier)
        }

        hasInitialLoadError && messages.isEmpty() -> {
            ChatDetailErrorState(
                modifier = modifier,
                onRetry = onRetry,
            )
        }

        messages.isEmpty() && !isTyping -> {
            ChatDetailEmptyState(modifier = modifier)
        }

        else -> {
            ChatMessageList(
                modifier = modifier,
                messages = messages,
                isRefreshing = isRefreshing,
                isTyping = isTyping,
                currentUserId = currentUserId,
            )
        }
    }
}

private fun chatDetailPreviewMessages(): List<Message> {
    val now = Instant.parse("2026-06-24T10:30:00Z")
    val alice =
        User(
            id = "user-alice",
            email = "alice@example.com",
            fullName = "Alice Nguyen",
            avatar = null,
            phone = null,
        )
    val doan =
        User(
            id = "user-doan",
            email = "doan@example.com",
            fullName = "Đoàn",
            avatar = null,
            phone = null,
        )

    return listOf(
        Message(
            id = "message-1",
            conversationId = "conversation-preview",
            user = alice,
            type = MessageType.TEXT,
            content = "Hey, did you finish the task review?",
            createdAt = now.minusSeconds(3600),
        ),
        Message(
            id = "message-2",
            conversationId = "conversation-preview",
            user = doan,
            type = MessageType.TEXT,
            content = "Almost done. I am checking the edge cases now.",
            createdAt = now.minusSeconds(600),
        ),
    )
}

private object ChatDetailMessageIds {
    val Error = AppMessageId("chat-detail-error")
}
