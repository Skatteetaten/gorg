package no.skatteetaten.aurora.gorg.service

import io.fabric8.kubernetes.client.KubernetesClientException
import io.fabric8.openshift.client.DefaultOpenShiftClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class DeleteService(
    @Value("\${gorg.delete.resources}") val deleteResources: Boolean
    ) {

    val logger = LoggerFactory.getLogger(DeleteService::class.java)

    fun deleteResource(client: DefaultOpenShiftClient, item: TemporaryResource, deleteFunction: (DefaultOpenShiftClient) -> Boolean): Boolean {

        if (deleteResources) {
            try {
                return deleteFunction(client)
                    .also { logger.info("deleteFunction boolean value: $it")
                        if (it)
                        {
                            logger.info("Resource with name: ${item.name} and removalTime: ${item.removalTime} deleted successfully.")
                        } else {

                        }
                    }

            } catch (e: KubernetesClientException){
                logger.error("Deletion of Resource with name : ${item.name} failed with exception : $e")
            }
            return false
        }

        logger.info(
            "deleteResources = false. Type ${item.resourceType} with name ${item.name} time-to-live expired. " +
                "Will be deleted once dryrun flag is false"
        )
        return true
    }
}