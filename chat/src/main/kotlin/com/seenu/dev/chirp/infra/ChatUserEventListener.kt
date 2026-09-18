package com.seenu.dev.chirp.infra

import com.seenu.dev.chirp.domain.events.user.UserEvent
import com.seenu.dev.chirp.domain.models.ChatParticipant
import com.seenu.dev.chirp.infra.message_queue.MessageQueues
import com.seenu.dev.chirp.service.ChatParticipantService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class ChatUserEventListener constructor(
    private val chatParticipantService: ChatParticipantService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = [MessageQueues.CHAT_USER_EVENTS])
    fun handleUserEvent(event: UserEvent) {
        when (event) {
            is UserEvent.Verified -> {
                logger.info("User verified event received for userId: ${event.userId}, creating Chat Participant...")

                chatParticipantService.createChatParticipant(
                    ChatParticipant(
                        userId = event.userId,
                        username = event.username,
                        email = event.email,
                        profilePicUrl = null
                    )
                )
            }

            else -> {}
        }
    }

}