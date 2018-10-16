package no.skatteetaten.aurora.gorg.extensions

import io.fabric8.kubernetes.api.model.HasMetadata
import java.lang.IllegalStateException
import java.time.Instant

const val REMOVE_AFTER_LABEL = "ttl"
const val TERMINATING_PHASE = "Terminating"

fun ApplicationDeployment.removalTime(): Instant {
    return this.metadata.labels[REMOVE_AFTER_LABEL]?.let {
        Instant.ofEpochSecond(it.toLong())
    } ?: throw IllegalStateException("ttl is not set or valid timstamp")
}


fun HasMetadata.removalTime(): Instant {
    return this.metadata.labels[REMOVE_AFTER_LABEL]?.let {
        Instant.ofEpochSecond(it.toLong())
    } ?: throw IllegalStateException("ttl is not set or valid timstamp")
}

