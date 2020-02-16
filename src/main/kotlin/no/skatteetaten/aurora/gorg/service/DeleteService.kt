package no.skatteetaten.aurora.gorg.service

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.skatteetaten.aurora.gorg.extensions.errorStackTraceIfDebug
import no.skatteetaten.aurora.kubernetes.ClientTypes
import no.skatteetaten.aurora.kubernetes.KubernetesClient
import no.skatteetaten.aurora.kubernetes.TargetClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class DeleteService(
    @TargetClient(ClientTypes.SERVICE_ACCOUNT)
    val client: KubernetesClient,
    val meterRegistry: MeterRegistry,
    @Value("\${gorg.delete.resources}") val deleteResources: Boolean
) {

    companion object {
        const val METRICS_DELETED_RESOURCES = "gorg_deleted_resources"
    }

/*


    fun deleteApplicationDeployment(item: ApplicationDeploymentResource) = deleteResource(item) { client ->
        client.delete(newSkatteetatenKubernetesResource<ApplicationDeployment> { metadata { item } })
    }

    fun deleteProject(item: ProjectResource) = deleteResource(item) { client ->
        client.delete(newProject { metadata { item } })  }

    fun deleteBuildConfig(item: BuildConfigResource) = deleteResource(item) { client ->
        client.delete(newBuildConfig { metadata { item } }) }

*/

/*
    fun deleteBuildConfig(item: BuildConfigResource) = deleteResource(item) { client ->
        client.buildConfigs()
            .inNamespace(item.namespace)
            .withName(item.name)
            .withPropagationPolicy("Background")
            .delete()
    }*/

    fun deleteResource(
        item: BaseResource,
        deleteFunction: (KubernetesClient) -> Boolean
    ): Boolean {

        if (!deleteResources) {
            logger.info(
                "deleteResources=false. Resource=$item. Will be deleted once deleteResource flag is true"
            )
            count(item, "skipped")
            return false
        }

        return try {
            runBlocking { deleteFunction(client).also {
                if (it) {
                    count(item, "deleted")
                    logger.info("Resource=$item deleted successfully.")
                } else {
                    count(item, "error")
                    logger.info("Resource=$item was not deleted.")
                }
            } }
        } catch (e: Exception) {
            logger.errorStackTraceIfDebug(
                "Deletion of Resource=$item failed with exception=${e} message=${e.localizedMessage}",
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
                Tag.of("resource", item.javaClass.simpleName.replace("Resource", "")),
                Tag.of("status", status),
                Tag.of("affiliation", item.affiliation)
            )
        ).increment()
    }
}
