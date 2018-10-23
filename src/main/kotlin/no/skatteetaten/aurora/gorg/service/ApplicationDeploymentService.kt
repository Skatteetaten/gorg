package no.skatteetaten.aurora.gorg.service

import io.fabric8.openshift.client.DefaultOpenShiftClient
import no.skatteetaten.aurora.gorg.extensions.applicationDeploymentsTemporary
import no.skatteetaten.aurora.gorg.extensions.deleteApplicationDeployment
import no.skatteetaten.aurora.gorg.extensions.removalTime
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class ApplicationDeploymentService(
    val client: DefaultOpenShiftClient,
    val deleteService: DeleteService
) {

    val logger = LoggerFactory.getLogger(ApplicationDeploymentService::class.java)

    fun findTemporaryApplicationDeployments(now: Instant): List<TemporaryResource> {
        val dcs = client.applicationDeploymentsTemporary()


        return dcs.map {
            val removalTime = it.removalTime()?.let { it }

            TemporaryResource(
                name = it.metadata.name,
                namespace = "",
                ttl = Duration.between(now, removalTime),
                removalTime = removalTime,
                resourceType = "buildConfig"
            )
        }
    }

    fun deleteProject(project: TemporaryResource): Boolean {
        return deleteService.deleteResource(project) {
            client.deleteApplicationDeployment(project.namespace, project.name)
        }
    }
}