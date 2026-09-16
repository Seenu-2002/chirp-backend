package com.seenu.dev.chirp.domain.exception

import com.seenu.dev.chirp.domain.type.UserId

class ChatParticipantNotFoundException constructor(private val id: UserId) :
    RuntimeException("The chat participant with ID $id not found.")