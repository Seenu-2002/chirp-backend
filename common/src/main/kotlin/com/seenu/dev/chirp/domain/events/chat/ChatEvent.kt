package com.seenu.dev.chirp.domain.events.chat

import com.seenu.dev.chirp.domain.events.ChirpEvent
import com.seenu.dev.chirp.domain.type.ChatId
import com.seenu.dev.chirp.domain.type.UserId
import java.time.Instant
import java.util.UUID
import kotlin.coroutines.AbstractCoroutineContextKey

sealed class ChatEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val exchange: String = ChatEventConstants.CHAT_EXCHANGE,
    override val occurredAt: Instant = Instant.now(),
) : ChirpEvent {

    data class NewMessage constructor(
        val senderId: UserId,
        val senderUsername: String,
        val recipientIds: Set<UserId>,
        val chatId: ChatId,
        val message: String,
        override val eventKey: String = ChatEventConstants.CHAT_NEW_MESSAGE,
    ) : ChatEvent()

}