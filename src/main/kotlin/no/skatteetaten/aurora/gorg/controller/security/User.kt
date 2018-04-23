package no.skatteetaten.aurora.gorg.controller.security

import java.lang.Math.min
import org.springframework.security.core.userdetails.User as SpringSecurityUser


class User(
        username: String,
        val token: String,
        val fullName: String? = null
) : SpringSecurityUser(username, token, true, true, true, true, listOf()) {

    val tokenSnippet: String
        get() = token.substring(0, min(token.length, 5))

}