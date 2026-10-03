package com.seenu.dev.chirp.domain.exception

class InvalidProfilePictureException constructor(
    override val message: String? = null
) : RuntimeException(message ?: "Invalid profile picture data")