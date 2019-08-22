package no.skatteetaten.aurora.gorg.service

import io.fabric8.kubernetes.client.KubernetesClientException
import io.fabric8.openshift.client.DefaultOpenShiftClient
import io.fabric8.openshift.client.OpenShiftClient
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import mu.KotlinLogging
import no.skatteetaten.aurora.gorg.extensions.deleteApplicationDeployment
import no.skatteetaten.aurora.gorg.extensions.errorStackTraceIfDebug
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class DeleteService(
    val client: OpenShiftClient,
    val meterRegistry: MeterRegistry,
    @Value("\${gorg.delete.resources}") val deleteResources: Boolean
) {

    companion object {
        val METRICS_DELETED_RESOURCES = "gorg_deleted_resources"
    }
    
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
                "deleteResources=false. Resource=$item. Will be deleted once deleteResource flag is true"
            )
            count(item, "skipped")
            return false
        }

        return try {
            deleteFunction(client).also {
                if (it) {
                    count(item, "deleted")
                    logger.info("Resource=$item deleted successfully.")
                } else {
                    count(item, "error")
                    logger.info("Resource=$item was not deleted.")
                }
            }
        } catch (e: KubernetesClientException) {
            logger.errorStackTraceIfDebug(
                "Deletion of Resource=$item failed with exception=${e.code} message=${e.localizedMessage}",
                e
            )
            count(item, "error")
            false
        }
    }

    private fun count(item: BaseResource, status: String) {
        meterRegistry.counter(
            METRICS_DELETED_RESOURCES,
            listOf(
                Tag.of("resource", item.javaClass.name.replace("Resource", "")),
                Tag.of("status", status)
            )
        ).increment()
    }
}
