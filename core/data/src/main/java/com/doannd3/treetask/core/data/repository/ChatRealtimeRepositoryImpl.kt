package com.doannd3.treetask.core.data.repository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.data.realtime.ChatRealtimeSocketContract.Events
import com.doannd3.treetask.core.data.realtime.ChatRealtimeSocketContract.Payload
import com.doannd3.treetask.core.data.realtime.toNewMessageRealtimeEventOrNull
import com.doannd3.treetask.core.data.realtime.toTypingRealtimeEventOrNull
import com.doannd3.treetask.core.datastore.token.TokenStorage
import com.doannd3.treetask.core.domain.repository.ChatRealtimeRepository
import com.doannd3.treetask.core.model.chat.ChatRealtimeEvent
import com.doannd3.treetask.core.network.BuildConfig
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import java.lang.IllegalArgumentException
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class ChatRealtimeRepositoryImpl
@Inject
constructor(
    private val tokenStorage: TokenStorage,
) : ChatRealtimeRepository {
    private val events = MutableSharedFlow<ChatRealtimeEvent>(extraBufferCapacity = 64)

    private var socket: Socket? = null

    override fun observeEvents(): Flow<ChatRealtimeEvent> = events.asSharedFlow()

    override suspend fun connect(): ApiResult<Unit> {
        val currentSocket = socket
        if (currentSocket?.connected() == true) {
            return ApiResult.Success(data = Unit)
        }

        val accessToken = tokenStorage.getAccessToken().first()
        if (accessToken.isNullOrBlank()) {
            return ApiResult.Error(
                exception = IllegalStateException("Missing access token for chat realtime"),
            )
        }

        currentSocket?.let(::disposeSocket)

        return suspendCancellableCoroutine { continuation ->
            val options =
                IO.Options
                    .builder()
                    .setForceNew(true)
                    .setReconnection(true)
                    .setAuth(mapOf("token" to accessToken))
                    .build()

            val newSocket = IO.socket(socketServerUri(), options)

            val onConnect =
                Emitter.Listener {
                    clearConnectionListeners(newSocket)
                    if (continuation.isActive) {
                        continuation.resume(ApiResult.Success(data = Unit))
                    }
                }

            val onConnectError =
                Emitter.Listener { args ->
                    clearConnectionListeners(newSocket)
                    val error =
                        args.firstOrNull() as? Throwable
                            ?: IllegalStateException(
                                args.firstOrNull()?.toString() ?: "Socket connect error",
                            )

                    if (continuation.isActive) {
                        continuation.resume(ApiResult.Error(exception = error))
                    }
                }

            newSocket.once(Socket.EVENT_CONNECT, onConnect)
            newSocket.once(Socket.EVENT_CONNECT_ERROR, onConnectError)

            registerRealtimeListeners(newSocket)

            socket = newSocket

            continuation.invokeOnCancellation {
                clearConnectionListeners(newSocket)
                disposeSocket(newSocket)
                if (socket === newSocket) {
                    socket = null
                }
            }

            newSocket.connect()
        }
    }

    private fun socketServerUri(): URI {
        val restUri = URI.create(BuildConfig.BASE_URL)
        return URI(restUri.scheme, restUri.authority, null, null, null)
    }

    private fun disposeSocket(socket: Socket) {
        socket.off()
        socket.disconnect()
    }

    private fun clearConnectionListeners(socket: Socket) {
        socket.off(Socket.EVENT_CONNECT)
        socket.off(Socket.EVENT_CONNECT_ERROR)
    }

    override suspend fun disconnect(): ApiResult<Unit> {
        socket?.let(::disposeSocket)
        socket = null
        return ApiResult.Success(data = Unit)
    }

    override suspend fun joinConversation(conversationId: String): ApiResult<Unit> =
        emitConversationRoomCommand(
            event = Events.JOIN_CONVERSATION,
            conversationId = conversationId,
        )

    override suspend fun leaveConversation(conversationId: String): ApiResult<Unit> =
        emitConversationRoomCommand(
            event = Events.LEAVE_CONVERSATION,
            conversationId = conversationId,
        )

    private fun registerRealtimeListeners(socket: Socket) {
        socket.on(Events.NEW_MESSAGE) { args ->
            args.firstOrNull()
                .toNewMessageRealtimeEventOrNull()
                ?.let(events::tryEmit)
        }
        socket.on(Events.USER_TYPING) { args ->
            args.firstOrNull()
                .toTypingRealtimeEventOrNull(ChatRealtimeEvent::TypingStarted)
                ?.let(events::tryEmit)
        }
        socket.on(Events.USER_STOP_TYPING) { args ->
            args.firstOrNull()
                .toTypingRealtimeEventOrNull(ChatRealtimeEvent::TypingStopped)
                ?.let(events::tryEmit)
        }
    }

    private fun emitConversationRoomCommand(
        event: String,
        conversationId: String,
    ): ApiResult<Unit> {
        if (conversationId.isBlank()) {
            return ApiResult.Error(
                exception = IllegalArgumentException("Conversation id cannot be blank"),
            )
        }

        val currentSocket = socket
        if (currentSocket?.connected() != true) {
            return ApiResult.Error(
                exception = IllegalStateException("Chat realtime socket is not connected"),
            )
        }

        return runCatching {
            currentSocket.emit(
                event,
                JSONObject().put(Payload.CONVERSATION_ID, conversationId),
            )
        }.fold(
            onSuccess = { ApiResult.Success(data = Unit) },
            onFailure = { ApiResult.Error(exception = it) },
        )
    }
}
