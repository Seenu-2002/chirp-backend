package com.seenu.dev.chirp.service

import com.seenu.dev.chirp.domain.type.UserId
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.time.Duration

@Service
class EmailService constructor(
    private val javaMailSender: JavaMailSender,
    private val templateService: EmailTemplateService,
    @param:Value("\${chirp.email.from}") private val fromEmail: String,
    @param:Value("\${chirp.email.url}") private val baseUrl: String
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun sendVerificationEmail(
        email: String,
        username: String,
        userId: UserId,
        token: String
    ) {
        logger.info("Sending verification email for user $userId")

        val verificationUrl = UriComponentsBuilder
            .fromUriString("$baseUrl/api/auth/verify")
            .queryParam("token", token)
            .build()
            .toUriString()

        val htmlContent = templateService.processTemplate(
            templateName = "emails/account-verification",
            variables = mapOf(
                "username" to username,
                "verificationUrl" to verificationUrl
            )
        )

        sendHtmlEmail(
            to = email,
            subject = "Verify your Chirp account",
            html = htmlContent,
        )
    }

    fun sendPasswordResetEmail(
        email: String,
        username: String,
        userId: UserId,
        token: String,
        expiredIn: Duration
    ) {
        logger.info("Sending password reset email for user $userId")

        val resetPasswordUrl = UriComponentsBuilder
            .fromUriString("$baseUrl/api/auth/reset-password")
            .queryParam("token", token)
            .build()
            .toUriString()

        val htmlContent = templateService.processTemplate(
            templateName = "emails/reset-password",
            variables = mapOf(
                "username" to username,
                "resetPasswordUrl" to resetPasswordUrl,
                "expiresInMinutes" to expiredIn.toMinutes()
            )
        )

        sendHtmlEmail(
            to = email,
            subject = "Reset your Chirp password",
            html = htmlContent,
        )
    }

    private fun sendHtmlEmail(to: String, subject: String, html: String) {
        val message = javaMailSender.createMimeMessage()
        MimeMessageHelper(message, true, "UTF-8").apply {
            setFrom(fromEmail)
            setTo(to)
            setSubject(subject)
            setText(html, true)
        }

        try{
            javaMailSender.send(message)
        } catch (exp: MailException) {
            logger.error("Could not send verification email to ${to}", exp)
        }
    }

}