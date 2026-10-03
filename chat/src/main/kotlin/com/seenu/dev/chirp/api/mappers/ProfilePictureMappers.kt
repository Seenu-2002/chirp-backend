package com.seenu.dev.chirp.api.mappers

import com.seenu.dev.chirp.api.dto.PictureUploadResponse
import com.seenu.dev.chirp.domain.models.ProfilePictureUploadCredentials

fun ProfilePictureUploadCredentials.toResponse(): PictureUploadResponse {
    return PictureUploadResponse(
        uploadUrl = this.uploadUrl,
        publicUrl = this.publicUrl,
        headers = this.headers,
        expiresAt = this.expiresAt,
    )
}