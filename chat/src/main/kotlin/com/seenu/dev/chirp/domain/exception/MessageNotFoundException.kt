package com.seenu.dev.chirp.domain.exception

import com.seenu.dev.chirp.domain.type.ChatMessageId

class MessageNotFoundException(private val id: ChatMessageId) : RuntimeException("Message with id $id not found.")