package com.seenu.dev.chirp.user.api.controller

import com.seenu.dev.chirp.user.api.config.IpRateLimit
import com.seenu.dev.chirp.user.api.dto.AuthenticatedUserDto
import com.seenu.dev.chirp.user.api.dto.ChangePasswordRequest
import com.seenu.dev.chirp.user.api.dto.EmailRequest
import com.seenu.dev.chirp.user.api.dto.LoginRequest
import com.seenu.dev.chirp.user.api.dto.RefreshRequest
import com.seenu.dev.chirp.user.api.dto.RegistrationRequest
import com.seenu.dev.chirp.user.api.dto.ResetPasswordRequest
import com.seenu.dev.chirp.user.api.dto.UserDto
import com.seenu.dev.chirp.user.api.mapper.toDto
import com.seenu.dev.chirp.user.api.util.requestUserId
import com.seenu.dev.chirp.user.domain.model.UserId
import com.seenu.dev.chirp.user.infra.rate_limiting.EmailRateLimiter
import com.seenu.dev.chirp.user.service.AuthService
import com.seenu.dev.chirp.user.service.EmailVerificationService
import com.seenu.dev.chirp.user.service.PasswordResetService
import io.lettuce.core.KillArgs.Builder.user
import jakarta.validation.Valid
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/auth")
class AuthController constructor(
    private val authService: AuthService,
    private val emailVerificationService: EmailVerificationService,
    private val passwordResetService: PasswordResetService,
    private val emailRateLimiter: EmailRateLimiter
) {

    @PostMapping("/register")
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = ChronoUnit.HOURS
    )
    fun register(@Valid @RequestBody body: RegistrationRequest): UserDto {
        return authService.register(
            userName = body.userName,
            email = body.email,
            password = body.password
        ).toDto()
    }

    @PostMapping("/login")
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = ChronoUnit.HOURS
    )
    fun login(
        @RequestBody body: LoginRequest
    ): AuthenticatedUserDto {
        return authService.login(
            email = body.email,
            password = body.password
        ).toDto()
    }

    @PostMapping("/refresh")
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = ChronoUnit.HOURS
    )
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

    @PostMapping("/resend-verification")
    fun resendVerificationEmail(@Valid @RequestBody body: EmailRequest) {
        emailRateLimiter.withRateLimit(body.email) {
            emailVerificationService.resendVerificationEmail(body.email)
        }
    }


    @GetMapping("/verify")
    fun verifyEmail(
        @RequestParam token: String
    ) {
        emailVerificationService.verifyEmail(token)
    }

    @PostMapping("forgot-password")
    fun forgotPassword(
        @Valid @RequestBody body: EmailRequest
    ) {
        passwordResetService.requestPasswordReset(body.email)
    }

    @PostMapping("/reset-password")
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = ChronoUnit.HOURS
    )
    fun resetPassword(
        @Valid @RequestBody body: ResetPasswordRequest
    ) {
        passwordResetService.resetPassword(
            token = body.token,
            newPassword = body.newPassword,
        )
    }

    @PostMapping("/change-password")
    fun changePassword(
        @Valid @RequestBody body: ChangePasswordRequest
    ) {
        passwordResetService.changePassword(
            userId = requestUserId,
            oldPassword =  body.oldPassword,
            newPassword = body.newPassword,
        )
    }

}