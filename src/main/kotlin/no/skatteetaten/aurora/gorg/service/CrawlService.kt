package no.skatteetaten.aurora.gorg.service

import io.fabric8.openshift.client.DefaultOpenShiftClient
import no.skatteetaten.aurora.model.ProjectInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service


@Service
class CrawlService {

    val logger: Logger = LoggerFactory.getLogger(CrawlService::class.java)

    fun findAllProjects(): List<String> {
        return DefaultOpenShiftClient().projects().list().items
                .filter { it.metadata.labels != null }
                .filter { it.metadata.labels.containsKey("removeAfter") }
                //.filter { it.metadata.namespace }
                .map {
                    val epoch = System.currentTimeMillis() / 1000
                    println(epoch)
                    it.metadata.labels.get("removeAfter") ?: ""
                }
    }


    fun listTtl(): List<ProjectInfo> {
        return DefaultOpenShiftClient().projects().list().items
                .filter { it.metadata.labels != null }
                .filter { it.metadata.labels.containsKey("removeAfter") }
                .map {
                    val removeAfter = it.metadata.labels.get("removeAfter")?.toLong() ?: 0
                    ProjectInfo( // Creates objects that holds Projectname and ttl time
                            it.metadata.name,
                            removeAfter
                    )
                }.filter { // Only includes projects that have expired
                    val epoch = System.currentTimeMillis() / 1000
                    it.removeAfter < epoch
                }
    }

    @Scheduled(fixedRate = 10_000, initialDelay = 10_000)
    fun testKim() {
        logger.info("Searching for project to delete")

        val namespacesToRemove = listTtl()

                .filter { it.name == "paas-kim-dev" }
                .forEach {
                    val removeAfterDate = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(java.util.Date(it.removeAfter * 1000)) // converts epoch to date
                    logger.info("finding project to remove: ${it.name}. Time-to-live expired $removeAfterDate")
                    DefaultOpenShiftClient().namespaces().withName(it.name).delete()
                }


    }

}