package com.doannd3.treetask.feature.chat.ui.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLight
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.core.model.chat.Conversation
import com.doannd3.treetask.core.model.chat.ConversationType
import com.doannd3.treetask.core.model.chat.Message
import com.doannd3.treetask.core.model.chat.MessageType
import com.doannd3.treetask.core.model.user.User
import com.doannd3.treetask.feature.chat.R
import com.doannd3.treetask.feature.chat.ui.util.toChatTimeLabel
import com.doannd3.treetask.feature.chat.ui.util.toDisplayString
import java.time.Instant

// region ConversationLoadingState
@Composable
internal fun ConversationLoadingState(modifier: Modifier = Modifier) {
    val placeholderColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        item(key = "conversation_loading_header") {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(26.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                        strokeWidth = 3.dp,
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ConversationLoadingBlock(
                        modifier =
                            Modifier
                                .fillMaxWidth(0.48f)
                                .height(20.dp),
                        color = placeholderColor,
                    )
                    ConversationLoadingBlock(
                        modifier =
                            Modifier
                                .fillMaxWidth(0.72f)
                                .height(14.dp),
                        color = placeholderColor,
                    )
                }
            }
        }

        items(
            count = CONVERSATION_LOADING_ROW_COUNT,
            key = { index -> "conversation_loading_row_$index" },
        ) { index ->
            ConversationLoadingRow(placeholderColor = placeholderColor)

            if (index < CONVERSATION_LOADING_ROW_COUNT - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 76.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }
        }
    }
}

private const val CONVERSATION_LOADING_ROW_COUNT = 8

@AppPreviewLight
@Composable
private fun ConversationLoadingStatePreview() {
    TreeTaskTheme {
        ConversationLoadingState()
    }
}

//endregion

// region ConversationErrorState
@Composable
internal fun ConversationErrorState(
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
                text = stringResource(R.string.chat_error_conversations),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            OutlinedButton(onClick = onRetry) {
                Text(text = stringResource(R.string.chat_retry))
            }
        }
    }
}

@AppPreviewLight
@Composable
private fun ConversationErrorStatePreview() {
    TreeTaskTheme {
        ConversationErrorState(onRetry = {})
    }
}

// endregion

// region ConversationEmptyState
@Composable
internal fun ConversationEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.chat_ic_empty_chat),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            )
            Text(
                text = stringResource(R.string.chat_empty_conversations),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@AppPreviewLight
@Composable
private fun ConversationEmptyStatePreview() {
    TreeTaskTheme {
        ConversationEmptyState()
    }
}

//endregion

// region ConversationList

@Composable
internal fun ConversationList(
    modifier: Modifier = Modifier,
    conversations: List<Conversation>,
    isRefresh: Boolean,
    onConversationClick: (Conversation) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        if (isRefresh) {
            item(key = "refreshing") {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }

        items(
            items = conversations,
            key = { it.id },
        ) { conversation ->
            ConversationRow(
                conversation = conversation,
                onClick = { onConversationClick(conversation) },
            )
        }
    }
}

@AppPreviewLight
@Composable
private fun ConversationListPreview() {
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
            Conversation(
                id = "c3",
                type = ConversationType.PRIVATE,
                name = "",
                creatorId = "u1",
                participants = listOf(mockUser),
                lastMessage = null,
                lastMessageAt = null,
            ),
        )

    TreeTaskTheme {
        ConversationList(
            conversations = mockConversations,
            isRefresh = false,
            onConversationClick = {},
        )
    }
}

@Composable
internal fun ConversationRow(
    modifier: Modifier = Modifier,
    currentUserId: String? = null,
    conversation: Conversation,
    onClick: () -> Unit,
) {
    val displayUser =
        remember(conversation.participants, currentUserId) {
            conversation.participants.firstOrNull { user ->
                currentUserId == null || user.id != currentUserId
            }
        }

    val title =
        conversation.name
            .ifBlank {
                displayUser?.fullName.orEmpty()
            }.ifBlank { stringResource(R.string.chat_unknown_conversation) }

    val subTitle =
        conversation.lastMessage?.content ?: stringResource(R.string.chat_no_messages_yet)

    val time = conversation.lastMessageAt.toChatTimeLabel().toDisplayString()

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick,
                ).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ConversationAvatar(name = title)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = subTitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        if (time.isNotBlank()) {
            Text(
                text = time,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun ConversationLoadingRow(placeholderColor: Color) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ConversationLoadingBlock(
            modifier = Modifier.size(48.dp),
            color = placeholderColor,
            shape = CircleShape,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ConversationLoadingBlock(
                modifier =
                    Modifier
                        .fillMaxWidth(0.66f)
                        .height(18.dp),
                color = placeholderColor,
            )
            ConversationLoadingBlock(
                modifier =
                    Modifier
                        .fillMaxWidth(0.92f)
                        .height(14.dp),
                color = placeholderColor,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        ConversationLoadingBlock(
            modifier =
                Modifier
                    .width(42.dp)
                    .height(14.dp),
            color = placeholderColor,
        )
    }
}

@Composable
private fun ConversationLoadingBlock(
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

@Composable
private fun ConversationAvatar(
    modifier: Modifier = Modifier,
    name: String,
) {
    val initial =
        name
            .trim()
            .firstOrNull()
            ?.uppercaseChar()
            ?.toString()
            .orEmpty()

    Box(
        modifier =
            modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 1,
        )
    }
}

// endregion
