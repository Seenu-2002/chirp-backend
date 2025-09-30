package com.seenu.dev.chirp.user.api.dto

data class AuthenticatedUserDto constructor(
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String
)