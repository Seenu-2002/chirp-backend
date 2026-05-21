package com.seenu.dev.chirp.user.domain.model

import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi

typealias UserId = UUID

data class User constructor(
    val id: UserId,
    val userName: String,
    val email: String,
    val hasEmailVerified: Boolean
)