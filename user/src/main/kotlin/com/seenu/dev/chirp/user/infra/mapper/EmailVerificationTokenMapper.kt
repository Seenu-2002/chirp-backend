package com.seenu.dev.chirp.user.infra.mapper

import com.seenu.dev.chirp.user.domain.model.EmailVerificationToken
import com.seenu.dev.chirp.user.infra.database.entity.EmailVerificationTokenEntity

fun EmailVerificationTokenEntity.toDomain(): EmailVerificationToken {
    return EmailVerificationToken(
        id = this.id,
        token = this.token,
        user = this.user.toDomain()
    )
}