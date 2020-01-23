package no.skatteetaten.aurora.gorg.service

import io.fabric8.kubernetes.client.KubernetesClientException
import io.fabric8.openshift.client.DefaultOpenShiftClient
import io.fabric8.openshift.client.OpenShiftClient
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import java.util.concurrent.TimeUnit
import mu.KotlinLogging
import no.skatteetaten.aurora.gorg.ApplicationConfig.Companion.OPENSHIFT_API_METRICS
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
        const val METRICS_DELETED_RESOURCES = "gorg_deleted_resources"
    }

    fun deleteApplicationDeployment(item: ApplicationDeploymentResource) = deleteResource(item) { client ->
        (client as DefaultOpenShiftClient).deleteApplicationDeployment(item.namespace, item.name)
    }

    fun deleteProject(item: ProjectResource) = deleteResource(item) { client ->
        client.projects().withName(item.name).delete()
    }

    fun deleteBuildConfig(item: BuildConfigResource) = deleteResource(item) { client ->
        client.buildConfigs()
            .inNamespace(item.namespace)
            .withName(item.name)
            .withPropagationPolicy("Background")
            .delete()
    }

    fun deleteResource(
        item: BaseResource,
        deleteFunction: (OpenShiftClient) -> Boolean
    ): Boolean {

        if (!deleteResources) {
            val start = System.currentTimeMillis()
            Thread.sleep(100)
            meterRegistry.timer(
                OPENSHIFT_API_METRICS,
                listOf(Tag.of("method", "DELETE"), Tag.of("outcome", "SUCCESS"))
            )
                .record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS)
            logger.info(
                "deleteResources=false. Resource=$item. Will be deleted once deleteResource flag is true"
            )
            count(item, "skipped")
            return false
        }

        return try {
            val start = System.currentTimeMillis()
            deleteFunction(client).also {
                if (it) {
                    meterRegistry.timer(
                        OPENSHIFT_API_METRICS,
                        listOf(Tag.of("method", "DELETE"), Tag.of("outcome", "SUCCESS"))
                    )
                        .record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS)
                    count(item, "deleted")
                    logger.info("Resource=$item deleted successfully.")
                } else {
                    meterRegistry.timer(
                        OPENSHIFT_API_METRICS,
                        listOf(Tag.of("method", "DELETE"), Tag.of("outcome", "ERROR"))
                    )
                        .record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS)
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
                Tag.of("resource", item.javaClass.simpleName.replace("Resource", "")),
                Tag.of("status", status),
                Tag.of("affiliation", item.affiliation)
            )
        ).increment()
    }
}
