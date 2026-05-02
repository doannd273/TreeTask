package com.doannd3.treetask.core.network.service

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.network.model.request.CreateConversationRequest
import com.doannd3.treetask.core.network.model.request.SendMessageRequest
import com.doannd3.treetask.core.network.model.response.ConversationResponse
import com.doannd3.treetask.core.network.model.response.ConversationsListResponse
import com.doannd3.treetask.core.network.model.response.MessageResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatService {
    @POST("/api/conversations/createConversation")
    suspend fun createConversation(
        @Body request: CreateConversationRequest,
    ): ApiResult<ConversationResponse>

    @GET("/api/conversations/getConversations")
    suspend fun getConversations(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): ApiResult<ConversationsListResponse>

    @POST("/api/messages/sendMessage")
    suspend fun sendMessage(
        @Body request: SendMessageRequest,
    ): ApiResult<MessageResponse>

    @PUT("/api/conversations/updateConversation/{conversationId}")
    suspend fun updateConversation(
        @Path("conversationId") conversationId: String,
        @Body request: CreateConversationRequest,
    ): ApiResult<ConversationsListResponse>
}
