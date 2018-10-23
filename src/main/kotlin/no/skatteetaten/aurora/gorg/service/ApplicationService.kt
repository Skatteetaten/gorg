package no.skatteetaten.aurora.gorg.service

import io.fabric8.openshift.client.DefaultOpenShiftClient
import no.skatteetaten.aurora.gorg.extensions.applicationDeploymentsTemporary
import no.skatteetaten.aurora.gorg.extensions.removalTime
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class ApplicationService(val client: DefaultOpenShiftClient) {

    val logger = LoggerFactory.getLogger(ApplicationService::class.java)

    // https://utv-master.paas.skead.no:8443/apis/skatteetaten.no/v1/applicationdeployments?labelSelector=ttl

    fun findTemporaryApplications(now: Instant): List<TemporaryApplication> {
        val dcs = client.applicationDeploymentsTemporary()

        return dcs.map {
            val ttl = it.removalTime()?.let { it }

            TemporaryApplication(
                it.metadata.name,
                it.metadata.namespace,
                Duration.between(now, ttl),
                ttl
            )
        }
    }

    fun deleteApplication(dc: TemporaryApplication): Boolean {
        logger.info("Found app to devour: ${dc.name}. time-to-live expired ${dc.removalTime}")

 /*       val lst = listOf(client.deploymentConfigs(),
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
        }*/
        return true
    }


}