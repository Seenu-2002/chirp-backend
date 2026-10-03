package com.seenu.dev.chirp.api.dto

import java.time.Instant

data class PictureUploadResponse constructor(
    val uploadUrl: String,
    val publicUrl: String,
    val headers: Map<String, String>,
    val expiresAt: Instant
)