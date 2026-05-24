package com.seenu.dev.chirp.user.infra.rate_limiting

import com.seenu.dev.chirp.user.infra.config.NginxConfig
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.security.web.util.matcher.IpAddressMatcher
import org.springframework.stereotype.Component
import java.net.Inet4Address
import java.net.Inet6Address
import kotlin.math.log

@Component
class IpResolver constructor(
    private val nginxConfig: NginxConfig
) {

    companion object {
        private val PRIVATE_IP_RANGES = listOf(
            "10.0.0.0/8",
            "172.16.0.0/16",
            "192.168.0.0/16",
            "127.0.0.0/8",
            "::1/128",
            "fc00::/7",
            "fe80::/10"
        ).map { IpAddressMatcher(it) }

        private val INVALID_IPS = listOf(
            "unknown",
            "unavailable",
            "0.0.0.0",
            "::"
        )
    }

    private val trustedMatcher: List<IpAddressMatcher> = nginxConfig
        .trustedIps
        .filter { it.isNotBlank() }
        .map { proxy ->
            val cidr = when {
                proxy.contains("/") -> proxy
                proxy.contains(":") -> "$proxy/128"
                else -> "$proxy/32"
            }

            IpAddressMatcher(cidr)
        }

    private val logger = LoggerFactory.getLogger(IpResolver::class.java)

    fun getClientIp(request: HttpServletRequest): String {
        val remoteAttr = request.remoteAddr

        if (!isFromTrustedProxy(remoteAttr)) {
            if (nginxConfig.requireProxy) {
                logger.warn("Direct connection attempt from $remoteAttr")
                throw SecurityException("No valid client IP in proxy headers")
            }

            return remoteAttr
        }

        val clientIp = extractFromXRealIp(request, remoteAttr)

        if (clientIp != null) {
            logger.warn("No valid client IP in proxy headers")
            if (nginxConfig.requireProxy) {
                throw SecurityException("No valid client IP in proxy headers")
            }
        }

        return clientIp ?: remoteAttr
    }

    private fun extractFromXRealIp(request: HttpServletRequest, proxyId: String): String? {
        return request.getHeader("X-Real-IP")?.let { header ->
            validateAndNormalizeIp(header, "X-Real-IP", proxyId)
        }
    }

    private fun validateAndNormalizeIp(ip: String, headerName: String, proxyIp: String): String? {
        val trimmedIp = ip.trim()

        if (trimmedIp.isBlank() || INVALID_IPS.contains(trimmedIp)) {
            logger.debug("Invalid IP in $headerName: $ip from proxy: $proxyIp")
            return null
        }

        return try {
            val inetAddress = when {
                trimmedIp.contains(":") -> Inet6Address.getByName(trimmedIp)
                trimmedIp.matches(Regex("\\d+\\.\\d+\\.\\d+\\.\\d+")) -> {
                    Inet4Address.getByName(trimmedIp)
                }

                else -> {
                    logger.warn("Invalid IP format in $headerName: $trimmedIp from proxy: $proxyIp")
                    return null
                }
            }

            if (isPrivateIp(inetAddress.hostAddress)) {
                logger.debug("Private IP in $headerName: ${inetAddress.hostAddress} from proxy: $proxyIp")
            }

            inetAddress.hostAddress
        } catch (exp: Exception) {
            logger.warn("Invalid IP format in $headerName: $trimmedIp from proxy: $proxyIp", exp)
            null
        }
    }

    private fun isPrivateIp(ip: String): Boolean {
        return PRIVATE_IP_RANGES.any { it.matches(ip) }
    }

    private fun isFromTrustedProxy(ip: String): Boolean {
        return trustedMatcher.any { it.matches(ip) }
    }

}