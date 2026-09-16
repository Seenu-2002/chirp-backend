package com.seenu.dev.chirp.infra.database.entities

import com.seenu.dev.chirp.domain.type.UserId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(
    name = "chat_participant",
    schema = "chat_service",
    indexes = [
        Index(name = "idx_chat_participant_username", columnList = "username"),
        Index(name = "idx_chat_participant_email", columnList = "email"),
    ]
)
class ChatParticipantEntity constructor(
    @Id
    var userId: UserId,
    @Column(nullable = false, unique = true)
    var username: String,
    @Column(nullable = false, unique = true)
    var email: String,
    @Column(nullable = true)
    var profilePicUrl: String? = null,
    @CreationTimestamp
    var createdAt: Instant = Instant.now()
)