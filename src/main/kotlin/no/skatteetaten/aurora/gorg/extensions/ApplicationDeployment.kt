package no.skatteetaten.aurora.gorg.extensions

import no.skatteetaten.aurora.gorg.service.ApplicationDeploymentResource
import no.skatteetaten.aurora.kubernetes.ApplicationDeployment
import java.time.Duration
import java.time.Instant

fun ApplicationDeployment.toResource(now: Instant): ApplicationDeploymentResource {
    val removalTime = this.removalTime()

    return ApplicationDeploymentResource(
        name = this.metadata.name,
        namespace = this.metadata.namespace,
        affiliation = this.metadata.labels["affiliation"]?.let { it }.toString(),
        ttl = Duration.between(now, removalTime),
        removalTime = removalTime
    )
}

    fun ApplicationDeployment.removalTime(): Instant {
        return this.metadata.labels[REMOVE_AFTER_LABEL]?.let {
            Instant.ofEpochSecond(it.toLong())
        } ?: throw IllegalStateException("removeAfter is not set or valid timstamp")
    }
