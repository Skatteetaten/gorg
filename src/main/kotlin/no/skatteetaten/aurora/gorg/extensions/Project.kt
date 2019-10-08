package no.skatteetaten.aurora.gorg.extensions

import io.fabric8.openshift.api.model.Project
import java.time.Duration
import java.time.Instant
import no.skatteetaten.aurora.gorg.service.ProjectResource

fun Project.toResource(now: Instant): ProjectResource {
    val removalTime = this.removalTime()

    return ProjectResource(
        name = this.metadata.name,
        ttl = Duration.between(now, removalTime),
        removalTime = removalTime
    )
}
