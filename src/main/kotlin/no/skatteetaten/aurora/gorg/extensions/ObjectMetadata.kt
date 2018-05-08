package no.skatteetaten.aurora.gorg.extensions

import io.fabric8.kubernetes.api.model.HasMetadata
import java.lang.IllegalStateException
import java.time.Instant

val REMOVE_AFTER_LABEL = "removeAfter"
val TERMINATING_PHASE = "Terminating"

fun HasMetadata.removalTime(): Instant {
    return this.metadata.labels[REMOVE_AFTER_LABEL]?.let {
        Instant.ofEpochSecond(it.toLong())
    } ?: throw IllegalStateException("removeAfter is not set or valid timstamp")
}

fun <T : HasMetadata> List<T>.isBefore(now: Instant): List<T> {
    return this.filter { it.removalTime()?.isBefore(now) ?: false }
}