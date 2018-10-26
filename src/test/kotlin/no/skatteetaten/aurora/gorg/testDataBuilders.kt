package no.skatteetaten.aurora.gorg

import com.fkorotkov.kubernetes.newObjectMeta
import com.fkorotkov.openshift.metadata
import com.fkorotkov.openshift.newBuildConfig
import com.fkorotkov.openshift.newDeploymentConfig
import com.fkorotkov.openshift.newProject
import com.fkorotkov.openshift.status
import io.fabric8.openshift.api.model.BuildConfig
import io.fabric8.openshift.api.model.DeploymentConfig
import io.fabric8.openshift.api.model.Project
import no.skatteetaten.aurora.gorg.extensions.REMOVE_AFTER_LABEL
import no.skatteetaten.aurora.gorg.model.ApplicationDeployment
import java.time.Instant


data class BuildConfigDataBuilder(
    val bcNamespace: String = "namespace",
    val bcKind: String = "Deployment",
    val bcName: String = "name",
    val bcTtl: Instant = Instant.now().plusSeconds(60)
) {

    fun build(): BuildConfig =
        newBuildConfig {
            kind = bcKind
            metadata = newObjectMeta {
                name = bcName
                namespace = bcNamespace
                labels = mapOf(REMOVE_AFTER_LABEL to bcTtl.epochSecond.toString())
            }
        }
}

data class ApplicationDeploymentBuilder(
    val adNamespace: String = "namespace",
    val adKind: String = "ApplicationDeployment",
    val adName: String = "name",
    val adTtl: Instant = Instant.now().plusSeconds(60)
) {

    fun build(): ApplicationDeployment {
        return ApplicationDeployment(
            kind = adKind,
            metadata = newObjectMeta {
                name = adName
                namespace = adNamespace
                labels = mapOf(REMOVE_AFTER_LABEL to adTtl.epochSecond.toString())
            }
        )
    }

}

data class ProjectDataBuilder(
    val pName: String = "name",
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
                    REMOVE_AFTER_LABEL to pTtl.epochSecond.toString()
                )
            }
        }
}

