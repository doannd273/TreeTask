package com.doannd3.treetask.core.data.realtime

import com.doannd3.treetask.core.data.mapper.toMessageOrNull
import com.doannd3.treetask.core.data.realtime.ChatRealtimeSocketContract.Payload
import com.doannd3.treetask.core.model.chat.ChatRealtimeEvent
import com.doannd3.treetask.core.network.model.response.MessageResponse
import com.doannd3.treetask.core.network.model.response.ParticipantUser
import org.json.JSONObject

internal fun Any?.toNewMessageRealtimeEventOrNull(): ChatRealtimeEvent.NewMessage? =
    runCatching {
        asJsonObjectOrNull()
            ?.messagePayloadOrSelf()
            ?.toMessageResponse()
            ?.toMessageOrNull()
            ?.let(ChatRealtimeEvent::NewMessage)
    }.getOrNull()

internal fun Any?.toTypingRealtimeEventOrNull(
    eventFactory: (conversationId: String, userId: String) -> ChatRealtimeEvent,
): ChatRealtimeEvent? =
    runCatching {
        val json = asJsonObjectOrNull() ?: return@runCatching null
        val conversationId = json.stringOrNull(Payload.CONVERSATION_ID) ?: return@runCatching null
        val userId = json.stringOrNull(Payload.USER_ID) ?: return@runCatching null

        eventFactory(conversationId, userId)
    }.getOrNull()

private fun Any?.asJsonObjectOrNull(): JSONObject? = this as? JSONObject

private fun JSONObject.messagePayloadOrSelf(): JSONObject = optJSONObject(Payload.MESSAGE) ?: this

private fun JSONObject.toMessageResponse(): MessageResponse =
    MessageResponse(
        id = stringOrNull(Payload.ID),
        conversationId = stringOrNull(Payload.CONVERSATION_ID),
        sender = jsonObjectOrNull(Payload.SENDER)?.toParticipantUser(),
        content = stringOrNull(Payload.CONTENT),
        type = stringOrNull(Payload.TYPE),
        createdAt = stringOrNull(Payload.CREATED_AT),
        updatedAt = stringOrNull(Payload.UPDATED_AT),
    )

private fun JSONObject.toParticipantUser(): ParticipantUser =
    ParticipantUser(
        id = stringOrNull(Payload.ID),
        email = stringOrNull(Payload.EMAIL),
        avatar = stringOrNull(Payload.AVATAR),
        phone = stringOrNull(Payload.PHONE),
        fullName = stringOrNull(Payload.FULL_NAME),
    )

private fun JSONObject.jsonObjectOrNull(key: String): JSONObject? = if (has(key) && !isNull(key)) optJSONObject(key) else null

private fun JSONObject.stringOrNull(key: String): String? {
    if (!has(key) || isNull(key)) return null
    return optString(key).trim().takeIf { it.isNotEmpty() }
}
