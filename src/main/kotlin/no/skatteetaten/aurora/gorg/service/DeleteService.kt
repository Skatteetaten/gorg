package no.skatteetaten.aurora.gorg.service

import io.fabric8.openshift.client.OpenShiftClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class DeleteService(val client: OpenShiftClient) {

    val logger: Logger = LoggerFactory.getLogger(DeleteService::class.java)

    fun deleteProject(project: CrawlService.TemporaryProject) {
        logger.info("Found project to devour: ${project.name}. time-to-live expired ${project.removalTime}")
        client.projects().withName(project.name).delete() // deletes namespace
        logger.info("Project ${project.name} gobbled, tastes like chicken!")
    }

    fun deleteApplication(dc: CrawlService.TemporaryApplication) {
        logger.info("Found app to devour: ${dc.name}. time-to-live expired ${dc.removalTime}")
        client.deploymentConfigs()
                .inNamespace(dc.namespace)
                .withLabel("app", dc.name)
                .delete()

        client.services()
                .inNamespace(dc.namespace)
                .withLabel("app", dc.name)
                .delete()

        client.buildConfigs()
                .inNamespace(dc.namespace)
                .withLabel("app", dc.name)
                .delete()

        client.configMaps()
                .inNamespace(dc.namespace)
                .withLabel("app", dc.name)
                .delete()

        client.secrets()
                .inNamespace(dc.namespace)
                .withLabel("app", dc.name)
                .delete()

        client.imageStreams()
                .inNamespace(dc.namespace)
                .withLabel("app", dc.name)
                .delete()

        client.routes()
                .inNamespace(dc.namespace)
                .withLabel("app", dc.name)
                .delete()

    }
}
