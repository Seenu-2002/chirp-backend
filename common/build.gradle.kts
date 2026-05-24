plugins {
    id("java-library")
    id("chirp.kotlin-common")
}

dependencies {
    api(libs.kotlin.reflect)
    api(libs.jackson.module.kotlin)
    api(libs.jackson.datatype.jsr310)

    implementation(libs.spring.boot.starter.amqp)
}