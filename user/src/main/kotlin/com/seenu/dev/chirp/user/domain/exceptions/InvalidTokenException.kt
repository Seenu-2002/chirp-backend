package com.seenu.dev.chirp.user.domain.exceptions

class InvalidTokenException constructor(
    override val message: String?
) : RuntimeException(
    message ?: "Invalid token"
)