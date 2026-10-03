package com.seenu.dev.chirp.domain.event

import com.seenu.dev.chirp.domain.type.UserId

data class ProfilePictureUpdatedEvent constructor(
    val userId: UserId,
    val newUrl: String? = null
)