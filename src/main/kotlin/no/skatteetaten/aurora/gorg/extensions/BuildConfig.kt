package no.skatteetaten.aurora.gorg.extensions

import io.fabric8.openshift.api.model.BuildConfig
import java.time.Duration
import java.time.Instant
import no.skatteetaten.aurora.gorg.service.BuildConfigResource

fun BuildConfig.toResource(now: Instant): BuildConfigResource {
    val removalTime = this.removalTime()

    return BuildConfigResource(
        name = this.metadata.name,
        namespace = this.metadata.namespace,
        ttl = Duration.between(now, removalTime),
        removalTime = removalTime
    )
}
