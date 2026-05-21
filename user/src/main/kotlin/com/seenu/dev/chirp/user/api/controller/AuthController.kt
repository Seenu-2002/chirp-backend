package com.seenu.dev.chirp.user.api.controller

import com.seenu.dev.chirp.user.api.dto.AuthenticatedUserDto
import com.seenu.dev.chirp.user.api.dto.LoginRequest
import com.seenu.dev.chirp.user.api.dto.RefreshRequest
import com.seenu.dev.chirp.user.api.dto.RegistrationRequest
import com.seenu.dev.chirp.user.api.dto.UserDto
import com.seenu.dev.chirp.user.api.mapper.toDto
import com.seenu.dev.chirp.user.service.AuthService
import com.seenu.dev.chirp.user.service.EmailVerificationService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController constructor(
    private val authService: AuthService,
    private val emailVerificationService: EmailVerificationService,
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody body: RegistrationRequest): UserDto {
        return authService.register(
            userName = body.userName,
            email = body.email,
            password = body.password
        ).toDto()
    }

    @PostMapping("/login")
    fun login(
        @RequestBody body: LoginRequest
    ): AuthenticatedUserDto {
        return authService.login(
            email = body.email,
            password = body.password
        ).toDto()
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody body: RefreshRequest
    ): AuthenticatedUserDto {
        return authService
            .refresh(body.refreshToken)
            .toDto()
    }

    @PostMapping("/logout")
    fun logout(
        @RequestBody body: RefreshRequest // Reusing @RefreshRequest as it has the same structure
    ) {
        authService.logout(body.refreshToken)
    }

    @GetMapping("/verify")
    fun verifyEmail(
        @RequestParam token: String
    ) {
       emailVerificationService.verifyEmail(token)
    }

}