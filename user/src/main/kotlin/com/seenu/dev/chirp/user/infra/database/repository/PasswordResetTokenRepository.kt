package com.seenu.dev.chirp.user.infra.database.repository

import com.seenu.dev.chirp.user.infra.database.entity.EmailVerificationTokenEntity
import com.seenu.dev.chirp.user.infra.database.entity.PasswordResetTokenEntity
import com.seenu.dev.chirp.user.infra.database.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface PasswordResetTokenRepository : JpaRepository<PasswordResetTokenEntity, Long> {

    fun findByToken(token: String): PasswordResetTokenEntity?

    fun deleteByExpiresAtLessThan(now: Instant)

    @Modifying
    @Query("""
        UPDATE PasswordResetTokenEntity p
        SET p.usedAt = CURRENT_TIMESTAMP
        WHERE p.user = :user AND p.usedAt IS NULL AND p.expiresAt > CURRENT_TIMESTAMP
    """)
    fun invalidateActiveTokensForUser(user: UserEntity)

}