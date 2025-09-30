package com.seenu.dev.chirp.user.api.controller

import com.seenu.dev.chirp.user.api.dto.RegistrationRequest
import com.seenu.dev.chirp.user.api.dto.UserDto
import com.seenu.dev.chirp.user.api.mapper.toDto
import com.seenu.dev.chirp.user.service.AuthService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController constructor(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody body: RegistrationRequest): UserDto {
        return authService.register(
            userName = body.userName,
            email = body.email,
            password = body.password
        ).toDto()
    }

}