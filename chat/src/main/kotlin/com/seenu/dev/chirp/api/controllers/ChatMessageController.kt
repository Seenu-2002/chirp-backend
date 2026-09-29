package com.seenu.dev.chirp.api.controllers

import com.seenu.dev.chirp.api.util.requestUserId
import com.seenu.dev.chirp.domain.type.ChatMessageId
import com.seenu.dev.chirp.service.ChatMessageService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/messages")
class ChatMessageController constructor(
    private val chatMessageService: ChatMessageService
) {

    @DeleteMapping("/{messageId}")
    fun deleteMessage(@PathVariable("messageId") messageId: ChatMessageId) {
        chatMessageService.deleteMessage(messageId, requestUserId)
    }

 }