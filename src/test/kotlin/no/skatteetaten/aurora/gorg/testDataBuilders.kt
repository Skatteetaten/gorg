package no.skatteetaten.aurora.gorg

import com.fkorotkov.kubernetes.newObjectMeta
import com.fkorotkov.openshift.metadata
import com.fkorotkov.openshift.newDeploymentConfig
import com.fkorotkov.openshift.newProject
import com.fkorotkov.openshift.status
import io.fabric8.openshift.api.model.DeploymentConfig
import io.fabric8.openshift.api.model.Project
import no.skatteetaten.aurora.gorg.service.TemporaryApplicationDeployment
import no.skatteetaten.aurora.gorg.service.TemporaryProject
import java.time.Duration
import java.time.Instant

data class DeploymentConfigDataBuilder(
    val dcNamespace: String = "namespace",
    val dcKind: String = "Deployment",
    val dcName: String = "name",
    val dcTtl: Instant = Instant.now().plusSeconds(60)
) {

    fun build(): DeploymentConfig =
        newDeploymentConfig {
            kind = dcKind
            metadata = newObjectMeta {
                name = dcName
                namespace = dcNamespace
                labels = mapOf("ttl" to dcTtl.epochSecond.toString())
            }
        }
}

data class ProjectDataBuilder(
    val pName: String = "name",
    val pAffiliation: String = "affiliation",
    val pPhase: String = "phase",
    val pTtl: Instant = Instant.now().plusSeconds(60)
) {

    fun build(): Project =
        newProject {
            status {
                phase = pPhase
            }
            metadata {
                name = pName
                labels = mapOf(
                    "affiliation" to pAffiliation,
                    "ttl" to pTtl.epochSecond.toString()
                )
            }
        }
}

data class TemporaryProjectDataBuilder(
    val name: String = "name",
    val affiliation: String = "affiliation"
) {

    fun build(): TemporaryProject =
        TemporaryProject(
            name = name,
            affiliation = affiliation,
            ttl = Duration.ZERO,
            removalTime = Instant.now()
        )
}

data class TemporaryApplicationDataBuilder(
    val name: String = "name",
    val namespace: String = "namespace"
) {

    fun build(): TemporaryApplicationDeployment =
        TemporaryApplicationDeployment(
            name = name,
            namespace = namespace,
            ttl = Duration.ZERO,
            removalTime = Instant.now()
        )
}