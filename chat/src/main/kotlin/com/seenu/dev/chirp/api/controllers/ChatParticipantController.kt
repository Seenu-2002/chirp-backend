package com.seenu.dev.chirp.api.controllers

import com.seenu.dev.chirp.api.dto.ChatParticipantDto
import com.seenu.dev.chirp.api.mappers.toChatParticipantDto
import com.seenu.dev.chirp.api.util.requestUserId
import com.seenu.dev.chirp.service.ChatParticipantService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api/chat/participants")
class ChatParticipantController constructor(
    private val chatParticipantService: ChatParticipantService
) {

    @GetMapping
    fun getChatParticipantByUsernameOrEmail(
        @RequestParam(required = false) query: String?
    ): ChatParticipantDto {
        val participant = if (query == null) {
            chatParticipantService.findChatParticipantById(requestUserId)
        } else {
            chatParticipantService.findChatParticipantByEmailOrUsername(query = query)
        }

        return participant?.toChatParticipantDto()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

}