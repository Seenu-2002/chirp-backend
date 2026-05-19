package com.seenu.dev.chirp.user.service

import com.seenu.dev.chirp.user.domain.exceptions.InvalidCredentialException
import com.seenu.dev.chirp.user.domain.exceptions.UserAlreadyExistException
import com.seenu.dev.chirp.user.domain.exceptions.UserNotFoundException
import com.seenu.dev.chirp.user.domain.model.AuthenticatedUser
import com.seenu.dev.chirp.user.domain.model.User
import com.seenu.dev.chirp.user.domain.model.UserId
import com.seenu.dev.chirp.user.infra.database.entity.RefreshTokenEntity
import com.seenu.dev.chirp.user.infra.database.entity.UserEntity
import com.seenu.dev.chirp.user.infra.database.repository.RefreshTokenRepository
import com.seenu.dev.chirp.user.infra.database.repository.UserRepository
import com.seenu.dev.chirp.user.infra.mapper.toDomain
import com.seenu.dev.chirp.user.infra.security.PasswordEncoder
import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Component
class AuthService constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    fun register(userName: String, email: String, password: String): User {
        val existingUser = userRepository.findByEmailOrUserName(
            userName = userName,
            email = email,
        )

        if (existingUser != null) {
            throw UserAlreadyExistException()
        }

        return userRepository.save(
            UserEntity(
                userName = userName,
                email = email,
                hashedPassword = passwordEncoder.encode(password),
            )
        ).toDomain()
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
            // TODO: Email verification
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