package com.seenu.dev.chirp.user.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email

data class EmailRequest constructor(
    @field:Email
    @JsonProperty("email")
    val email: String,
)