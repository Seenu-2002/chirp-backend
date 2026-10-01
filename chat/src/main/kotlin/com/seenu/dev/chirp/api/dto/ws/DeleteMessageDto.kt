package com.seenu.dev.chirp.api.dto.ws

import com.seenu.dev.chirp.domain.type.ChatId
import com.seenu.dev.chirp.domain.type.ChatMessageId

data class DeleteMessageDto constructor(
    val chatId: ChatId,
    val messageId: ChatMessageId
)