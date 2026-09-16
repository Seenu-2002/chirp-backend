package com.seenu.dev.chirp.api.dto

import com.seenu.dev.chirp.domain.type.UserId
import jakarta.validation.constraints.Size

data class CreateChatRequest constructor(
    @field:Size(
        min = 1,
        message = "Chats must have at least 2 unique participants"
    )
    val otherUserIds: List<UserId>
)
