package no.skatteetaten.aurora.gorg.service

import io.fabric8.openshift.client.DefaultOpenShiftClient
import io.fabric8.openshift.client.OpenShiftClient
import io.micrometer.core.instrument.MeterRegistry
import no.skatteetaten.aurora.gorg.extensions.REMOVE_AFTER_LABEL
import no.skatteetaten.aurora.gorg.extensions.TERMINATING_PHASE
import no.skatteetaten.aurora.gorg.extensions.applicationDeploymentsTemporary
import no.skatteetaten.aurora.gorg.extensions.toResource
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class OpenShiftService(
    val client: OpenShiftClient
) {

    fun findTemporaryProjects(now: Instant = Instant.now()): List<ProjectResource> =
        client.projects()
        .withLabel(REMOVE_AFTER_LABEL)
        .list().items
        .filter { it.status.phase != TERMINATING_PHASE }
        .map { it.toResource(now) }

    fun findTemporaryBuildConfigs(now: Instant = Instant.now()): List<BuildConfigResource> =
        client.buildConfigs()
        .inAnyNamespace()
        .withLabel(REMOVE_AFTER_LABEL)
        .list().items
        .map { it.toResource(now) }

    fun findTemporaryApplicationDeployments(now: Instant = Instant.now()): List<ApplicationDeploymentResource> =
        (client as DefaultOpenShiftClient).applicationDeploymentsTemporary()
            .map { it.toResource(now) }
}
