package com.doannd3.treetask.feature.chat.ui.detail

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.doannd3.treetask.core.designsystem.component.LocalGlobalAppState
import com.doannd3.treetask.feature.chat.R

@Composable
fun ChatDetailRoute(
    viewModel: ChatDetailViewModel = hiltViewModel(),
    conversationId: String,
    onBackClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val globalAppState = LocalGlobalAppState.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    ChatDetailScreen(state = state, onEvent = viewModel::onEvent)

    LaunchedEffect(conversationId) {
        viewModel.onEvent(ChatDetailEvent.LoadMessages(conversationId = conversationId))
    }

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is ChatDetailEffect.ShowErrorMessage -> {
                        globalAppState.showError(effect.message.asString(context = context))
                    }

                    ChatDetailEffect.NavigateBack -> {
                        onBackClick()
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
internal fun ChatDetailScreen(
    state: ChatDetailState,
    onEvent: (ChatDetailEvent) -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CommonHeader(
                title = stringResource(R.string.chat_detail_title),
                onNavigateBack = { onEvent(ChatDetailEvent.BackClick) },
            )
        },
    ) { paddingValues ->
        ChatDetailContent(
            modifier = Modifier.padding(paddingValues = paddingValues),
            state = state,
            onEvent = onEvent,
        )
    }
}

@Composable
internal fun ChatDetailContent(
    modifier: Modifier = Modifier,
    state: ChatDetailState,
    onEvent: (ChatDetailEvent) -> Unit,
) {
    when {
        state.isLoading && state.messages.isEmpty() -> {
            ChatDetailLoadingState(modifier = modifier)
        }

        state.hasInitialLoadError && state.messages.isEmpty() -> {
            ChatDetailErrorState(
                modifier = modifier,
                onRetry = { onEvent(ChatDetailEvent.Refresh) },
            )
        }

        state.messages.isEmpty() -> {
            ChatDetailEmptyState(modifier = modifier)
        }

        else -> {
            ChatMessageList(
                modifier = modifier,
                messages = state.messages,
                isRefreshing = state.isRefreshing,
            )
        }
    }
}
