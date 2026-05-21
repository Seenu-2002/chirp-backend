plugins {
    id("chirp.spring-boot-app")
}

group = "com.seenu.dev.chirp"
version = "0.0.1-SNAPSHOT"
description = "Chirp Backend"

dependencies {
    implementation(projects.chat)
    implementation(projects.user)
    implementation(projects.notification)
    implementation(projects.common)

    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.security)
    runtimeOnly(libs.postgresql)
}