package com.seenu.dev.chirp.api.exception_handling

import com.seenu.dev.chirp.domain.exception.ChatNotFoundException
import com.seenu.dev.chirp.domain.exception.ChatParticipantNotFoundException
import com.seenu.dev.chirp.domain.exception.InvalidChatSizeException
import com.seenu.dev.chirp.domain.exception.MessageNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ChatExceptionHandler {

    @ExceptionHandler(
        ChatNotFoundException::class,
        MessageNotFoundException::class,
        ChatParticipantNotFoundException::class
    )
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun onNotFoundException(e: Exception) = mapOf(
        "code" to "NOT_FOUND",
        "message" to e.message
    )

    @ExceptionHandler(InvalidChatSizeException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun onNotFoundException(e: InvalidChatSizeException) = mapOf(
            "code" to "INVALID_CHAT_SIZE",
            "message" to e.message
        )

}