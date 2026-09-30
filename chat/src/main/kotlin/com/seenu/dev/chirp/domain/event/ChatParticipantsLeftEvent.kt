package com.seenu.dev.chirp.domain.event

import com.seenu.dev.chirp.domain.type.ChatId
import com.seenu.dev.chirp.domain.type.UserId

data class ChatParticipantsLeftEvent constructor(
    val chatId: ChatId,
    val userId: UserId
)