package com.seenu.dev.chirp.api.dto

import jakarta.validation.constraints.NotBlank

data class ConfirmProfilePictureRequest constructor(
    @field:NotBlank
    val publicUrl: String
)