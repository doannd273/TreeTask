package com.doannd3.treetask.core.data.respository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.data.mapper.mapOrNull
import com.doannd3.treetask.core.data.mapper.toConversationOrNull
import com.doannd3.treetask.core.data.mapper.toMessageOrNull
import com.doannd3.treetask.core.domain.repository.ChatRepository
import com.doannd3.treetask.core.model.chat.Conversation
import com.doannd3.treetask.core.model.chat.ConversationType
import com.doannd3.treetask.core.model.chat.Message
import com.doannd3.treetask.core.model.chat.MessageType
import com.doannd3.treetask.core.network.model.request.CreateConversationRequest
import com.doannd3.treetask.core.network.model.request.SendMessageRequest
import com.doannd3.treetask.core.network.service.ChatService
import javax.inject.Inject

class ChatRepositoryImpl
    @Inject
    constructor(
        private val chatService: ChatService,
    ) : ChatRepository {
        override suspend fun getMessages(
            conversationId: String,
            page: Int,
            limit: Int,
        ): ApiResult<List<Message>> {
            val result =
                chatService.getMessages(
                    conversationId = conversationId,
                    page = page,
                    limit = limit,
                )

            return result.mapSuccessResult { success ->
                val data = success.data ?: return@mapSuccessResult missingResponseDataError()
                val messages =
                    data.messages.mapOrNull { message ->
                        message.toMessageOrNull()
                    } ?: return@mapSuccessResult missingResponseDataError()

                ApiResult.Success(
                    data = messages,
                    message = success.message,
                )
            }
        }

        override suspend fun getConversations(
            page: Int,
            limit: Int,
        ): ApiResult<List<Conversation>> {
            val result =
                chatService.getConversations(
                    page = page,
                    limit = limit,
                )

            return result.mapSuccessResult { success ->
                val data = success.data ?: return@mapSuccessResult missingResponseDataError()
                val conversations =
                    data.conversations.mapOrNull { conversation ->
                        conversation.toConversationOrNull()
                    } ?: return@mapSuccessResult missingResponseDataError()

                ApiResult.Success(
                    data = conversations,
                    message = success.message,
                )
            }
        }

        override suspend fun sendMessage(
            conversationId: String,
            content: String,
        ): ApiResult<Message> {
            val request =
                SendMessageRequest(
                    conversationId = conversationId,
                    content = content,
                    type = MessageType.TEXT.type,
                )

            val result = chatService.sendMessage(request = request)
            return result.mapSuccessResult { success ->
                val data = success.data ?: return@mapSuccessResult missingResponseDataError()
                val message =
                    data.toMessageOrNull()
                        ?: return@mapSuccessResult missingResponseDataError()

                ApiResult.Success(
                    data = message,
                    message = success.message,
                )
            }
        }

        override suspend fun createPrivateConversation(otherUserId: String): ApiResult<Conversation> {
            val request =
                CreateConversationRequest(
                    type = ConversationType.PRIVATE.type,
                    name = PRIVATE_CONVERSATION_NAME,
                    participantIds = listOf(otherUserId),
                )

            val result = chatService.createConversation(request = request)
            return result.mapSuccessResult { success ->
                val data = success.data ?: return@mapSuccessResult missingResponseDataError()
                val conversation =
                    data.toConversationOrNull()
                        ?: return@mapSuccessResult missingResponseDataError()

                ApiResult.Success(
                    data = conversation,
                    message = success.message,
                )
            }
        }

        private companion object {
            const val PRIVATE_CONVERSATION_NAME = ""
        }
    }
