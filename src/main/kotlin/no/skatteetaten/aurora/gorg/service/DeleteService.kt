package no.skatteetaten.aurora.gorg.service

import io.fabric8.kubernetes.client.KubernetesClientException
import io.fabric8.openshift.client.DefaultOpenShiftClient
import io.fabric8.openshift.client.OpenShiftClient
import no.skatteetaten.aurora.gorg.extensions.deleteApplicationDeployment
import no.skatteetaten.aurora.gorg.extensions.errorStackTraceIfDebug
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class DeleteService(
    val client: OpenShiftClient,
    @Value("\${gorg.delete.resources}") val deleteResources: Boolean
) {

    val logger = LoggerFactory.getLogger(DeleteService::class.java)

    fun deleteApplicationDeployment(item: ApplicationDeploymentResource) = deleteResource(item) { client ->
        (client as DefaultOpenShiftClient).deleteApplicationDeployment(item.namespace, item.name)
    }

    fun deleteProject(item: ProjectResource) = deleteResource(item) { client ->
        client.projects().withName(item.name).delete()
    }

    fun deleteBuildConfig(item: BuildConfigResource) = deleteResource(item) { client ->
        client.buildConfigs().inNamespace(item.namespace).withName(item.name).delete()
    }

    fun deleteResource(
        item: BaseResource,
        deleteFunction: (OpenShiftClient) -> Boolean
    ): Boolean {

        if (!deleteResources) {
            logger.info(
                "deleteResources = false. Resource=$item. Will be deleted once deleteResource flag is true"
            )
            return false
        }

        return try {
             deleteFunction(client)
                .also {
                    if (it) {
                        logger.info("Resource=$item deleted successfully.")
                    } else {
                        logger.info("Resource=$item was not deleted.")
                    }
                }
        } catch (e: KubernetesClientException) {
            logger.errorStackTraceIfDebug(
                "Deletion of Resource=$item failed with exception=${e.code} message=${e.localizedMessage}",
                e
            )
            false
        }
    }
}

