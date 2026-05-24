package com.seenu.dev.chirp.user.domain.exceptions

class RateLimitException constructor(
    val resetInSeconds: Long
) : RuntimeException(
    "Rate limit exceeded. Please try again in $resetInSeconds seconds.",
)