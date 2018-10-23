package no.skatteetaten.aurora.gorg.service

import io.fabric8.openshift.client.DefaultOpenShiftClient
import no.skatteetaten.aurora.gorg.extensions.applicationDeploymentsTemporary
import no.skatteetaten.aurora.gorg.extensions.deleteApplicationDeployment
import no.skatteetaten.aurora.gorg.extensions.removalTime
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class ApplicationDeploymentService(val client: DefaultOpenShiftClient, @Value("\${dryrun}") val dryrun: Boolean) {

    val logger = LoggerFactory.getLogger(ApplicationDeploymentService::class.java)

    fun findTemporaryApplicationDeployments(now: Instant): List<TemporaryApplicationDeployment> {
        val dcs = client.applicationDeploymentsTemporary()


        return dcs.map {
            val ttl = it.removalTime()?.let { it }

            TemporaryApplicationDeployment(
                it.metadata.name,
                it.metadata.namespace,
                Duration.between(now, ttl),
                ttl
            )
        }
    }

    fun deleteApplicationDeployment(dc: TemporaryApplicationDeployment): Boolean {
        logger.info("Found app to devour: ${dc.name}. time-to-live expired ${dc.removalTime}")

        if (dryrun){
            logger.info("Dryrun is true: ApplicationDeployment ${dc.name}, namespace ${dc.namespace}. time-to-live expired ${dc.removalTime}. Set dryrun to false to delete this application")
        } else {
            client.deleteApplicationDeployment(dc.namespace, dc.name)
                .also {
                    if (it.isSuccessful) {
                        logger.info("Build ${dc.name} gobbled, tastes like chicken!")
                    } else {
                        logger.error("Unable to delete ApplicationDeployment=${dc.name}. Response ${it.header("Status Code")}")
                    }
                }
        }
        return true
    }


}