package no.skatteetaten.aurora.gorg.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class DeleteService(@Value("\${gorg.delete.resources}") val deleteResources: Boolean) {

    val logger = LoggerFactory.getLogger(DeleteService::class.java)

    fun deleteResource(item: TemporaryResource, deleteFunction: (TemporaryResource) -> Boolean): Boolean {

        logger.info("Found ${item.resourceType} to devour: ${item.name}. time-to-live expired ${item.removalTime}")

        if (deleteResources) {
            logger.info(
                "deleteResources = true. Type ${item.resourceType} with name ${item.name} time-to-live expired. " +
                    "Will be deleted once dryrun flag is false"
            )
            return true
        }
        return deleteFunction(item) //try catch this part.
            .also {
                if (it) {
                    logger.info("Type ${item.resourceType} with name ${item.name}gobbled, tastes like chicken!")
                } else {
                    logger.error("Unable to delete project=${item.name}")
                }
            }
    }
}