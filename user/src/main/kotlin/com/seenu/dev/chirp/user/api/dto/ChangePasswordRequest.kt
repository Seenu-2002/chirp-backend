package com.seenu.dev.chirp.user.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.seenu.dev.chirp.user.api.util.Password
import jakarta.validation.constraints.NotBlank

data class ChangePasswordRequest constructor(
    @field:NotBlank
    @JsonProperty("old_password")
    val oldPassword: String,
    @field:Password
    @JsonProperty("new_password")
    val newPassword: String
)
