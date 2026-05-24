package com.seenu.dev.chirp.user.service

import com.seenu.dev.chirp.domain.events.user.UserEvent
import com.seenu.dev.chirp.user.domain.exceptions.EmailNotVerifiedException
import com.seenu.dev.chirp.user.domain.exceptions.InvalidCredentialException
import com.seenu.dev.chirp.user.domain.exceptions.InvalidTokenException
import com.seenu.dev.chirp.user.domain.exceptions.UserAlreadyExistException
import com.seenu.dev.chirp.user.domain.exceptions.UserNotFoundException
import com.seenu.dev.chirp.user.domain.model.AuthenticatedUser
import com.seenu.dev.chirp.user.domain.model.User
import com.seenu.dev.chirp.domain.type.UserId
import com.seenu.dev.chirp.infra.message_queue.EventPublisher
import com.seenu.dev.chirp.user.infra.database.entity.RefreshTokenEntity
import com.seenu.dev.chirp.user.infra.database.entity.UserEntity
import com.seenu.dev.chirp.user.infra.database.repository.RefreshTokenRepository
import com.seenu.dev.chirp.user.infra.database.repository.UserRepository
import com.seenu.dev.chirp.user.infra.mapper.toDomain
import com.seenu.dev.chirp.user.infra.security.PasswordEncoder
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Component
class AuthService constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val emailVerificationService: EmailVerificationService,
    private val eventPublisher: EventPublisher
) {

    @Transactional
    fun register(userName: String, email: String, password: String): User {
        val trimmedEmail = email.trim()
        val existingUser = userRepository.findByEmailOrUserName(
            userName = userName,
            email = trimmedEmail,
        )

        if (existingUser != null) {
            throw UserAlreadyExistException()
        }

        val savedUser = userRepository.saveAndFlush(
            UserEntity(
                userName = userName,
                email = trimmedEmail,
                hashedPassword = passwordEncoder.encode(password),
            )
        ).toDomain()

        val token = emailVerificationService.createVerificationToken(trimmedEmail)

        eventPublisher.publish(
            event = UserEvent.Created(
                email = savedUser.email,
                userId = savedUser.id,
                username = savedUser.userName,
                verificationToken = token.token
            )
        )

        return savedUser
    }

    fun login(
        email: String,
        password: String,
    ): AuthenticatedUser {
        val user = userRepository.findByEmail(
            email = email.trim(),
        ) ?: throw InvalidCredentialException()

        if (!passwordEncoder.matches(password, user.hashedPassword)) {
            throw InvalidCredentialException()
        }

        if (!user.hasEmailVerified) {
            throw EmailNotVerifiedException()
        }

        return user.id?.let { userId ->
            val accessToken = jwtService.generateAccessToken(userId)
            val refreshToken = jwtService.generateRefreshToken(userId)
            storeRefreshToken(userId, refreshToken)

            AuthenticatedUser(
                user = user.toDomain(),
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        } ?: throw UserNotFoundException()
    }

    @Transactional
    fun refresh(refreshToken: String): AuthenticatedUser {
        if (!jwtService.validateRefreshToken(token = refreshToken)) {
            throw InvalidTokenException(
                message = "Invalid refresh token",
            )
        }

        val userId = jwtService.getUserIdFromToken(refreshToken)
            ?: throw UserNotFoundException()
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException()

        val hashed = hashToken(refreshToken)

        return user.id?.let { userId ->
            refreshTokenRepository.findByUserIdAndHashedToken(
                userId = userId, hashedToken = hashed
            ) ?: throw InvalidTokenException("Invalid refresh token")

            refreshTokenRepository.deleteByUserIdAndHashedToken(
                userId = userId,
                hashedToken = hashed
            )

            val newAccessToken = jwtService.generateAccessToken(userId = userId)
            val newRefreshToken = jwtService.generateRefreshToken(userId = userId)

            storeRefreshToken(userId = userId, token = newRefreshToken)

            AuthenticatedUser(
                user = user.toDomain(),
                accessToken = newAccessToken,
                refreshToken = newRefreshToken
            )
        } ?: throw UserNotFoundException()
    }

    @Transactional
    fun logout(refreshToken: String) {
        val userId = jwtService.getUserIdFromToken(refreshToken) ?: throw InvalidTokenException(
            "Invalid refresh token"
        )
        val hashed = hashToken(refreshToken)
        refreshTokenRepository.deleteByUserIdAndHashedToken(
            userId = userId,
            hashedToken = hashed
        )
    }

    private fun storeRefreshToken(userId: UserId, token: String) {
        val hashedToken = hashToken(token)
        val expiryInMs = jwtService.refreshTokenValidityInMs
        val expiresAt = Instant.now().plusMillis(expiryInMs)

        refreshTokenRepository.save(
            RefreshTokenEntity(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashedToken,
            )
        )
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }

}