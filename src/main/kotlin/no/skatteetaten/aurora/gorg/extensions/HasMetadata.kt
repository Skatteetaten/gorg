package no.skatteetaten.aurora.gorg.extensions

import io.fabric8.kubernetes.api.model.HasMetadata
import java.time.Instant
import mu.KotlinLogging

const val REMOVE_AFTER_LABEL = "removeAfter"
const val TERMINATING_PHASE = "Terminating"

private val logger = KotlinLogging.logger {}

fun HasMetadata.removalTime(): Instant? {
    val removeAfterLabel = this.metadata.labels[REMOVE_AFTER_LABEL]
    if (removeAfterLabel == null || removeAfterLabel == "null") {
        logger.warn { "removeAfter equals null and will be ignored kind=${this.kind} namespace=${this.metadata.namespace} name=${this.metadata.name}" }
        return null
    }

    return runCatching {
        Instant.ofEpochSecond(removeAfterLabel.toLong())
    }.recover {
        logger.warn {
            "Not a valid timestamp for removeAfter kind=${this.kind} namespace=${this.metadata.namespace} name=${this.metadata.name}"
        }
        return null
    }.getOrNull()
}
