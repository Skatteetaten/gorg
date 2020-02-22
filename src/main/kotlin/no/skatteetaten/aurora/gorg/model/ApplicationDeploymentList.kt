package no.skatteetaten.aurora.gorg.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import no.skatteetaten.aurora.gorg.extensions.REMOVE_AFTER_LABEL
import no.skatteetaten.aurora.gorg.service.ApplicationDeploymentResource
import no.skatteetaten.aurora.kubernetes.crd.SkatteetatenCRD
import java.time.Duration
import java.time.Instant

fun newApplicationDeployment(block: ApplicationDeployment.() -> Unit = {}): ApplicationDeployment {
    val instance = ApplicationDeployment(ApplicationDeploymentSpec())
    instance.block()
    return instance
}

@JsonPropertyOrder(value = ["apiVersion", "kind", "metadata", "spec"])
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializer.None::class)
data class ApplicationDeployment(
    var spec: ApplicationDeploymentSpec
) : SkatteetatenCRD("ApplicationDeployment") {

    fun toResource(now: Instant): ApplicationDeploymentResource {
        val removalTime = this.removalTime()

        return ApplicationDeploymentResource(
            name = this.metadata.name,
            namespace = this.metadata.namespace,
            affiliation = this.metadata.labels["affiliation"]?.let { it }.toString(),
            ttl = Duration.between(now, removalTime),
            removalTime = removalTime
        )
    }

    fun removalTime(): Instant {
        return this.metadata.labels[REMOVE_AFTER_LABEL]?.let {
            Instant.ofEpochSecond(it.toLong())
        }
            ?: throw IllegalStateException("removeAfter is not set or valid timstamp $this , $this.metadata.labels[REMOVE_AFTER_LABEL]? ")
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApplicationDeploymentSpec(
    val name: String = "",
    val namespace: String = "",
    val affiliation: String = "",
    val ttl: Duration? = null,
    val removalTime: Instant? = null
)
