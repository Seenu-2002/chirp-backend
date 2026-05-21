package com.seenu.dev.chirp.user.api.mapper

import com.seenu.dev.chirp.user.api.dto.AuthenticatedUserDto
import com.seenu.dev.chirp.user.api.dto.UserDto
import com.seenu.dev.chirp.user.domain.model.AuthenticatedUser
import com.seenu.dev.chirp.user.domain.model.User

fun AuthenticatedUser.toDto(): AuthenticatedUserDto {
    return AuthenticatedUserDto(
        user = this.user.toDto(),
        accessToken = this.accessToken,
        refreshToken = this.refreshToken
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id = this.id,
        userName = this.userName,
        email = this.email,
        hasEmailVerified = this.hasEmailVerified
    )
}