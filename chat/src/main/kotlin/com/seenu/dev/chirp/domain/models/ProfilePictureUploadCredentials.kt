package com.seenu.dev.chirp.domain.models

import java.time.Instant

data class ProfilePictureUploadCredentials constructor(
    val uploadUrl: String,
    val publicUrl: String,
    val headers: Map<String, String>,
    val expiresAt: Instant
)
