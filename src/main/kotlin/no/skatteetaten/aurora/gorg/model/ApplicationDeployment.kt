package no.skatteetaten.aurora.gorg.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import no.skatteetaten.aurora.kubernetes.crd.SkatteetatenCRD
import java.time.Duration
import java.time.Instant

fun newApplicationDeployment(block: ApplicationDeployment.() -> Unit = {}): ApplicationDeployment {
    val instance = ApplicationDeployment()
    instance.block()
    return instance
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = ["apiVersion", "kind", "metadata", "spec"])
@JsonDeserialize(using = JsonDeserializer.None::class)
class ApplicationDeployment(
    var spec: ApplicationDeploymentSpec = ApplicationDeploymentSpec()
) : SkatteetatenCRD("ApplicationDeployment")

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)

data class ApplicationDeploymentSpec(
    val name: String = "",
    val namespace: String = "",
    val affiliation: String? = null,
    val ttl: Duration? = null,
    val removalTime: Instant? = null
)
