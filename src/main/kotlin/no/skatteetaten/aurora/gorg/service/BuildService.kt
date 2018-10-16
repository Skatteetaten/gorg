package no.skatteetaten.aurora.gorg.service

import io.fabric8.openshift.client.DefaultOpenShiftClient
import no.skatteetaten.aurora.gorg.extensions.REMOVE_AFTER_LABEL
import no.skatteetaten.aurora.gorg.extensions.removalTime
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class BuildService(val client: DefaultOpenShiftClient){

    val logger = LoggerFactory.getLogger(BuildService::class.java)

    fun findTemporaryBuilds(now: Instant): List<TemporaryBuild> {

        val builds = client.builds()
            .withLabel(REMOVE_AFTER_LABEL)
            .list().items

        return builds.map {
            val removalTime = it.removalTime()
            TemporaryBuild(
                it.metadata.name,
                it.metadata.namespace,
                Duration.between(now, removalTime),
                removalTime)
        }

    }

    fun deleteBuild(build: TemporaryBuild): Boolean {
        logger.info("Found build to devour: ${build.name}. time-to-live expired ${build.removalTime}")
        return client.builds().withName(build.name).delete().also {
            if (it) {
                logger.info("Build ${build.name} gobbled, tastes like chicken!")
            } else {
                logger.error("Unable to delete project=${build.name}")
            }
        }
    }
}
