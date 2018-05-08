package no.skatteetaten.aurora.gorg.service

import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.openshift.api.model.DeploymentConfig
import io.fabric8.openshift.api.model.Project
import io.fabric8.openshift.client.DefaultOpenShiftClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant


@Service
class CrawlService {

    // Makes logger availible
    val logger: Logger = LoggerFactory.getLogger(CrawlService::class.java)


    fun test() {
        val now: Instant = Instant.now()
        logger.info("time: $now")
        val epoch = System.currentTimeMillis()
        logger.info("epoch: $epoch")
        val instant = Instant.ofEpochMilli(epoch)
        logger.info("back to time: $instant")
    }


    fun HasMetadata.removeAfter(): Instant? {
        val labels = this.metadata.labels ?: emptyMap() //
        return labels["removeAfter"]?.let {
            Instant.ofEpochSecond(it.toLong())
        }
    }

    private fun <T : HasMetadata> List<T>.isAfter(now: Instant): List<T> {
        return this.filter { it.removeAfter()?.isAfter(now) ?: false }
    }

    //
    fun findExpiredDc(): List<DeploymentConfig> {
        logger.info("findExpiredDc() invoked!")
        val dcs = DefaultOpenShiftClient().deploymentConfigs()
                .list().items.isAfter(Instant.now())
        return dcs
    }

    fun findExpiredProjectInfo(): List<Project> = DefaultOpenShiftClient().projects()
            .list().items.isAfter(Instant.now())


    @Scheduled(fixedRate = 300_000, initialDelay = 2_000)
    fun deleteProjects() {
        logger.info("Searching for projects to gorge")
        val client = DefaultOpenShiftClient()


        findExpiredProjectInfo()
                .forEach {
                    logger.info("Found project to devour: ${it.metadata.name}. time-to-live expired ${it.removeAfter().toString()}")
                    client.projects().withName(it.metadata.name).delete() // deletes namespace
                    logger.info("Project ${it.metadata.name} gobbled, tastes like chicken!")
                }
        logger.info("No more projects to feed on, will try again in a while")

    }

    @Scheduled(fixedRate = 300_000, initialDelay = 2_000)
    fun testKim() {
        logger.info("Searching for applications to gorge")

        val client = DefaultOpenShiftClient()
        findExpiredDc()
                .forEach {
                    logger.info("Found app to devour: ${it.metadata.name}. time-to-live expired ${it.removeAfter().toString()}")
                    client.deploymentConfigs()
                            .inNamespace(it.metadata.namespace)
                            .withLabel("app", it.metadata.name)
                            .delete()

                    client.services()
                            .inNamespace(it.metadata.namespace)
                            .withLabel("app", it.metadata.name)
                            .delete()

                    client.buildConfigs()
                            .inNamespace(it.metadata.namespace)
                            .withLabel("app", it.metadata.name)
                            .delete()

                    client.configMaps()
                            .inNamespace(it.metadata.namespace)
                            .withLabel("app", it.metadata.name)
                            .delete()

                    client.secrets()
                            .inNamespace(it.metadata.namespace)
                            .withLabel("app", it.metadata.name)
                            .delete()

                    client.imageStreams()
                            .inNamespace(it.metadata.namespace)
                            .withLabel("app", it.metadata.name)
                            .delete()

                    client.routes()
                            .inNamespace(it.metadata.namespace)
                            .withLabel("app", it.metadata.name)
                            .delete()
                }
        logger.info("No more applications to feed on, will try again in a while")

    }
}
