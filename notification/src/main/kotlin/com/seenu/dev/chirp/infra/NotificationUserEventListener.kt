package com.seenu.dev.chirp.infra

import com.seenu.dev.chirp.domain.events.user.UserEvent
import com.seenu.dev.chirp.infra.message_queue.MessageQueues
import com.seenu.dev.chirp.service.EmailService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Component
class NotificationUserEventListener constructor(
    private val emailService: EmailService,
) {

    @RabbitListener(queues = [MessageQueues.NOTIFICATION_USER_EVENTS])
    @Transactional
    fun handleUserEvent(event: UserEvent) {
        when (event) {
            is UserEvent.Created -> {
                emailService.sendVerificationEmail(
                    username = event.username,
                    email = event.email,
                    userId = event.userId,
                    token = event.verificationToken
                )
            }

            is UserEvent.RequestResendVerification -> {
                emailService.sendVerificationEmail(
                    username = event.username,
                    email = event.email,
                    userId = event.userId,
                    token = event.verificationToken
                )
            }

            is UserEvent.RequestResetPassword -> {
                emailService.sendPasswordResetEmail(
                    username = event.username,
                    email = event.email,
                    userId = event.userId,
                    token = event.passwordResetToken,
                    expiredIn = Duration.ofMinutes(event.expiryInMinutes)
                )
            }

            is UserEvent.Verified -> {
                println("User verified!")
            }
        }
    }

}