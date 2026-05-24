package com.seenu.dev.chirp.infra

import com.seenu.dev.chirp.domain.events.user.UserEvent
import com.seenu.dev.chirp.infra.message_queue.MessageQueues
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class NotificationUserEventListener {

    @RabbitListener(queues = [MessageQueues.NOTIFICATION_USER_EVENTS])
    @Transactional
    fun handleUserEvent(event: UserEvent) {
        when (event) {
            is UserEvent.Created -> {
                println("User created!")
            }

            is UserEvent.RequestResendVerification -> {
                println("User resend verification!")
            }

            is UserEvent.RequestResetPassword -> {
                println("User reset password!")
            }

            is UserEvent.Verified -> {
                println("User verified!")
            }
        }
    }

}