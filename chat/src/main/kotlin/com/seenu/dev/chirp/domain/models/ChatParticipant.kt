package com.seenu.dev.chirp.domain.models

import com.seenu.dev.chirp.domain.type.UserId


data class ChatParticipant constructor(
    val userId: UserId,
    val username: String,
    val profilePicUrl: String?
)
