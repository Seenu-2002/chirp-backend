package com.seenu.dev.chirp.user.domain.model

data class AuthenticatedUser constructor(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)