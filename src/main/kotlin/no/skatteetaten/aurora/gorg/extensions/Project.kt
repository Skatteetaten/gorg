package no.skatteetaten.aurora.gorg.extensions

import io.fabric8.openshift.api.model.Project
import no.skatteetaten.aurora.gorg.service.ProjectResource
import java.time.Duration
import java.time.Instant

fun Project.toResource(now: Instant): ProjectResource {
    val removalTime = this.removalTime()

    return ProjectResource(
        name = this.metadata.name,
        ttl = Duration.between(now, removalTime),
        removalTime = removalTime
    )
}
