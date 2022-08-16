package no.skatteetaten.aurora.gorg.extensions

import io.fabric8.kubernetes.api.model.HasMetadata
import java.lang.IllegalStateException
import java.time.Instant
import mu.KotlinLogging

const val REMOVE_AFTER_LABEL = "removeAfter"
const val TERMINATING_PHASE = "Terminating"

private val logger = KotlinLogging.logger {}

fun HasMetadata.removalTime(): Instant? {
    return this.metadata.labels[REMOVE_AFTER_LABEL]?.let {
        if (it == "null") {
            logger.info { "Not a valid timestamp for removeAfter kind=${this.kind} namespace=${this.metadata.namespace} name=${this.metadata.name}" }
            return null
        }
        Instant.ofEpochSecond(it.toLong())
    }
        ?: throw IllegalStateException("removeAfter is not set or valid timestamp kind=${this.kind} namespace=${this.metadata.namespace} name=${this.metadata.name}")
}
