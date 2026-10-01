package com.seenu.dev.chirp.service

import com.seenu.dev.chirp.api.dto.ChatMessageDto
import com.seenu.dev.chirp.api.mappers.toChatMessageDto
import com.seenu.dev.chirp.api.util.requestUserId
import com.seenu.dev.chirp.domain.event.ChatParticipantsJoinedEvent
import com.seenu.dev.chirp.domain.event.ChatParticipantsLeftEvent
import com.seenu.dev.chirp.domain.exception.ChatNotFoundException
import com.seenu.dev.chirp.domain.exception.ChatParticipantNotFoundException
import com.seenu.dev.chirp.domain.exception.ForbiddenException
import com.seenu.dev.chirp.domain.exception.InvalidChatSizeException
import com.seenu.dev.chirp.domain.models.Chat
import com.seenu.dev.chirp.domain.models.ChatMessage
import com.seenu.dev.chirp.domain.type.ChatId
import com.seenu.dev.chirp.domain.type.UserId
import com.seenu.dev.chirp.infra.database.entities.ChatEntity
import com.seenu.dev.chirp.infra.database.mappers.toChat
import com.seenu.dev.chirp.infra.database.mappers.toChatMessage
import com.seenu.dev.chirp.infra.database.repositories.ChatMessageRepository
import com.seenu.dev.chirp.infra.database.repositories.ChatParticipantRepository
import com.seenu.dev.chirp.infra.database.repositories.ChatRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ChatService constructor(
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @Cacheable(
        value = ["messages"],
        key = "#chatId",
        condition = "#before == null && #pageSize <= 50",
        sync = true
    )
    fun getChatMessage(
        chatId: ChatId,
        before: Instant? = null,
        pageSize: Int
    ): List<ChatMessageDto> {
        return chatMessageRepository
            .findByChatIdBefore(
                chatId = chatId,
                before = before ?: Instant.now(),
                pageable = PageRequest.of(0, pageSize)
            )
            .content
            .asReversed()
            .map {
                it.toChatMessage().toChatMessageDto()
            }
    }

    fun getChatById(chatId: ChatId, requestUserId: UserId): Chat? {
        return chatRepository.findChatById(chatId, requestUserId)
            ?.toChat(lastMessageForChat(chatId))
    }

    fun findChatsByUser(userId: UserId): List<Chat> {
        val chatEntities = chatRepository.findAllByUserId(userId)
        val chatIds = chatEntities.mapNotNull { it.id }
        val latestMessages = chatMessageRepository
            .findLatestMessagesByChatIds(chatIds = chatIds.toSet())
            .associateBy { it.chatId }

        return chatEntities
            .map {
                it.toChat(
                    lastMessage = latestMessages[it.id]?.toChatMessage()
                )
            }
            .sortedByDescending { it.lastActivityAt }
    }

    @Transactional
    fun createChat(
        creatorId: UserId,
        otherUserIds: Set<UserId>,
    ): Chat {
        val otherParticipants = chatParticipantRepository.findByUserIdIn(userIds = otherUserIds)

        val allParticipants = (otherParticipants + creatorId)
        if (allParticipants.size < 2) {
            throw InvalidChatSizeException()
        }

        val creator = chatParticipantRepository.findByIdOrNull(creatorId)
            ?: throw ChatParticipantNotFoundException(creatorId)

        return chatRepository.save(
            ChatEntity(
                creator = creator,
                participants = setOf(creator) + otherParticipants
            )
        ).toChat(lastMessage = null)
    }

    @Transactional
    fun addParticipants(
        requestUserId: UserId,
        chatId: ChatId,
        userIds: Set<UserId>
    ): Chat {
        val chat = chatRepository.findByIdOrNull(chatId)
            ?: throw ChatNotFoundException()

        val isRequestedUserInChat = chat.participants.any {
            it.userId == requestUserId
        }

        if (!isRequestedUserInChat) {
            throw ForbiddenException()
        }

        val users = userIds.map {
            chatParticipantRepository.findByIdOrNull(it)
                ?: throw ChatParticipantNotFoundException(it)
        }

        val lastMessage = lastMessageForChat(chatId)
        val updatedChat = chatRepository.save(
            chat.apply {
                this.participants += users
            }
        ).toChat(lastMessage)

        applicationEventPublisher.publishEvent(
            ChatParticipantsJoinedEvent(
                chatId = chatId,
                userIds = userIds
            )
        )

        return updatedChat
    }

    @Transactional
    fun removeParticipantFromChat(
        chatId: ChatId,
        userId: UserId
    ) {
        val chat = chatRepository.findByIdOrNull(chatId)
            ?: throw ChatNotFoundException()

        val participant = chatParticipantRepository.findByIdOrNull(userId)
            ?: throw ChatParticipantNotFoundException(userId)

        val newParticipantSize = chat.participants.size - 1
        if (newParticipantSize == 0) {
            chatRepository.deleteById(chatId)
            return
        }

        chatRepository.save(
            chat.apply {
                this.participants -= participant
            }
        )

        applicationEventPublisher.publishEvent(
            ChatParticipantsLeftEvent(
                chatId = chatId,
                userId = userId
            )
        )
    }

    private fun lastMessageForChat(chatId: ChatId): ChatMessage? {
        return chatMessageRepository
            .findLatestMessagesByChatIds(chatIds = setOf(chatId))
            .firstOrNull()
            ?.toChatMessage()
    }
}