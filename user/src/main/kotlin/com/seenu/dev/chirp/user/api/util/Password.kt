package com.seenu.dev.chirp.user.api.util

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Pattern
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
@Pattern(
    regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
    message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character"
)
annotation class Password(
    val message: String = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character",
    val groups: Array<KClass<out Any>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
