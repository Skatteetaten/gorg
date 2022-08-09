package no.skatteetaten.aurora.gorg.extensions

import io.fabric8.openshift.api.model.BuildConfig
import no.skatteetaten.aurora.gorg.service.BuildConfigResource
import java.time.Duration
import java.time.Instant

fun BuildConfig.toResource(now: Instant): BuildConfigResource? {
    val removalTime = this.removalTime() ?: return null

    return BuildConfigResource(
        name = this.metadata.name,
        namespace = this.metadata.namespace,
        affiliation = this.metadata.labels["affiliation"]?.let { it }.toString(),
        ttl = Duration.between(now, removalTime),
        removalTime = removalTime
    )
}
