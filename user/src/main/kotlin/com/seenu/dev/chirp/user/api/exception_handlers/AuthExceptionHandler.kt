package com.seenu.dev.chirp.user.api.exception_handlers

import com.seenu.dev.chirp.user.domain.exceptions.EmailNotVerifiedException
import com.seenu.dev.chirp.user.domain.exceptions.InvalidCredentialException
import com.seenu.dev.chirp.user.domain.exceptions.InvalidTokenException
import com.seenu.dev.chirp.user.domain.exceptions.UserAlreadyExistException
import com.seenu.dev.chirp.user.domain.exceptions.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class AuthExceptionHandler {

    @ExceptionHandler(UserAlreadyExistException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun onUserAlreadyExists(exp: UserAlreadyExistException): Map<String, String?> {
        return mapOf(
            "code" to "USER_ALREADY_EXISTS",
            "message" to exp.message
        )
    }

    @ExceptionHandler(InvalidTokenException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun onInvalidToken(exp: InvalidTokenException): Map<String, String?> {
        return mapOf(
            "code" to "INVALID_TOKEN",
            "message" to exp.message
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun onValidException(exp: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = exp.bindingResult.allErrors.map {
            it.defaultMessage ?: "Invalid value"
        }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                mapOf(
                    "code" to "VALIDATION_ERROR",
                    "errors" to errors
                )
            )
    }

    @ExceptionHandler(UserNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun onUserNotFound(exp: UserNotFoundException): Map<String, String?> {
        return mapOf(
            "code" to "USER_NOT_FOUND",
            "message" to exp.message
        )
    }

    @ExceptionHandler(InvalidCredentialException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun onInvalidCredentials(exp: InvalidCredentialException): Map<String, String?> {
        return mapOf(
            "code" to "INVALID_CREDENTIALS",
            "message" to exp.message
        )
    }

    @ExceptionHandler(EmailNotVerifiedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun onEmailNotVerified(exp: EmailNotVerifiedException): Map<String, String?> {
        return mapOf(
            "code" to "EMAIL_NOT_VERIFIED",
            "message" to exp.message
        )
    }

}