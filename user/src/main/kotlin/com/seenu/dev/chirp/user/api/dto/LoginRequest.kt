package com.seenu.dev.chirp.user.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

data class LoginRequest constructor(
    @JsonProperty(value = "email")
    val email: String,
    @JsonProperty(value = "password")
    val password: String
)