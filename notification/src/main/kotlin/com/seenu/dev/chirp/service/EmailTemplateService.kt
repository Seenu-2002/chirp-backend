package com.seenu.dev.chirp.service

import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class EmailTemplateService constructor(
    private val templateEngine: TemplateEngine
) {

    fun processTemplate(
        templateName: String,
        variables: Map<String, Any> = emptyMap()
    ): String {
        val ctx = Context().apply {
            variables.forEach { key, value ->
                setVariable(key, value)
            }
        }

        return templateEngine.process(templateName, ctx)
    }


}