package no.skatteetaten.aurora.gorg.service

import io.fabric8.openshift.client.OpenShiftClient
import no.skatteetaten.aurora.gorg.extensions.REMOVE_AFTER_LABEL
import no.skatteetaten.aurora.gorg.extensions.removalTime
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class ApplicationService(val client: OpenShiftClient) {

    val logger = LoggerFactory.getLogger(ApplicationService::class.java)

    fun findTemporaryApplications(now: Instant): List<TemporaryApplication> {
        val dcs = client.deploymentConfigs()
            .inAnyNamespace()
            .withLabel(REMOVE_AFTER_LABEL)
            .list().items

        return dcs.map {
            val removalTime = it.removalTime()
            TemporaryApplication(
                it.metadata.name,
                it.metadata.namespace,
                Duration.between(now, removalTime),
                removalTime
            )
        }
    }

    fun deleteApplication(dc: TemporaryApplication): Boolean {
        logger.info("Found app to devour: ${dc.name}. time-to-live expired ${dc.removalTime}")

        val lst = listOf(client.deploymentConfigs(),
            client.services(),
            client.buildConfigs(),
            client.configMaps(),
            client.secrets(),
            client.imageStreams(),
            client.routes())

        val deleted = lst.map {
            it.inNamespace(dc.namespace).withLabel("app", dc.name).delete()
        }

        return deleted.all { it }.also {
            if (!it) {
                logger.error("Unable to delete application=${dc.name}")
            }
        }
    }


}