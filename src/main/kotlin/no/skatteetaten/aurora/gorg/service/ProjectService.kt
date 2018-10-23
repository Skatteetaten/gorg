package no.skatteetaten.aurora.gorg.service

import io.fabric8.openshift.client.DefaultOpenShiftClient
import no.skatteetaten.aurora.gorg.extensions.REMOVE_AFTER_LABEL
import no.skatteetaten.aurora.gorg.extensions.TERMINATING_PHASE
import no.skatteetaten.aurora.gorg.extensions.removalTime
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class ProjectService(val client: DefaultOpenShiftClient,@Value("\${dryrun}") val dryrun: Boolean) {

    val logger = LoggerFactory.getLogger(ProjectService::class.java)

    fun findTemporaryProjects(now: Instant): List<TemporaryProject> {
        val projects = client.projects()
                .withLabel(REMOVE_AFTER_LABEL)
                .list().items

        return projects
                .filter { it.status.phase != TERMINATING_PHASE }
                .map {
                    val removalTime = it.removalTime()
                    TemporaryProject(
                            it.metadata.name,
                            it.metadata.labels["affiliation"] ?: "",
                            Duration.between(now, removalTime),
                            removalTime
                    )
                }
    }

    fun deleteProject(project: TemporaryProject): Boolean {

        logger.info("Found project to devour: ${project.name}. time-to-live expired ${project.removalTime}")
        if (dryrun) {
            logger.info("dryrun = true, Project ${project.name} time-to-live expired. Will be deleted once dryrun flag is false")
        } else {
            return client.projects().withName(project.name).delete().also {
                if (it) {
                    logger.info("Project ${project.name} gobbled, tastes like chicken!")
                } else {
                    logger.error("Unable to delete project=${project.name}")
                }
            }
        }
       return true
    }
}