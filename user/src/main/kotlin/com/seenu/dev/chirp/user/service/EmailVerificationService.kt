package com.seenu.dev.chirp.user.service

import com.seenu.dev.chirp.user.domain.exceptions.InvalidTokenException
import com.seenu.dev.chirp.user.domain.exceptions.UserNotFoundException
import com.seenu.dev.chirp.user.domain.model.EmailVerificationToken
import com.seenu.dev.chirp.user.infra.database.entity.EmailVerificationTokenEntity
import com.seenu.dev.chirp.user.infra.database.repository.EmailVerificationTokenRepository
import com.seenu.dev.chirp.user.infra.database.repository.UserRepository
import com.seenu.dev.chirp.user.infra.mapper.toDomain
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class EmailVerificationService constructor(
    private val emailVerificationTokenRepository: EmailVerificationTokenRepository,
    private val userRepository: UserRepository,
    @param:Value("\${chirp.email.verification.expiry-hours}") private val expiryHours: Long
) {

    @Transactional
    fun createVerificationToken(email: String): EmailVerificationToken {
        val userEntity = userRepository.findByEmail(email)
            ?: throw UserNotFoundException()

        emailVerificationTokenRepository.invalidateActiveTokensForUser(userEntity)
        val now = Instant.now()
        val token = EmailVerificationTokenEntity(
            expiresAt = now.plus(expiryHours, ChronoUnit.HOURS),
            user = userEntity,
        )

        return emailVerificationTokenRepository.save(token).toDomain()
    }

    fun verifyEmail(token: String) {
        val verificationToken = emailVerificationTokenRepository.findByToken(token)
            ?: throw InvalidTokenException("Email verification token is invalid")

        if (verificationToken.isUsed) {
            throw InvalidTokenException("Email verification token is already used")
        }

        if (verificationToken.isExpired) {
            throw InvalidTokenException("Email verification token is expired")
        }

        emailVerificationTokenRepository.save(
            verificationToken.apply {
                this.usedAt = Instant.now()
            }
        )

        userRepository.save(
            verificationToken.user.apply {
                this.hasEmailVerified = true
            }
        )
    }

    @Scheduled(cron = "0 0 3 * * *")
    fun cleanupExpiredTokens() {
        emailVerificationTokenRepository.deleteByExpiresAtLessThan(
            Instant.now()
        )
    }

}