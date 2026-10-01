package com.seenu.dev.chirp.api.dto.ws

import com.seenu.dev.chirp.domain.type.ChatId

data class ChatParticipantsChangedDto constructor(
    val chatId: ChatId
)