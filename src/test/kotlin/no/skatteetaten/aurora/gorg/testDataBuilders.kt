package no.skatteetaten.aurora.gorg

import com.fkorotkov.kubernetes.newObjectMeta
import com.fkorotkov.openshift.metadata
import com.fkorotkov.openshift.newDeploymentConfig
import com.fkorotkov.openshift.newProject
import com.fkorotkov.openshift.status
import io.fabric8.openshift.api.model.DeploymentConfig
import io.fabric8.openshift.api.model.Project
import java.time.Instant

data class DeploymentConfigDataBuilder(val dcNamespace: String = "namespace",
                                       val dcKind: String = "Deployment",
                                       val dcName: String = "name") {

    fun build(): DeploymentConfig {
        return newDeploymentConfig {
            kind = dcKind
            metadata = newObjectMeta {
                name = dcName
                namespace = dcNamespace
                labels = mapOf("removeAfter" to Instant.now().epochSecond.toString())
            }
        }
    }
}

data class ProjectDataBuilder(
        val pName: String = "name",
        val pAffiliation: String = "affiliation",
        val pPhase: String = "phase") {

    fun build(): Project {
        return newProject {
            status {
                phase = pPhase
            }
            metadata {
                name = pName
                labels = mapOf("affiliation" to pAffiliation,
                        "removeAfter" to Instant.now().epochSecond.toString())
            }
        }
    }
}