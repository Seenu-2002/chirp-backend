@file:Suppress("DEPRECATION")

package com.seenu.dev.infra.caching

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.seenu.dev.chirp.domain.events.ChirpEvent
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import java.time.Duration

@Configuration
@EnableCaching
class RedisConfig {

    @Bean
    fun cacheManager(
        connectionFactory: LettuceConnectionFactory
    ): RedisCacheManager {
        val objectMapper = ObjectMapper().apply {
            registerModule(KotlinModule.Builder().build())
            registerModule(JavaTimeModule())
            findAndRegisterModules()

            val polymorphicValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(ChirpEvent::class.java)
                .allowIfSubType("java.util.")
                .allowIfSubType("kotlin.collections.")
                .allowIfBaseType("com.seenu.dev.chirp.")
                .build()

            activateDefaultTyping(
                polymorphicValidator,
                ObjectMapper.DefaultTyping.NON_FINAL
            )
        }

        val cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(1L))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    GenericJackson2JsonRedisSerializer(objectMapper)
                )
            )

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(cacheConfig)
            .withCacheConfiguration(
                "messages",
                cacheConfig.entryTtl(Duration.ofMinutes(30))
            )
            .transactionAware()
            .build()
    }

}