package no.skatteetaten.aurora.gorg.controller.security

import javax.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint
import org.springframework.security.web.util.matcher.RequestMatcher

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    @Value("\${management.server.port}") val managementPort: Int,
    @Value("\${gorg.username}") val userName: String,
    @Value("\${gorg.password}") val password: String,
    val passwordEncoder: PasswordEncoder,
    val authEntryPoint: BasicAuthenticationEntryPoint

) : WebSecurityConfigurerAdapter() {

    @Autowired
    @Throws(Exception::class)
    fun configureGlobalSecurity(auth: AuthenticationManagerBuilder) {
        auth.inMemoryAuthentication().withUser(userName).password(passwordEncoder.encode(password)).roles("USER")
    }

    private fun forPort(port: Int) = RequestMatcher { request: HttpServletRequest -> port == request.localPort }

    override fun configure(http: HttpSecurity) {

        http.csrf().disable().sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // We don't need sessions to be created.

        http.authorizeRequests()
            .requestMatchers(forPort(managementPort)).permitAll()
            .antMatchers("/docs/index.html").permitAll()
            .antMatchers("/").permitAll()
            .antMatchers("/api/**").hasRole("USER")
            .and().httpBasic().realmName("GORG").authenticationEntryPoint(authEntryPoint)
    }
}
