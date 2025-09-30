package com.seenu.dev.chirp.user.service

import com.seenu.dev.chirp.user.domain.exceptions.UserAlreadyExistException
import com.seenu.dev.chirp.user.domain.model.User
import com.seenu.dev.chirp.user.infra.database.entity.UserEntity
import com.seenu.dev.chirp.user.infra.database.repository.UserRepository
import com.seenu.dev.chirp.user.infra.mapper.toDomain
import com.seenu.dev.chirp.user.infra.security.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AuthService constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
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

}