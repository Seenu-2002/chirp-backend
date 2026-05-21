package com.seenu.dev.chirp.user.infra.mapper

import com.seenu.dev.chirp.user.domain.model.User
import com.seenu.dev.chirp.user.infra.database.entity.UserEntity

fun UserEntity.toDomain(): User {
    return User(
        id = this.id!!,
        userName = this.userName,
        email = this.email,
        hasEmailVerified = this.hasEmailVerified
    )
}