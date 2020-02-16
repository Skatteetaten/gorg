package no.skatteetaten.aurora.gorg.service

import com.fkorotkov.openshift.metadata
import com.fkorotkov.openshift.newBuildConfig
import com.fkorotkov.openshift.newProject
import kotlinx.coroutines.runBlocking
import no.skatteetaten.aurora.gorg.extensions.REMOVE_AFTER_LABEL
import no.skatteetaten.aurora.gorg.extensions.TERMINATING_PHASE
import no.skatteetaten.aurora.gorg.extensions.toResource
import no.skatteetaten.aurora.gorg.model.ApplicationDeployment
import no.skatteetaten.aurora.gorg.model.ApplicationDeploymentList
import no.skatteetaten.aurora.kubernetes.ClientTypes
import no.skatteetaten.aurora.kubernetes.KubernetesClient
import no.skatteetaten.aurora.kubernetes.TargetClient
import no.skatteetaten.aurora.kubernetes.crd.newSkatteetatenKubernetesResource
import no.skatteetaten.aurora.kubernetes.newLabel
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class KubernetesService(
    @TargetClient(ClientTypes.SERVICE_ACCOUNT)
    val kubernetesClient: KubernetesClient
) {

    fun findTemporaryProjects(now: Instant = Instant.now()): List<ProjectResource> =
        runBlocking {
            kubernetesClient.getList(newProject { metadata { labels = newLabel(REMOVE_AFTER_LABEL) } }).items
                .filter { it.status.phase != TERMINATING_PHASE }
                .map { it.toResource(now) }
        }

    fun findTemporaryBuildConfigs(now: Instant = Instant.now()): List<BuildConfigResource> =
        runBlocking {
            kubernetesClient.getList(newBuildConfig { metadata { labels = newLabel(REMOVE_AFTER_LABEL) } }).items
                .map { it.toResource(now) }
        }

    fun findTemporaryApplicationDeployments(now: Instant = Instant.now()): List<ApplicationDeploymentResource> {
        var ads: ApplicationDeploymentList
        var returnList: List<ApplicationDeploymentResource> = emptyList()

        runBlocking {
            ads = kubernetesClient.getResource(newSkatteetatenKubernetesResource<ApplicationDeployment> {
                metadata {
                    labels = newLabel(REMOVE_AFTER_LABEL)
                }
            })
            returnList = ads.items.map { it.toResource(now) }
        }
        return returnList
    }
}
