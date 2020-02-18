package no.skatteetaten.aurora.gorg

import no.skatteetaten.aurora.kubernetes.KubernetesClientConfig
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint

@Configuration
@Import(KubernetesClientConfig::class)
class ApplicationConfig : BeanPostProcessor {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun basic(): BasicAuthenticationEntryPoint {
        return BasicAuthenticationEntryPoint().also {
            it.realmName = "GORG"
        }
    }
}
