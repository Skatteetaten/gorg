package no.skatteetaten.aurora.gorg.service

import com.fkorotkov.kubernetes.newObjectMeta
import com.fkorotkov.openshift.metadata
import com.fkorotkov.openshift.newBuildConfig
import io.fabric8.openshift.api.model.Project
import kotlinx.coroutines.runBlocking
import no.skatteetaten.aurora.gorg.extensions.REMOVE_AFTER_LABEL
import no.skatteetaten.aurora.gorg.extensions.TERMINATING_PHASE
import no.skatteetaten.aurora.gorg.extensions.toResource
import no.skatteetaten.aurora.gorg.model.newApplicationDeployment
import no.skatteetaten.aurora.kubernetes.ClientTypes
import no.skatteetaten.aurora.kubernetes.KubernetesCoroutinesClient
import no.skatteetaten.aurora.kubernetes.TargetClient
import no.skatteetaten.aurora.kubernetes.newLabel
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class KubernetesService(
    @TargetClient(ClientTypes.SERVICE_ACCOUNT)
    val kubernetesClient: KubernetesCoroutinesClient
) {

    fun findTemporaryProjects(now: Instant = Instant.now()): List<ProjectResource> = runBlocking {
        kubernetesClient.getMany<Project>(newObjectMeta { labels = mapOf(REMOVE_AFTER_LABEL to "") })
            .filter { it.status.phase != TERMINATING_PHASE }
    }.map { it.toResource(now) }

    fun findTemporaryBuildConfigs(now: Instant = Instant.now()): List<BuildConfigResource> =
        runBlocking {
            kubernetesClient.getMany(newBuildConfig { metadata { labels = newLabel(REMOVE_AFTER_LABEL) } })
        }.map { it.toResource(now) }

    fun findTemporaryApplicationDeployments(now: Instant = Instant.now()): List<ApplicationDeploymentResource> =
        runBlocking {
            kubernetesClient.getMany(newApplicationDeployment {
                metadata { labels = newLabel(REMOVE_AFTER_LABEL) }
            })
        }.map { it.toResource(now) }
}
