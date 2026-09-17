package com.seenu.dev.chirp.infra.database.mappers

import com.seenu.dev.chirp.domain.models.Chat
import com.seenu.dev.chirp.domain.models.ChatMessage
import com.seenu.dev.chirp.domain.models.ChatParticipant
import com.seenu.dev.chirp.infra.database.entities.ChatEntity
import com.seenu.dev.chirp.infra.database.entities.ChatParticipantEntity

fun ChatEntity.toChat(lastMessage: ChatMessage? = null): Chat {
    return Chat(
        id = this.id!!,
        participant = participants.map {
            it.toChatParticipant()
        }.toSet(),
        creator = this.creator.toChatParticipant(),
        lastActivityAt = lastMessage?.createdAt ?: createdAt,
        createdAt = this.createdAt,
        lastMessage = lastMessage
    )
}

fun ChatParticipantEntity.toChatParticipant(): ChatParticipant {
    return ChatParticipant(
        userId = this.userId,
        username = this.username,
        email = this.email,
        profilePicUrl = this.profilePicUrl
    )
}

fun ChatParticipant.toChatParticipantEntity(): ChatParticipantEntity {
    return ChatParticipantEntity(
        userId = this.userId,
        username = this.username,
        email = this.email,
        profilePicUrl = this.profilePicUrl
    )
}