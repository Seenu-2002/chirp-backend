package com.seenu.dev.chirp.domain.exception

class InvalidChatSizeException : RuntimeException(
    "There must be at least 2 participants to create a chat."
)