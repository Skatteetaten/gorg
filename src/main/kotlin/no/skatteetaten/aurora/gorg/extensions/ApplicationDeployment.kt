package no.skatteetaten.aurora.gorg.extensions

import no.skatteetaten.aurora.gorg.model.ApplicationDeployment
import no.skatteetaten.aurora.gorg.service.ApplicationDeploymentResource
import java.time.Duration
import java.time.Instant

fun ApplicationDeployment.toResource(now: Instant): ApplicationDeploymentResource? {
    val removalTime = this.removalTime() ?: return null

    return ApplicationDeploymentResource(
        name = this.metadata.name,
        namespace = this.metadata.namespace,
        affiliation = this.metadata.labels["affiliation"]?.let { it }.toString(),
        ttl = Duration.between(now, removalTime),
        removalTime = removalTime
    )
}
