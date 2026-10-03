package com.seenu.dev.chirp.service

import com.seenu.dev.chirp.domain.event.ProfilePictureUpdatedEvent
import com.seenu.dev.chirp.domain.exception.ChatParticipantNotFoundException
import com.seenu.dev.chirp.domain.models.ProfilePictureUploadCredentials
import com.seenu.dev.chirp.domain.type.UserId
import com.seenu.dev.chirp.infra.database.repositories.ChatParticipantRepository
import com.seenu.dev.chirp.infra.storage.SupabaseStorageService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfilePictureService constructor(
    private val supabaseStorageService: SupabaseStorageService,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    private val logger = LoggerFactory.getLogger(ProfilePictureService::class.java)

    fun generateUploadCredentials(
        userId: UserId,
        mimeType: String,
    ): ProfilePictureUploadCredentials {
        return supabaseStorageService.generateSignedUploadUrl(
            userId = userId,
            mimeType = mimeType,
        )
    }

    @Transactional
    fun deleteProfilePicture(userId: UserId) {
        val participant = chatParticipantRepository.findByIdOrNull(userId)
            ?: throw ChatParticipantNotFoundException(userId)

        participant.profilePicUrl?.let { url ->
            chatParticipantRepository.save(
                participant.apply { profilePicUrl = url }
            )

            supabaseStorageService.deleteFile(url)

            applicationEventPublisher.publishEvent(
                ProfilePictureUpdatedEvent(
                    userId = userId,
                    newUrl = url
                )
            )

        }
    }

    fun confirmProfilePictureUpload(
        userId: UserId,
        publicUrl: String
    ) {
        val participant = chatParticipantRepository.findByIdOrNull(userId)
            ?: throw ChatParticipantNotFoundException(userId)

        val oldUrl = participant.profilePicUrl

        chatParticipantRepository.save(
            participant.apply { profilePicUrl = publicUrl }
        )

        try {
            if (oldUrl != null) {
                supabaseStorageService.deleteFile(oldUrl)
            }
        } catch (exp: Exception) {
            logger.warn("Deleting old profile picture upload failed for user $userId", exp)
        }

        applicationEventPublisher.publishEvent(
            ProfilePictureUpdatedEvent(
                userId = userId,
                newUrl = publicUrl
            )
        )
    }

}