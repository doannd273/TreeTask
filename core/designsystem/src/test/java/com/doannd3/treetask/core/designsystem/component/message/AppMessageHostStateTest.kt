package com.doannd3.treetask.core.designsystem.component.message

import androidx.compose.runtime.saveable.SaverScope
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AppMessageHostStateTest {
    @Test
    fun `messages are displayed and dismissed in insertion order`() {
        val firstMessage = createMessage(id = "first")
        val secondMessage = createMessage(id = "second")
        val state = AppMessageHostState()

        state.enqueue(firstMessage)
        state.enqueue(secondMessage)

        assertThat(state.currentMessage).isEqualTo(firstMessage)
        assertThat(state.dismissCurrent()).isEqualTo(firstMessage)
        assertThat(state.currentMessage).isEqualTo(secondMessage)
        assertThat(state.dismissCurrent()).isEqualTo(secondMessage)
        assertThat(state.currentMessage).isNull()
    }

    @Test
    fun `message with queued id is not duplicated`() {
        val state = AppMessageHostState()

        state.enqueue(createMessage(id = "error", message = "First error"))
        state.enqueue(createMessage(id = "error", message = "Updated error"))

        assertThat(state.messages).containsExactly(
            createMessage(id = "error", message = "First error"),
        )
    }

    @Test
    fun `dismissed message id can be enqueued again`() {
        val state = AppMessageHostState()
        val firstMessage = createMessage(id = "success", message = "First success")
        val repeatedMessage = createMessage(id = "success", message = "Second success")

        state.enqueue(firstMessage)
        state.dismissCurrent()
        state.enqueue(repeatedMessage)

        assertThat(state.currentMessage).isEqualTo(repeatedMessage)
    }

    @Test
    fun `saver restores queued message data`() {
        val state =
            AppMessageHostState(
                initialMessages =
                listOf(
                    createMessage(
                        id = "success",
                        message = "Task completed",
                        type = AppDialogType.Success,
                        title = "Done",
                    ),
                    createMessage(id = "error"),
                ),
            )
        val saverScope = SaverScope { true }
        val savedState =
            with(AppMessageHostState.Saver) {
                saverScope.save(state)
            }

        val restoredState = savedState?.let(AppMessageHostState.Saver::restore)

        assertThat(restoredState?.messages).containsExactlyElementsIn(state.messages).inOrder()
    }

    private fun createMessage(
        id: String,
        message: String = "Message",
        type: AppDialogType = AppDialogType.Error,
        title: String? = null,
    ): AppMessage =
        AppMessage(
            id = AppMessageId(id),
            message = message,
            type = type,
            title = title,
        )
}
