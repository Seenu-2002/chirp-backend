package com.seenu.dev.chirp.api.dto

import com.seenu.dev.chirp.domain.type.UserId

data class ChatParticipantDto constructor(
    val userId: UserId,
    val username: String,
    val email: String,
    val profilePictureUrl: String?
)
