package com.seenu.dev.chirp

import com.seenu.dev.chirp.user.infra.database.entity.UserEntity
import com.seenu.dev.chirp.user.infra.database.repository.UserRepository
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component

@SpringBootApplication
class ChirpBackendApplication

fun main(args: Array<String>) {
    runApplication<ChirpBackendApplication>(*args)
}

@Component
class Demo(
    private val repository: UserRepository
) {

    @PostConstruct
    fun init() {
        repository.save(
            UserEntity(
                email = "seenivasan.t@gmail.com",
                hashedPassword = "sdkjgbsgnpsbanfbfe",
                userName = "seenu",
                hasVerified = true
            )
        )
    }

}