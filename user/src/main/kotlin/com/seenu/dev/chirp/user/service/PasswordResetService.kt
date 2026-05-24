package com.seenu.dev.chirp.user.service

import com.seenu.dev.chirp.user.domain.exceptions.InvalidCredentialException
import com.seenu.dev.chirp.user.domain.exceptions.InvalidTokenException
import com.seenu.dev.chirp.user.domain.exceptions.SamePasswordException
import com.seenu.dev.chirp.user.domain.exceptions.UserNotFoundException
import com.seenu.dev.chirp.domain.type.UserId
import com.seenu.dev.chirp.user.infra.database.entity.PasswordResetTokenEntity
import com.seenu.dev.chirp.user.infra.database.repository.PasswordResetTokenRepository
import com.seenu.dev.chirp.user.infra.database.repository.RefreshTokenRepository
import com.seenu.dev.chirp.user.infra.database.repository.UserRepository
import com.seenu.dev.chirp.user.infra.security.PasswordEncoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class PasswordResetService constructor(
    private val userRepository: UserRepository,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    @param:Value("\${chirp.email.reset-password.expiry-minutes}")
    private val expiryMinutes: Long
) {

    @Transactional
    fun requestPasswordReset(email: String) {
        val user = userRepository.findByEmail(email)
            ?: return

        passwordResetTokenRepository.invalidateActiveTokensForUser(user)

        val token = PasswordResetTokenEntity(
            user = user,
            expiresAt = Instant.now().plus(expiryMinutes, ChronoUnit.MINUTES),
        )

        passwordResetTokenRepository.save(token)

        // TODO: Inform notification service about reset trigger to send email
    }

    @Transactional
    fun resetPassword(token: String, newPassword: String) {
        val resetToken = passwordResetTokenRepository.findByToken(token)
            ?: throw InvalidTokenException("Invalid password reset token")

        if (resetToken.isExpired) {
            throw InvalidTokenException("Password reset token is expired")
        }

        if (resetToken.isUsed) {
            throw InvalidTokenException("Password reset token is used")
        }

        val user = resetToken.user

        if (passwordEncoder.matches(newPassword, user.hashedPassword)) {
            throw SamePasswordException()
        }

        val hashedNewPassword = passwordEncoder.encode(newPassword)
        userRepository.save(
            user.apply {
                this.hashedPassword = hashedNewPassword
            }
        )

        passwordResetTokenRepository.save(
            resetToken.apply {
                this.usedAt = Instant.now()
            }
        )

        refreshTokenRepository.deleteByUserId(user.id!!)
    }

    @Transactional
    fun changePassword(
        userId: UserId,
        oldPassword: String,
        newPassword: String
    ) {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException()

        if (!passwordEncoder.matches(oldPassword, user.hashedPassword)) {
            throw InvalidCredentialException()
        }

        if (oldPassword == newPassword) {
            throw SamePasswordException()
        }

        refreshTokenRepository.deleteByUserId(user.id!!)

        val newHashedPassword = passwordEncoder.encode(newPassword)
        userRepository.save(
            user.apply {
                this.hashedPassword = newHashedPassword
            }
        )
    }

    @Scheduled(cron = "0 0 3 * * *")
    fun cleanupExpiredTokens() {
        passwordResetTokenRepository.deleteByExpiresAtLessThan(
            now = Instant.now()
        )
    }

}