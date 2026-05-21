package com.seenu.dev.chirp.user.domain.exceptions

class SamePasswordException : RuntimeException("New password cannot be the same as the old password")