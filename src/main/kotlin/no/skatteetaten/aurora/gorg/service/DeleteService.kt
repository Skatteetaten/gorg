package no.skatteetaten.aurora.gorg.service

import com.fkorotkov.openshift.metadata
import com.fkorotkov.openshift.newBuildConfig
import com.fkorotkov.openshift.newProject
import io.fabric8.kubernetes.api.model.Status
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.skatteetaten.aurora.gorg.extensions.errorStackTraceIfDebug
import no.skatteetaten.aurora.gorg.model.newApplicationDeployment
import no.skatteetaten.aurora.kubernetes.config.ClientTypes
import no.skatteetaten.aurora.kubernetes.KubernetesCoroutinesClient
import no.skatteetaten.aurora.kubernetes.config.TargetClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class DeleteService(
    @TargetClient(ClientTypes.SERVICE_ACCOUNT)
    val client: KubernetesCoroutinesClient,
    val meterRegistry: MeterRegistry,
    @Value("\${gorg.delete.resources}") val deleteResources: Boolean
) {

    companion object {
        const val METRICS_DELETED_RESOURCES = "gorg_deleted_resources"
    }

    fun deleteApplicationDeployment(item: ApplicationDeploymentResource) = deleteResource(item) { client ->
        runBlocking {
            client.deleteBackground(
                newApplicationDeployment {
                    metadata {
                        name = item.name
                        namespace = item.namespace
                    }
                }
            )
        }
    }

    fun deleteProject(item: ProjectResource) = deleteResource(item) { client ->
        runBlocking { client.deleteBackground(newProject { metadata { name = item.name } }) }
    }

    fun deleteBuildConfig(item: BuildConfigResource) = deleteResource(item) { client ->
        runBlocking {
            client.deleteBackground(
                newBuildConfig {
                    metadata {
                        name = item.name
                        namespace = item.namespace
                    }
                }
            )
        }
    }

    fun deleteResource(
        item: BaseResource,
        deleteFunction: (KubernetesCoroutinesClient) -> Status
    ): Boolean {
        if (!deleteResources) {
            logger.info(
                "deleteResources=false. Resource=$item. Will be deleted once deleteResource flag is true"
            )
            count(item, "skipped")
            return false
        }

        var status = false
        return try {
            deleteFunction(client).also {
                status = if (it.status == "Success") {
                    count(item, "deleted")
                    logger.info("Resource=$item deleted successfully.")
                    true
                } else {
                    count(item, "error")
                    if (it.status == "Conflict") {
                        logger.error("Resource=$item in conflict with message=${it.message}")
                    } else {
                        logger.error("Resource=$item was not deleted.")
                    }
                    false
                }
            }
            status
        } catch (e: Exception) {
            logger.errorStackTraceIfDebug(
                "Deletion of Resource=$item", e
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
