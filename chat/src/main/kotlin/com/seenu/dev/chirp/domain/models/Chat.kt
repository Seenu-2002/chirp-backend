package com.seenu.dev.chirp.domain.models

import com.seenu.dev.chirp.domain.type.ChatId
import java.time.Instant

data class Chat constructor(
    val id: ChatId,
    val participant: Set<ChatParticipant>,
    val lastMessage: ChatMessage? = null,
    val creator: ChatParticipant,
    val lastActivityAt: Instant,
    val createdAt: Instant,
)
