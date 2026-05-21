package com.seenu.dev.chirp.user.domain.exceptions

class InvalidCredentialException : RuntimeException(
    "Invalid email or password. Please try again later."
)