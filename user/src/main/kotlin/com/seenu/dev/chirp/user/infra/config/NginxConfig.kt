package com.seenu.dev.chirp.user.infra.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "nginx")
data class NginxConfig constructor(
    var trustedIps: List<String> = emptyList(),
    var requireProxy: Boolean = false
)
