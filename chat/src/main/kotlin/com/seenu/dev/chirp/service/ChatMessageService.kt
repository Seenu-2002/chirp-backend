package com.seenu.dev.chirp.service

import com.seenu.dev.chirp.api.dto.ChatMessageDto
import com.seenu.dev.chirp.api.mappers.toChatMessageDto
import com.seenu.dev.chirp.domain.event.MessageDeletedEvent
import com.seenu.dev.chirp.domain.events.chat.ChatEvent
import com.seenu.dev.chirp.domain.exception.ChatNotFoundException
import com.seenu.dev.chirp.domain.exception.ChatParticipantNotFoundException
import com.seenu.dev.chirp.domain.exception.ForbiddenException
import com.seenu.dev.chirp.domain.exception.MessageNotFoundException
import com.seenu.dev.chirp.domain.models.ChatMessage
import com.seenu.dev.chirp.domain.type.ChatId
import com.seenu.dev.chirp.domain.type.ChatMessageId
import com.seenu.dev.chirp.domain.type.UserId
import com.seenu.dev.chirp.infra.database.entities.ChatMessageEntity
import com.seenu.dev.chirp.infra.database.mappers.toChatMessage
import com.seenu.dev.chirp.infra.database.repositories.ChatMessageRepository
import com.seenu.dev.chirp.infra.database.repositories.ChatParticipantRepository
import com.seenu.dev.chirp.infra.database.repositories.ChatRepository
import com.seenu.dev.chirp.infra.message_queue.EventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event
import java.time.Instant

@Service
class ChatMessageService constructor(
    private val chatRepository: ChatRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val eventPublisher: EventPublisher
) {

    @Transactional
    fun sendMessage(
        chatId: ChatId,
        senderId: UserId,
        content: String,
        messageId: ChatMessageId? = null
    ): ChatMessage {
        val chat = chatRepository.findChatById(chatId, userId = senderId)
            ?: throw ChatNotFoundException()

        val sender = chatParticipantRepository.findByIdOrNull(senderId)
            ?: throw ChatParticipantNotFoundException(senderId)

        val savedMessage = chatMessageRepository.saveAndFlush(
            ChatMessageEntity(
                id = messageId,
                content = content.trim(),
                chatId = chatId,
                chat = chat,
                sender = sender
            )
        )

        eventPublisher.publish(
            event = ChatEvent.NewMessage(
                senderId = sender.userId,
                senderUsername = sender.username,
                recipientIds = chat.participants.map { it.userId }.toSet(),
                chatId = chatId,
                message = savedMessage.content.trim()
            )
        )

        return savedMessage.toChatMessage()
    }

    @Transactional
    fun deleteMessage(
        messageId: ChatMessageId,
        requestUserId: UserId
    ) {
        val message = chatMessageRepository.findByIdOrNull(messageId)
            ?: throw MessageNotFoundException(messageId)

        if (message.sender.userId != requestUserId) {
            throw ForbiddenException()
        }

        chatMessageRepository.delete(message)

        applicationEventPublisher.publishEvent(
            MessageDeletedEvent(
                chatId = message.chatId,
                messageId = messageId
            )
        )
    }

}