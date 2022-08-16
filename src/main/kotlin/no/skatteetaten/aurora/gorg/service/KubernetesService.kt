package no.skatteetaten.aurora.gorg.service

import com.fkorotkov.openshift.metadata
import com.fkorotkov.openshift.newBuildConfig
import com.fkorotkov.openshift.newProject
import kotlinx.coroutines.runBlocking
import no.skatteetaten.aurora.gorg.extensions.REMOVE_AFTER_LABEL
import no.skatteetaten.aurora.gorg.extensions.TERMINATING_PHASE
import no.skatteetaten.aurora.gorg.extensions.toResource
import no.skatteetaten.aurora.gorg.model.newApplicationDeployment
import no.skatteetaten.aurora.kubernetes.config.ClientTypes
import no.skatteetaten.aurora.kubernetes.KubernetesCoroutinesClient
import no.skatteetaten.aurora.kubernetes.config.TargetClient
import no.skatteetaten.aurora.kubernetes.newLabel
import org.springframework.stereotype.Service
import java.time.Instant
import io.fabric8.kubernetes.api.model.HasMetadata
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Service
class KubernetesService(
    @TargetClient(ClientTypes.SERVICE_ACCOUNT)
    val kubernetesClient: KubernetesCoroutinesClient,
    val meterRegistry: MeterRegistry,
) {

    private fun <T : HasMetadata> List<T>.registerTemporaryResourceMetric(): List<T> {
        val kind = this.first().kind
        val averageByteSize = this.map { bc -> bc.toString().toByteArray().size }.average()
        meterRegistry.gauge("gorg_temporary_resource_avg_size", listOf(Tag.of("resource", kind)), averageByteSize)
        meterRegistry.gaugeCollectionSize("gorg_temporary_resource", listOf(Tag.of("resource", kind)), this)

        return this
    }

    fun findTemporaryProjects(now: Instant = Instant.now()): List<ProjectResource> = runBlocking {
        kubernetesClient.getMany(newProject { metadata { labels = newLabel(REMOVE_AFTER_LABEL) } })
            .filter { it.status.phase != TERMINATING_PHASE }
    }.registerTemporaryResourceMetric()
        .mapNotNull { it.toResource(now) }

    fun findTemporaryBuildConfigs(now: Instant = Instant.now()): List<BuildConfigResource> =
        runBlocking {
            kubernetesClient.getMany(newBuildConfig { metadata { labels = newLabel(REMOVE_AFTER_LABEL) } })
        }.registerTemporaryResourceMetric()
            .mapNotNull { it.toResource(now) }

    fun findTemporaryApplicationDeployments(now: Instant = Instant.now()): List<ApplicationDeploymentResource> =
        runBlocking {
            kubernetesClient.getMany(
                newApplicationDeployment {
                    metadata { labels = newLabel(REMOVE_AFTER_LABEL) }
                }
            )
        }.registerTemporaryResourceMetric()
            .map { it.toResource(now) }
}
