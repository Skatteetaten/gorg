package no.skatteetaten.aurora.gorg.service

import com.fkorotkov.openshift.newBuildConfig
import com.fkorotkov.openshift.newProject
import com.fkorotkov.openshift.metadata
import io.fabric8.kubernetes.api.model.Status
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.skatteetaten.aurora.gorg.extensions.errorStackTraceIfDebug
import no.skatteetaten.aurora.gorg.model.ApplicationDeployment
import no.skatteetaten.aurora.kubernetes.ClientTypes
import no.skatteetaten.aurora.kubernetes.KubernetesClient
import no.skatteetaten.aurora.kubernetes.TargetClient
import no.skatteetaten.aurora.kubernetes.crd.newSkatteetatenKubernetesResource
import no.skatteetaten.aurora.kubernetes.success
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




    fun deleteApplicationDeployment(item: ApplicationDeploymentResource) = deleteResource(item) { client ->
        runBlocking{ client.delete(newSkatteetatenKubernetesResource<ApplicationDeployment> { metadata { name = item.name
        namespace = item.namespace } }) }
    }

    fun deleteProject(item: ProjectResource) = deleteResource(item) { client ->
        runBlocking{ client.delete(newProject { metadata { name = item.name }})  }}

    fun deleteBuildConfig(item: BuildConfigResource) = deleteResource(item) { client ->
        runBlocking{ client.delete(newBuildConfig { metadata { name = item.name
        namespace = item.namespace}}) }}



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
        deleteFunction: (KubernetesClient) -> Status
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
                if (it.success()) {
                    count(item, "deleted")
                    logger.info("Resource=$item deleted successfully.")
                } else {
                    count(item, "error")
                    logger.error("Resource=$item was not deleted. Reason = ${it.message}")
                }
            }.success()
        } catch (e: Exception) {
            logger.errorStackTraceIfDebug(
                "Deletion of Resource=$item failed with exception=${e} message =${e.localizedMessage}",
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
