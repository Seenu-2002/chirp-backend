package com.seenu.dev.chirp.user.api.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RefreshRequest constructor(
    @JsonProperty("refresh_token")
    val refreshToken: String,
)
