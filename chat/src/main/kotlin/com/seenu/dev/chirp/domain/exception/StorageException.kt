package com.seenu.dev.chirp.domain.exception

class StorageException constructor(
    override val message: String? = null,
) : RuntimeException(message ?: "Unable to storage file")