package com.seenu.dev.chirp.user.infra.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordEncoder {

    private val encoder = BCryptPasswordEncoder()

    fun encode(rawPassword: String): String {
        return encoder.encode(rawPassword)!!
    }

    fun matches(rawPassword: String, hashedPassword: String): Boolean {
        return encoder.matches(rawPassword, hashedPassword)
    }

}