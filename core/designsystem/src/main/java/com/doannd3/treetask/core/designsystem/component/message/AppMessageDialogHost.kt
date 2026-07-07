package com.doannd3.treetask.core.designsystem.component.message

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme

@Immutable
@JvmInline
value class AppMessageId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "AppMessageId must not be blank" }
    }
}

@Immutable
data class AppMessage(
    val id: AppMessageId,
    val message: String,
    val type: AppDialogType,
    val title: String? = null,
)

@Stable
class AppMessageHostState internal constructor(
    initialMessages: List<AppMessage> = emptyList(),
) {
    var messages by mutableStateOf(initialMessages.toList())
        private set

    val currentMessage: AppMessage?
        get() = messages.firstOrNull()

    fun enqueue(message: AppMessage) {
        if (messages.none { queuedMessage -> queuedMessage.id == message.id }) {
            messages = messages + message
        }
    }

    fun dismissCurrent(): AppMessage? {
        val dismissedMessage = messages.firstOrNull() ?: return null
        messages = messages.drop(1)
        return dismissedMessage
    }

    companion object {
        internal val Saver: Saver<AppMessageHostState, Any> =
            listSaver<AppMessageHostState, Any>(
                save = { state: AppMessageHostState ->
                    buildList<Any> {
                        state.messages.forEach { message ->
                            add(message.id.value)
                            add(message.message)
                            add(message.type.name)
                            add(message.title != null)
                            add(message.title.orEmpty())
                        }
                    }
                },
                restore = { restoredValues: List<Any> ->
                    if (restoredValues.size % SAVED_MESSAGE_FIELD_COUNT != 0) {
                        null
                    } else {
                        AppMessageHostState(
                            initialMessages =
                            restoredValues
                                .chunked(SAVED_MESSAGE_FIELD_COUNT)
                                .map { messageValues ->
                                    val hasTitle = messageValues[3] as Boolean
                                    AppMessage(
                                        id = AppMessageId(messageValues[0] as String),
                                        message = messageValues[1] as String,
                                        type = AppDialogType.valueOf(messageValues[2] as String),
                                        title = if (hasTitle) messageValues[4] as String else null,
                                    )
                                },
                        )
                    }
                },
            )
    }
}

private const val SAVED_MESSAGE_FIELD_COUNT = 5

@Composable
fun rememberAppMessageHostState(): AppMessageHostState =
    rememberSaveable(saver = AppMessageHostState.Saver) {
        AppMessageHostState()
    }

@Composable
fun AppMessageDialogHost(
    state: AppMessageHostState,
    onAcknowledged: (AppMessage) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentMessage = state.currentMessage ?: return

    AppMessageDialog(
        modifier = modifier,
        appDialogState =
        AppDialogState(
            title = currentMessage.title,
            message = currentMessage.message,
            type = currentMessage.type,
        ),
        onDismiss = {
            state.dismissCurrent()?.let(onAcknowledged)
        },
    )
}

@AppPreviewLightDark
@Composable
private fun AppMessageDialogHostPreview() {
    TreeTaskTheme {
        val state =
            remember {
                AppMessageHostState(
                    initialMessages =
                    listOf(
                        AppMessage(
                            id = AppMessageId("preview-error"),
                            message = "Unable to complete the request.",
                            type = AppDialogType.Error,
                        ),
                    ),
                )
            }

        AppMessageDialogHost(
            state = state,
            onAcknowledged = {},
        )
    }
}
