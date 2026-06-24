package com.doannd3.treetask.feature.chat.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLight
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.core.model.chat.Message
import com.doannd3.treetask.core.model.chat.MessageType
import com.doannd3.treetask.core.model.user.User
import com.doannd3.treetask.feature.chat.R
import com.doannd3.treetask.feature.chat.ui.util.toChatTimeLabel
import com.doannd3.treetask.feature.chat.ui.util.toDisplayString
import java.time.Instant
import kotlin.math.min

// region ChatDetailLoadingState
@Composable
internal fun ChatDetailLoadingState(modifier: Modifier = Modifier) {
    val placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.16f)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding =
            PaddingValues(
                horizontal = 16.dp,
                vertical = 12.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            count = CHAT_DETAIL_LOADING_ROW_COUNT,
            key = { index -> "chat_detail_loading_row_$index" },
        ) {
            ChatMessageLoadingBubble(
                placeholderColor = placeholderColor,
            )
        }
    }
}

private const val CHAT_DETAIL_LOADING_ROW_COUNT = 8

@AppPreviewLight
@Composable
private fun ChatDetailLoadingStatePreview() {
    TreeTaskTheme {
        ChatDetailLoadingState()
    }
}

@Composable
private fun ChatMessageLoadingBubble(placeholderColor: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(0.88f),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier =
                Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 10.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            ChatDetailLoadingBlock(
                modifier =
                    Modifier
                        .fillMaxWidth(0.42f)
                        .height(14.dp),
                color = placeholderColor,
            )

            ChatDetailLoadingBlock(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(18.dp),
                color = placeholderColor,
            )

            ChatDetailLoadingBlock(
                modifier =
                    Modifier
                        .fillMaxWidth(0.72f)
                        .height(18.dp),
                color = placeholderColor,
            )

            ChatDetailLoadingBlock(
                modifier =
                    Modifier
                        .width(46.dp)
                        .height(12.dp),
                color = placeholderColor,
                shape = RoundedCornerShape(4.dp),
            )
        }
    }
}

@Composable
private fun ChatDetailLoadingBlock(
    modifier: Modifier,
    color: Color,
    shape: Shape = RoundedCornerShape(6.dp),
) {
    Box(
        modifier =
            modifier.background(
                color = color,
                shape = shape,
            ),
    )
}

// endregion

// region ChatDetailErrorState
@Composable
internal fun ChatDetailErrorState(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.chat_ic_error),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = stringResource(R.string.chat_error_messages),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            OutlinedButton(onClick = onRetry) {
                Text(
                    text = stringResource(R.string.chat_retry),
                )
            }
        }
    }
}

@AppPreviewLight
@Composable
private fun ChatDetailErrorStatePreview() {
    TreeTaskTheme {
        ChatDetailErrorState(onRetry = {})
    }
}

// endregion

// region ChatDetailEmptyState

@Composable
internal fun ChatDetailEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.chat_ic_empty_messages),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            )
            Text(
                text = stringResource(R.string.chat_no_messages_yet),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@AppPreviewLight
@Composable
private fun ChatDetailEmptyStatePreview() {
    TreeTaskTheme {
        ChatDetailEmptyState()
    }
}

// endregion

// region ChatMessageList
@Composable
internal fun ChatMessageList(
    modifier: Modifier = Modifier,
    messages: List<Message>,
    isRefreshing: Boolean,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding =
            PaddingValues(
                horizontal = 16.dp,
                vertical = 12.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (isRefreshing) {
            item(key = "refreshing") {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }

        items(items = messages, key = { it.id }) { message ->
            ChatMessageBubble(message = message)
        }
    }
}

@AppPreviewLight
@Composable
private fun ChatMessageListPreview() {
    val now = Instant.now()
    val mockUser =
        User(
            id = "u1",
            email = "alice@example.com",
            fullName = "Alice Nguyen",
            avatar = null,
            phone = null,
        )
    val mockMessages =
        listOf(
            Message(
                id = "m1",
                conversationId = "c1",
                user = mockUser,
                type = MessageType.TEXT,
                content = "Hey, did you finish the task review?",
                createdAt = now,
            ),
            Message(
                id = "m2",
                conversationId = "c1",
                user = mockUser,
                type = MessageType.TEXT,
                content = "Almost done. I am checking the edge cases now.",
                createdAt = now.minusSeconds(3600),
            ),
        )

    TreeTaskTheme {
        ChatMessageList(
            messages = mockMessages,
            isRefreshing = false,
        )
    }
}

@Composable
private fun ChatMessageBubble(message: Message) {
    val timestamp = message.createdAt.toChatTimeLabel().toDisplayString()
    val senderName =
        message.user.fullName.ifBlank {
            stringResource(R.string.chat_unknown_sender)
        }

    Surface(
        modifier = Modifier.fillMaxWidth(0.88f),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier =
                Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 10.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = senderName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = timestamp,
                modifier = Modifier.align(Alignment.End),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
            )
        }
    }
}

@Composable
internal fun ChatMessageComposer(
    message: String,
    isSending: Boolean,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val canSend = message.isNotBlank() && !isSending

    Surface(
        modifier = modifier.fillMaxWidth()
            .imePadding(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column {
            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = message,
                    onValueChange = onMessageChange,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.chat_message_input_placeholder)
                        )
                    },
                    minLines = 1,
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (canSend) {
                                onSendClick()
                            }
                        },
                    )
                )

                FilledIconButton(
                    enabled = canSend,
                    onClick = onSendClick,
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.chat_ic_send),
                        contentDescription = stringResource(R.string.chat_send_message),
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@AppPreviewLight
@Composable
private fun ChatMessageComposerPreview() {
    TreeTaskTheme {
        ChatMessageComposer(
            message = "Hello Đoàn",
            isSending = false,
            onMessageChange = {},
            onSendClick = {},
        )
    }
}

// endregion
