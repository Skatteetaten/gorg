package no.skatteetaten.aurora.gorg.service

import io.fabric8.openshift.client.DefaultOpenShiftClient
import no.skatteetaten.aurora.gorg.extensions.REMOVE_AFTER_LABEL
import no.skatteetaten.aurora.gorg.extensions.removalTime
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class BuildConfigService(
    val client: DefaultOpenShiftClient,
    val deleteService: DeleteService
) {

    val logger = LoggerFactory.getLogger(BuildConfigService::class.java)

    fun findTemporaryBuildConfigs(now: Instant): List<TemporaryResource> {

        val buildConfigs = client.buildConfigs()
            .inAnyNamespace()
            .withLabel(REMOVE_AFTER_LABEL)
            .list().items

        return buildConfigs.map {
            val removalTime = it.removalTime()

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
            client.buildConfigs().inNamespace(project.namespace).withName(project.name).delete()
        }
    }
}


