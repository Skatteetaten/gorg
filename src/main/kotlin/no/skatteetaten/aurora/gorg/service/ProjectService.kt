package no.skatteetaten.aurora.gorg.service

import io.fabric8.openshift.client.DefaultOpenShiftClient
import no.skatteetaten.aurora.gorg.extensions.REMOVE_AFTER_LABEL
import no.skatteetaten.aurora.gorg.extensions.TERMINATING_PHASE
import no.skatteetaten.aurora.gorg.extensions.removalTime
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class ProjectService(
    val client: DefaultOpenShiftClient,
    val deleteService: DeleteService
) {

    val logger = LoggerFactory.getLogger(ProjectService::class.java)

    fun findTemporaryProjects(now: Instant): List<TemporaryResource> {
        val projects = client.projects()
            .withLabel(REMOVE_AFTER_LABEL)
            .list().items

        return projects
            .filter { it.status.phase != TERMINATING_PHASE }
            .map {
                val removalTime = it.removalTime()


                TemporaryResource(
                    name = it.metadata.name,
                    namespace = "",
                    ttl = Duration.between(now, removalTime),
                    removalTime = removalTime,
                    resourceType = "project"
                )

            }
    }

    fun deleteProject(project: TemporaryResource): Boolean {
        return deleteService.deleteResource(project) {
            client.projects().withName(it.name).delete()
        }
    }
}