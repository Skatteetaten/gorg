package no.skatteetaten.aurora.gorg.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import io.fabric8.kubernetes.api.model.ObjectMeta
import no.skatteetaten.aurora.gorg.extensions.REMOVE_AFTER_LABEL
import no.skatteetaten.aurora.gorg.service.ProjectResource
import java.time.Duration
import java.time.Instant

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Project(
    val kind: String = "Project",
    val metadata: ObjectMeta,
    val apiVersion: String = "skatteetaten.no/v1"
) {

    fun toResource(now: Instant): ProjectResource {
        val removalTime = this.removalTime()

        return ProjectResource(
            name = this.metadata.name,
            affiliation = this.metadata.labels["affiliation"]?.let { it }.toString(),
            ttl = Duration.between(now, removalTime),
            removalTime = removalTime
        )
    }

    fun removalTime(): Instant {
        return this.metadata.labels[REMOVE_AFTER_LABEL]?.let {
            Instant.ofEpochSecond(it.toLong())
        } ?: throw IllegalStateException("removeAfter is not set or valid timstamp")
    }
}