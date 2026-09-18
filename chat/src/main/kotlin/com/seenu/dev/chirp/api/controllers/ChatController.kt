package com.seenu.dev.chirp.api.controllers

import com.seenu.dev.chirp.api.dto.ChatDto
import com.seenu.dev.chirp.api.dto.CreateChatRequest
import com.seenu.dev.chirp.api.mappers.toChatDto
import com.seenu.dev.chirp.api.util.requestUserId
import com.seenu.dev.chirp.service.ChatService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController("/api/chat")
class ChatController constructor(
    private val chatService: ChatService
) {

    @PostMapping
    fun createChat(
        @Valid @RequestBody body: CreateChatRequest
    ): ChatDto {
        return chatService.createChat(
            creatorId = requestUserId,
            otherUserIds = body.otherUserIds.toSet(),
        ).toChatDto()
    }

    @PostMapping("/{chatId}/add")
    fun addChatParticipants(
        @PathVariable chatId: Int,
        @Valid @RequestBody body: AddParticipantToChatDto
    ): ChatDto {
        return chatService.addParticipants(
            requestUserId = requestUserId,
            chatId = chatId,
            userIds = body.userIds.toSet()
        ).toChatDto()
    }

    @DeleteMapping("/{chatId}/leave")
    fun leaveChat(
        @PathVariable chatId: Int
    ) {
        chatService.removeParticipant(
            requestUserId = requestUserId,
            chatId = chatId
        )
    }

}