package com.seenu.dev.chirp.user.domain.exceptions

import java.lang.RuntimeException

class UserAlreadyExistException : RuntimeException(
    "User with given email or username already exists"
)