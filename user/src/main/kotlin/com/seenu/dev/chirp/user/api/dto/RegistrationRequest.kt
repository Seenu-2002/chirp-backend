package com.seenu.dev.chirp.user.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.seenu.dev.chirp.user.api.util.Password
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

data class RegistrationRequest constructor(
    @field:Length(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @JsonProperty("user_name")
    val userName: String,
    @field:Email(message = "Email should be valid")
    @JsonProperty("email")
    val email: String,
    @field:Password
    @JsonProperty("password")
    val password: String,
)