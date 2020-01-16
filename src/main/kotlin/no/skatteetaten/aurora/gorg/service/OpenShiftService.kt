package no.skatteetaten.aurora.gorg.service

import io.fabric8.openshift.client.DefaultOpenShiftClient
import io.fabric8.openshift.client.OpenShiftClient
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import no.skatteetaten.aurora.gorg.extensions.REMOVE_AFTER_LABEL
import no.skatteetaten.aurora.gorg.extensions.TERMINATING_PHASE
import no.skatteetaten.aurora.gorg.extensions.applicationDeploymentsTemporary
import no.skatteetaten.aurora.gorg.extensions.toResource
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.TimeUnit

@Service
class OpenShiftService(
    val client: OpenShiftClient,
    val meterRegistry: MeterRegistry
) {

    fun findTemporaryProjects(now: Instant = Instant.now()): List<ProjectResource> {
        val start = System.currentTimeMillis()

        val projectResources = client.projects()
            .withLabel(REMOVE_AFTER_LABEL)
            .list().items
            .filter { it.status.phase != TERMINATING_PHASE }
            .map { it.toResource(now) }

        meterRegistry.timer("openshift_api_request", listOf(Tag.of("client","OpenShiftClient"))).record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS)

        return projectResources
    }

    fun findTemporaryBuildConfigs(now: Instant = Instant.now()): List<BuildConfigResource> {
        val start = System.currentTimeMillis()

        val buildConfigs = client.buildConfigs()
            .inAnyNamespace()
            .withLabel(REMOVE_AFTER_LABEL)
            .list().items
            .map { it.toResource(now) }

        meterRegistry.timer("openshift_api_request", listOf(Tag.of("client","OpenShiftClient"))).record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS)

        return buildConfigs
    }

    fun findTemporaryApplicationDeployments(now: Instant = Instant.now()): List<ApplicationDeploymentResource> {
       val start = System.currentTimeMillis()

       val applicationDeployments = (client as DefaultOpenShiftClient).applicationDeploymentsTemporary()
            .map { it.toResource(now) }

        meterRegistry.timer("openshift_api_request", listOf(Tag.of("client","http"))).record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS)

        return applicationDeployments
    }
}
