package com.seenu.dev.chirp.user.domain.model

import com.seenu.dev.chirp.domain.type.UserId

data class User constructor(
    val id: UserId,
    val userName: String,
    val email: String,
    val hasEmailVerified: Boolean
)