package com.seenu.dev.chirp.user.api.config

import com.seenu.dev.chirp.user.domain.exceptions.RateLimitException
import com.seenu.dev.chirp.user.infra.rate_limiting.IpRateLimiter
import com.seenu.dev.chirp.user.infra.rate_limiting.IpResolver
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import java.time.Duration

@Component
class IpRateLimitInterceptor constructor(
    private val ipRateLimiter: IpRateLimiter,
    private val ipResolver: IpResolver,
    @param:Value("\${chirp.rate-limit.ip.apply-limit}")
    private val applyLimit: Boolean

) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod && applyLimit) {
            val annotation = handler.getMethodAnnotation(IpRateLimit::class.java)
            if (annotation != null) {
                val clientIp = ipResolver.getClientIp(request)

                return try {
                    ipRateLimiter.withIpRateLimit(
                        ipAddress = clientIp,
                        resetsIn = Duration.of(
                            annotation.duration,
                            annotation.unit
                        ),
                        annotation.requests,
                        action = { true }
                    )
                } catch (exp: RateLimitException) {
                    response.sendError(429)
                    false
                }
            }
        }

        return super.preHandle(request, response, handler)
    }

}