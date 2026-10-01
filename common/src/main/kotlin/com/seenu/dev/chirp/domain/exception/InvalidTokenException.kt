package com.seenu.dev.chirp.domain.exception

class InvalidTokenException constructor(
    override val message: String?
) : RuntimeException(
    message ?: "Invalid token"
)