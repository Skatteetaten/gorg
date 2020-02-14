package no.skatteetaten.aurora.gorg.service

import com.fkorotkov.openshift.metadata
import com.fkorotkov.openshift.newBuildConfig
import com.fkorotkov.openshift.newProject
import io.fabric8.openshift.client.DefaultOpenShiftClient
import kotlinx.coroutines.runBlocking
import no.skatteetaten.aurora.gorg.extensions.REMOVE_AFTER_LABEL
import no.skatteetaten.aurora.gorg.extensions.TERMINATING_PHASE
import no.skatteetaten.aurora.gorg.extensions.applicationDeploymentsTemporary
import no.skatteetaten.aurora.gorg.extensions.toResource
import no.skatteetaten.aurora.kubernetes.ClientTypes
import no.skatteetaten.aurora.kubernetes.KubernetesClient
import no.skatteetaten.aurora.kubernetes.TargetClient
import no.skatteetaten.aurora.kubernetes.crd.newSkatteetatenKubernetesResource
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class KubernetesService(
    @TargetClient(ClientTypes.SERVICE_ACCOUNT)
    val kubernetesClient: KubernetesClient
) {

/*    fun findTemporaryProjects(now: Instant = Instant.now()): List<ProjectResource> =
        client.projects()
            .withLabel(REMOVE_AFTER_LABEL)
            .list().items
            .filter { it.status.phase != TERMINATING_PHASE }
            .map { it.toResource(now) }*/

    fun findTemporaryProjects(now: Instant = Instant.now()): List<ProjectResource> =
        runBlocking {
            kubernetesClient.getList(newProject { }).items
                .filter { it.status.phase != TERMINATING_PHASE }
                .map { it.toResource(now) }
        }

/*    fun findTemporaryBuildConfigs(now: Instant = Instant.now()): List<BuildConfigResource> =
        client.buildConfigs()
            .inAnyNamespace()
            .withLabel(REMOVE_AFTER_LABEL)
            .list().items
            .map { it.toResource(now) }*/

    fun findTemporaryBuildConfigs(now: Instant = Instant.now()): List<BuildConfigResource> =
        runBlocking { kubernetesClient.getList(newBuildConfig{ metadata { labels[REMOVE_AFTER_LABEL] }}).items
            .map { it.toResource(now) }
        }


/*
    fun findTemporaryApplicationDeployments(now: Instant = Instant.now()): List<ApplicationDeploymentResource> =
        (client as DefaultOpenShiftClient).applicationDeploymentsTemporary()
            .map { it.toResource(now) }

    fun findTemporaryApplicationDeployments(now: Instant = Instant.now()): List<ApplicationDeploymentResource> =
        runBlocking { kubernetesClient.getList(newSkatteetatenKubernetesResource<ApplicationDeploymentResource> { metadata { labels[REMOVE_AFTER_LABEL] } }).items
            .map { it.toResource(now) }
        }
*/


}