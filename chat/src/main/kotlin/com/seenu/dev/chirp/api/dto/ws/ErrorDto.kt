package com.seenu.dev.chirp.api.dto.ws

data class ErrorDto constructor(
    val code: String,
    val message: String
)