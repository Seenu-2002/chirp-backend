package com.seenu.dev.chirp.api.dto

import com.seenu.dev.chirp.domain.type.ChatId
import java.time.Instant

data class ChatDto constructor(
    val id: ChatId,
    val participants: List<ChatParticipantDto>,
    val lastActivityAt: Instant,
    val lastMessage: ChatMessageDto?,
    val creator: ChatParticipantDto
)
