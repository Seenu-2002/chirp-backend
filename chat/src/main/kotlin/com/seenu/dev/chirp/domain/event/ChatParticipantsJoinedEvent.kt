package com.seenu.dev.chirp.domain.event

import com.seenu.dev.chirp.domain.type.ChatId
import com.seenu.dev.chirp.domain.type.UserId

data class ChatParticipantsJoinedEvent constructor(
    val chatId: ChatId,
    val userIds: Set<UserId>
)
