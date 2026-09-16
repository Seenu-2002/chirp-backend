package com.seenu.dev.chirp.domain.models

import com.seenu.dev.chirp.domain.type.ChatId
import com.seenu.dev.chirp.domain.type.ChatMessageId
import java.time.Instant

data class ChatMessage constructor(
    val id: ChatMessageId,
    val chatId: ChatId,
    val sender: ChatParticipant,
    val content: String,
    val createdAt: Instant
)
