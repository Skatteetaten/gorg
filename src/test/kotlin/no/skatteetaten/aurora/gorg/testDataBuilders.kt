package no.skatteetaten.aurora.gorg

import com.fkorotkov.kubernetes.newObjectMeta
import com.fkorotkov.openshift.metadata
import com.fkorotkov.openshift.newBuildConfig
import com.fkorotkov.openshift.newProject
import com.fkorotkov.openshift.status
import io.fabric8.openshift.api.model.BuildConfig
import io.fabric8.openshift.api.model.Project
import no.skatteetaten.aurora.gorg.extensions.REMOVE_AFTER_LABEL
import no.skatteetaten.aurora.gorg.model.ApplicationDeployment
import no.skatteetaten.aurora.gorg.model.newApplicationDeployment
import no.skatteetaten.aurora.gorg.service.ApplicationDeploymentResource
import no.skatteetaten.aurora.gorg.service.BuildConfigResource
import no.skatteetaten.aurora.gorg.service.ProjectResource
import java.time.Duration
import java.time.Instant

data class BuildConfigDataBuilder(
    val bcNamespace: String = "namespace",
    val bcKind: String = "BuildConfig",
    val bcName: String = "name",
    val bcTtl: String? = Instant.now().plusSeconds(60).epochSecond.toString()
) {

    fun build(): BuildConfig =
        newBuildConfig {
            kind = bcKind
            metadata = newObjectMeta {
                name = bcName
                namespace = bcNamespace
                labels = if (bcTtl == null) {
                    emptyMap()
                } else {
                    mapOf(REMOVE_AFTER_LABEL to bcTtl)
                }
            }
        }
}

data class ApplicationDeploymentBuilder(
    val adNamespace: String = "namespace",
    val adName: String = "name",
    val adTtl: Instant = Instant.now().plusSeconds(60)
) {

    fun build(): ApplicationDeployment = newApplicationDeployment {
        metadata {
            name = adName
            namespace = adNamespace
            labels = mapOf(REMOVE_AFTER_LABEL to adTtl.epochSecond.toString())
        }
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

data class ProjectResourceBuilder(val ttl: Duration = Duration.ofSeconds(100)) {
    fun build() =
        ProjectResource("name", "affiliation", ttl, Instant.now().plusSeconds(100))
}

data class BuildConfigResourceBuilder(val ttl: Duration = Duration.ofSeconds(100)) {

    fun build() =
        BuildConfigResource("name", "namespace", "affiliation", ttl, Instant.now().plusSeconds(100))
}

data class ApplicationDeploymentResourceBuilder(val ttl: Duration = Duration.ofSeconds(100)) {

    fun build() =
        ApplicationDeploymentResource("name", "namespace", "affiliation", ttl, Instant.now().plusSeconds(100))
}

data class StatusResourceBuilder(val status: String) {
    fun build() =
        """{"kind":"Status","apiVersion":"v1","metadata":{},"status":"$status"}"""
}
