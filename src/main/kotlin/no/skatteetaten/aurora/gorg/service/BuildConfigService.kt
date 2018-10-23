package no.skatteetaten.aurora.gorg.service

import io.fabric8.openshift.client.DefaultOpenShiftClient
import no.skatteetaten.aurora.gorg.extensions.REMOVE_AFTER_LABEL
import no.skatteetaten.aurora.gorg.extensions.removalTime
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class BuildConfigService(val client: DefaultOpenShiftClient,
    @Value("\${gorg.dryrun}") val dryrun: Boolean){

    val logger = LoggerFactory.getLogger(BuildConfigService::class.java)

    fun findTemporaryBuildConfigs(now: Instant): List<TemporaryBuildConfig> {

        val buildConfigs = client.buildConfigs()
            .inAnyNamespace()
            .withLabel(REMOVE_AFTER_LABEL)
            .list().items

        return buildConfigs.map {
            val removalTime = it.removalTime()
            TemporaryBuildConfig(
                it.metadata.name,
                it.metadata.namespace,
                Duration.between(now, removalTime),
                removalTime)
        }

    }

    fun deleteBuildConfig(buildConfig: TemporaryBuildConfig): Boolean {
        logger.info("Found build to devour: ${buildConfig.name}. time-to-live expired ${buildConfig.removalTime}")

        if (dryrun) {
            logger.info("Dryrun = true. Build ${buildConfig.name} time-to-live expired. Will be deleted once dryrun flagg is false")
        } else {
            return client.buildConfigs().inNamespace(buildConfig.namespace).withName(buildConfig.name).delete()
                .also {
                    if (it) {
                        logger.info("Build ${buildConfig.name} gobbled, tastes like chicken!")
                    } else {
                        logger.error("Unable to delete project=${buildConfig.name}")
                    }
                }
        }
        return true
    }
}
