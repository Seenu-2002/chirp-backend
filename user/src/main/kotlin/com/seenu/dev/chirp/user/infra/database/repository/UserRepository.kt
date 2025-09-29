package com.seenu.dev.chirp.user.infra.database.repository

import com.seenu.dev.chirp.user.domain.model.UserId
import com.seenu.dev.chirp.user.infra.database.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, UserId> {
    fun findByEmail(email: String): UserEntity?
    fun findByEmailOrUserName(email: String, userName: String): UserEntity?
}