package no.skatteetaten.aurora.gorg

import io.fabric8.openshift.client.DefaultOpenShiftClient
import io.fabric8.openshift.client.OpenShiftClient
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ApplicationConfig : BeanPostProcessor {

    @Bean
    fun client(): OpenShiftClient {
        return DefaultOpenShiftClient()
    }

}
