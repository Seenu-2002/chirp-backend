package com.seenu.dev.chirp.api.util

import com.seenu.dev.chirp.domain.exception.UnauthorizedException
import com.seenu.dev.chirp.domain.type.UserId
import org.springframework.security.core.context.SecurityContextHolder

val requestUserId: UserId
    get() = SecurityContextHolder.getContext().authentication?.principal as? UserId
        ?: throw UnauthorizedException()