package com.seenu.dev.chirp.user.domain.model

data class EmailVerificationToken constructor(
    val id: Long,
    val token: String,
    val user: User
)