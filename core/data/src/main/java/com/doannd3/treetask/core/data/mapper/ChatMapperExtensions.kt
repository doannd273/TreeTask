package com.doannd3.treetask.core.data.mapper

import com.doannd3.treetask.core.model.chat.MessageType

internal fun String?.requiredMessageTypeOrNull(): MessageType? {
    return MessageType.entries.firstOrNull { messageType ->
        messageType.type == this
    }
}
