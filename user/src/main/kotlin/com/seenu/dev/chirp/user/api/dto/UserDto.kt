package com.seenu.dev.chirp.user.api.dto

import com.seenu.dev.chirp.domain.type.UserId

data class UserDto constructor(
    val id: UserId,
    val userName: String,
    val email: String,
    val hasEmailVerified: Boolean,
)