package no.skatteetaten.aurora.gorg.controller

import no.skatteetaten.aurora.gorg.service.CrawlService
import no.skatteetaten.aurora.gorg.service.DeleteService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/apps")
class ApplicationController(val crawler: CrawlService, val deletionService: DeleteService) {

    @DeleteMapping
    fun deleteApplications() {
        val now = Instant.now()
        crawler.findTemporaryApplications(now)
                .filter { it.ttl.isNegative }
                .forEach { deletionService.deleteApplication(it) }

    }

    @GetMapping
    fun list(): List<CrawlService.TemporaryApplication> {
        val now = Instant.now()
        return crawler.findTemporaryApplications(now)
    }

}
