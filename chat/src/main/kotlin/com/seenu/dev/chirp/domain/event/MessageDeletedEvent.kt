package com.seenu.dev.chirp.domain.event

import com.seenu.dev.chirp.domain.type.ChatId
import com.seenu.dev.chirp.domain.type.ChatMessageId

data class MessageDeletedEvent constructor(
    val chatId: ChatId,
    val messageId: ChatMessageId,
)