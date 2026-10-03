package com.seenu.dev.chirp.api.dto.ws

import com.seenu.dev.chirp.domain.type.UserId

data class ProfilePictureUpdateDto constructor(
    val userId: UserId,
    val newUrl: String?
)