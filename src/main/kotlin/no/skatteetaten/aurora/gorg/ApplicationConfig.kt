package no.skatteetaten.aurora.gorg

import io.fabric8.openshift.client.DefaultOpenShiftClient
import io.fabric8.openshift.client.OpenShiftClient
import no.skatteetaten.aurora.kubernetes.ClientTypes
import no.skatteetaten.aurora.kubernetes.KubernetesClient
import no.skatteetaten.aurora.kubernetes.KubernetesClientConfig
import no.skatteetaten.aurora.kubernetes.TargetClient
import no.skatteetaten.aurora.kubernetes.TokenFetcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint

@Configuration
@Import(KubernetesClientConfig::class)
class ApplicationConfig(
    @Value("\${kubernetes.tokenLocation:/var/run/secrets/kubernetes.io/serviceaccount/token}") val tokenLocation: String
) : BeanPostProcessor {

    @Bean
    fun client(): OpenShiftClient {
        return DefaultOpenShiftClient()
    }

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
