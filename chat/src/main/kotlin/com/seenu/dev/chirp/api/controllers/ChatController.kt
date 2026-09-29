package com.seenu.dev.chirp.api.controllers

import com.seenu.dev.chirp.api.dto.AddParticipantToChatDto
import com.seenu.dev.chirp.api.dto.ChatDto
import com.seenu.dev.chirp.api.dto.ChatMessageDto
import com.seenu.dev.chirp.api.dto.CreateChatRequest
import com.seenu.dev.chirp.api.mappers.toChatDto
import com.seenu.dev.chirp.api.util.requestUserId
import com.seenu.dev.chirp.domain.type.ChatId
import com.seenu.dev.chirp.service.ChatService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController("/api/chat")
class ChatController constructor(
    private val chatService: ChatService
) {

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }

    @GetMapping("/{chatId}/messages")
    fun getMessagesForChat(
        @PathVariable("chatId") chatId: ChatId,
        @PathVariable("before", required = false) before: Instant? = null,
        @PathVariable("pageSize", required = false) pageSize: Int = DEFAULT_PAGE_SIZE
    ): List<ChatMessageDto> {
        return chatService.getChatMessage(
            chatId = chatId,
            before = before,
            pageSize = pageSize
        )
    }

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
        @PathVariable chatId: ChatId,
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
        @PathVariable chatId: ChatId
    ) {
        chatService.removeParticipantFromChat(
            userId = requestUserId,
            chatId = chatId
        )
    }

}