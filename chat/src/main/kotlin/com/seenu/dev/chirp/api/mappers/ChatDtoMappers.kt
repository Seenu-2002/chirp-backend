package com.seenu.dev.chirp.api.mappers

import com.seenu.dev.chirp.api.dto.ChatDto
import com.seenu.dev.chirp.api.dto.ChatMessageDto
import com.seenu.dev.chirp.api.dto.ChatParticipantDto
import com.seenu.dev.chirp.domain.models.Chat
import com.seenu.dev.chirp.domain.models.ChatMessage
import com.seenu.dev.chirp.domain.models.ChatParticipant

fun ChatParticipant.toChatParticipantDto(): ChatParticipantDto {
    return ChatParticipantDto(
        userId = this.userId,
        username = this.username,
        email = this.email,
        profilePictureUrl = this.profilePicUrl
    )
}

fun ChatMessage.toChatMessageDto(): ChatMessageDto {
    return ChatMessageDto(
        id = this.id,
        chatId = this.chatId,
        content = this.content,
        createdAt = this.createdAt,
        senderId = this.sender.userId
    )
}

fun Chat.toChatDto(): ChatDto {
    return ChatDto(
        id = this.id,
        participants = this.participant.map { it.toChatParticipantDto() },
        lastMessage = this.lastMessage?.toChatMessageDto(),
        creator = this.creator.toChatParticipantDto(),
        lastActivityAt = this.lastActivityAt,
    )
}