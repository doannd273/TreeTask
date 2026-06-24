package com.doannd3.treetask.feature.chat.ui.conversation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.doannd3.treetask.core.common.asString
import com.doannd3.treetask.core.designsystem.component.LocalGlobalAppState
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.core.model.chat.Conversation
import com.doannd3.treetask.core.model.chat.ConversationType
import com.doannd3.treetask.core.model.chat.Message
import com.doannd3.treetask.core.model.chat.MessageType
import com.doannd3.treetask.core.model.user.User
import java.time.Instant

@Composable
fun ConversationRoute(
    viewModel: ConversationViewModel = hiltViewModel(),
    onNavigationToChatDetail: (String) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val globalAppState = LocalGlobalAppState.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var shouldRefreshOnReturnFromDetail by rememberSaveable {
        mutableStateOf(false)
    }

    ConversationScreen(
        state = state,
        onEvent = viewModel::onEvent,
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && shouldRefreshOnReturnFromDetail) {
                shouldRefreshOnReturnFromDetail = false
                viewModel.onEvent(ConversationEvent.RefreshAfterReturnChatDetail)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(ConversationEvent.LoadConversations)
    }

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is ConversationEffect.ShowErrorMessage -> {
                        val errorStr = effect.message.asString(context)
                        globalAppState.showError(errorStr)
                    }

                    is ConversationEffect.NavigateToChatDetail -> {
                        shouldRefreshOnReturnFromDetail = true
                        onNavigationToChatDetail(effect.conversationId)
                    }
                }
            }
        }
    }

    LaunchedEffect(viewModel.baseErrorEffect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.baseErrorEffect.collect { message ->
                globalAppState.showError(message.asString(context))
            }
        }
    }
}

@Composable
internal fun ConversationScreen(
    state: ConversationState,
    onEvent: (ConversationEvent) -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        ConversationContent(
            modifier = Modifier.padding(paddingValues = paddingValues),
            state = state,
            onEvent = onEvent,
        )
    }
}

@Composable
internal fun ConversationContent(
    modifier: Modifier = Modifier,
    state: ConversationState,
    onEvent: (ConversationEvent) -> Unit,
) {
    when {
        state.isLoading && state.conversations.isEmpty() -> {
            ConversationLoadingState(modifier = modifier)
        }

        state.hasInitialLoadError && state.conversations.isEmpty() -> {
            ConversationErrorState(
                modifier = modifier,
                onRetry = { onEvent(ConversationEvent.Refresh) },
            )
        }

        state.conversations.isEmpty() -> {
            ConversationEmptyState(modifier = modifier)
        }

        else -> {
            ConversationList(
                modifier = modifier,
                conversations = state.conversations,
                isRefresh = state.isRefreshing,
                onConversationClick = {
                    onEvent(ConversationEvent.ConversationClicked(it))
                },
            )
        }
    }
}

@AppPreviewLightDark
@Composable
private fun ConversationScreenPreview() {
    val now = Instant.now()
    val mockUser =
        User(
            id = "u1",
            email = "alice@example.com",
            fullName = "Alice Nguyen",
            avatar = null,
            phone = null,
        )
    val mockConversations =
        listOf(
            Conversation(
                id = "c1",
                type = ConversationType.PRIVATE,
                name = "",
                creatorId = "u1",
                participants = listOf(mockUser),
                lastMessage =
                    Message(
                        id = "m1",
                        conversationId = "c1",
                        user = mockUser,
                        type = MessageType.TEXT,
                        content = "Hey, are you free tomorrow?",
                        createdAt = now,
                    ),
                lastMessageAt = now,
            ),
            Conversation(
                id = "c2",
                type = ConversationType.GROUP,
                name = "Team TreeTask",
                creatorId = "u1",
                participants = listOf(mockUser),
                lastMessage =
                    Message(
                        id = "m2",
                        conversationId = "c2",
                        user = mockUser,
                        type = MessageType.TEXT,
                        content = "Sprint review at 3pm",
                        createdAt = now.minusSeconds(3600),
                    ),
                lastMessageAt = now.minusSeconds(3600),
            ),
        )

    TreeTaskTheme {
        ConversationScreen(
            state = ConversationState(conversations = mockConversations),
            onEvent = {},
        )
    }
}
