package com.seenu.dev.chirp.user.api.util

import com.seenu.dev.chirp.user.domain.exceptions.UnauthorizedException
import com.seenu.dev.chirp.user.domain.model.UserId
import org.springframework.security.core.context.SecurityContextHolder

val requestUserId: UserId
    get() = SecurityContextHolder.getContext().authentication?.principal as? UserId
        ?: throw UnauthorizedException()