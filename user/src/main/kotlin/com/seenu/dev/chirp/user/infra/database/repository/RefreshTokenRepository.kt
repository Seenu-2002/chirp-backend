package com.seenu.dev.chirp.user.infra.database.repository

import com.seenu.dev.chirp.user.infra.database.entity.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, Long> {

    fun findByUserIdAndHashedToken(userId: String, hashedToken: String): RefreshTokenEntity?

    fun deleteByUserIdAndHashedToken(userId: String, hashedToken: String)

    fun deleteByUserId(userId: String)

}