package com.seenu.dev.chirp.user.api.config

import java.time.temporal.ChronoUnit


annotation class IpRateLimit constructor(
    val requests: Int = 60,
    val duration: Long = 1L,
    val unit: ChronoUnit = ChronoUnit.MINUTES
)
