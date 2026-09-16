package com.seenu.dev.chirp.api.dto

import com.seenu.dev.chirp.domain.type.ChatId
import com.seenu.dev.chirp.domain.type.ChatMessageId
import com.seenu.dev.chirp.domain.type.UserId
import java.time.Instant

data class ChatMessageDto constructor(
    val id: ChatMessageId,
    val chatId: ChatId,
    val content: String,
    val createdAt: Instant,
    val senderId: UserId
)
