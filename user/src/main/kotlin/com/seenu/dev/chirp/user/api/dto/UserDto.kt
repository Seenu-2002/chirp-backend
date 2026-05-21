package com.seenu.dev.chirp.user.api.dto

import com.seenu.dev.chirp.user.domain.model.UserId
import jakarta.validation.constraints.Email
import org.hibernate.validator.constraints.Length

data class UserDto constructor(
    val id: UserId,
    val userName: String,
    val email: String,
    val hasEmailVerified: Boolean,
)