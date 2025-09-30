package com.seenu.dev.chirp.user.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

data class RegistrationRequest constructor(
    @field:Length(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @JsonProperty("userName")
    val userName: String,
    @field:Email(message = "Email should be valid")
    @JsonProperty("email")
    val email: String,
    @field:Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
        message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    @JsonProperty("password")
    val password: String,
)