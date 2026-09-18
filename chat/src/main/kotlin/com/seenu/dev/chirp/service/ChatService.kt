package com.seenu.dev.chirp.service

import com.seenu.dev.chirp.domain.exception.ChatNotFoundException
import com.seenu.dev.chirp.domain.exception.ChatParticipantNotFoundException
import com.seenu.dev.chirp.domain.exception.InvalidChatSizeException
import com.seenu.dev.chirp.domain.models.Chat
import com.seenu.dev.chirp.domain.models.ChatMessage
import com.seenu.dev.chirp.domain.type.ChatId
import com.seenu.dev.chirp.domain.type.UserId
import com.seenu.dev.chirp.infra.database.entities.ChatEntity
import com.seenu.dev.chirp.infra.database.mappers.toChat
import com.seenu.dev.chirp.infra.database.repositories.ChatMessageRepository
import com.seenu.dev.chirp.infra.database.repositories.ChatParticipantRepository
import com.seenu.dev.chirp.infra.database.repositories.ChatRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatService constructor(
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val chatMessageRepository: ChatMessageRepository
) {

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
            ?: throw ChatNotFoundException(chatId)

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
                this.participants = this.participants + users
            }
        ).toChat(lastMessage)
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

        val newParticipantSize  = chat.participants.size - 1
        if (newParticipantSize == 0) {
            chatRepository.deleteById(chatId)
            return
        }

        chatRepository.save(
            chat.apply {
                this.participants = participants - participant
            }
        )
    }

    private fun lastMessageForChat(chatId: ChatId): ChatMessage? {
        return chatMessageRepository
            .findLatestMessagesByChatIds(chatIds = setOf(chatId))
            .firstOrNull()
            ?.toChatMessage()
    }
}