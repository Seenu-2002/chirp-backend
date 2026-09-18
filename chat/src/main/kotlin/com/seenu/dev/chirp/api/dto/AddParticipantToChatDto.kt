package com.seenu.dev.chirp.api.dto

import com.seenu.dev.chirp.domain.type.UserId
import jakarta.validation.constraints.Size

data class AddParticipantToChatDto constructor(
    @field:Size(min = 1, message = "At least one user ID must be provided")
    val userIds: Set<UserId>
)
