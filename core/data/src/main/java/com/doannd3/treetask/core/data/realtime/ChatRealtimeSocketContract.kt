package com.doannd3.treetask.core.data.realtime

internal object ChatRealtimeSocketContract {
    object Events {
        const val JOIN_CONVERSATION = "join_conversation"
        const val LEAVE_CONVERSATION = "leave_conversation"
        const val NEW_MESSAGE = "new_message"
        const val USER_TYPING = "user_typing"
        const val USER_STOP_TYPING = "user_stop_typing"
    }

    object Payload {
        const val MESSAGE = "message"
        const val ID = "_id"
        const val CONVERSATION_ID = "conversationId"
        const val SENDER = "sender"
        const val CONTENT = "content"
        const val TYPE = "type"
        const val CREATED_AT = "createdAt"
        const val UPDATED_AT = "updatedAt"
        const val USER_ID = "userId"
        const val EMAIL = "email"
        const val AVATAR = "avatar"
        const val PHONE = "phone"
        const val FULL_NAME = "fullName"
    }
}
