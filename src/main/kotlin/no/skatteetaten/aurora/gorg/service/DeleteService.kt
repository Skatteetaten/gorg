package no.skatteetaten.aurora.gorg.service

import io.fabric8.openshift.client.OpenShiftClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class DeleteService(val client: OpenShiftClient) {

    val logger: Logger = LoggerFactory.getLogger(DeleteService::class.java)

    fun deleteProject(project: CrawlService.TemporaryProject): Boolean {
        logger.info("Found project to devour: ${project.name}. time-to-live expired ${project.removalTime}")
        return client.projects().withName(project.name).delete().also {
            if(it) {
                logger.info("Project ${project.name} gobbled, tastes like chicken!")
            } else {
                logger.warn("Unable to delete project ${project.name}")
            }
        }
    }

    fun deleteApplication(dc: CrawlService.TemporaryApplication): Boolean {
        logger.info("Found app to devour: ${dc.name}. time-to-live expired ${dc.removalTime}")
        val deleted = mutableListOf<Boolean>()

        deleted.add(client.deploymentConfigs()
                .inNamespace(dc.namespace)
                .withLabel("app", dc.name)
                .delete())

        deleted.add(client.services()
                .inNamespace(dc.namespace)
                .withLabel("app", dc.name)
                .delete())

        deleted.add(client.buildConfigs()
                .inNamespace(dc.namespace)
                .withLabel("app", dc.name)
                .delete())

        deleted.add(client.configMaps()
                .inNamespace(dc.namespace)
                .withLabel("app", dc.name)
                .delete())

        deleted.add(client.secrets()
                .inNamespace(dc.namespace)
                .withLabel("app", dc.name)
                .delete())

        deleted.add(client.imageStreams()
                .inNamespace(dc.namespace)
                .withLabel("app", dc.name)
                .delete())

        deleted.add(client.routes()
                .inNamespace(dc.namespace)
                .withLabel("app", dc.name)
                .delete())

        return deleted.all { true }
    }
}
