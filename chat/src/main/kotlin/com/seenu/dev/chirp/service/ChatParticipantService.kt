package com.seenu.dev.chirp.service

import com.seenu.dev.chirp.domain.models.ChatParticipant
import com.seenu.dev.chirp.domain.type.UserId
import com.seenu.dev.chirp.infra.database.mappers.toChatParticipant
import com.seenu.dev.chirp.infra.database.mappers.toChatParticipantEntity
import com.seenu.dev.chirp.infra.database.repositories.ChatParticipantRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ChatParticipantService constructor(
    private val chatParticipantRepository: ChatParticipantRepository
) {

    fun createChatParticipant(
        participant: ChatParticipant
    ) {
        chatParticipantRepository.save(
            participant.toChatParticipantEntity()
        )
    }

    fun findChatParticipantById(
        participantId: UserId
    ): ChatParticipant? {
        val participantEntity = chatParticipantRepository.findByIdOrNull(participantId)
        return participantEntity?.toChatParticipant()
    }

    fun findChatParticipantByEmailOrUsername(
        query: String
    ): ChatParticipant? {
        val normalizedQuery = query.lowercase().trim()

        return chatParticipantRepository.findByEmailOrUsername(
            query = normalizedQuery
        )?.toChatParticipant()
    }

}