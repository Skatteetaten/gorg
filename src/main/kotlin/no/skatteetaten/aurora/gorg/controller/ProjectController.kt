package no.skatteetaten.aurora.gorg.controller

import no.skatteetaten.aurora.gorg.service.CrawlService
import no.skatteetaten.aurora.gorg.service.DeleteService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/project")
class ProjectController(val crawler: CrawlService, val deletionService: DeleteService) {

    @DeleteMapping
    fun deleteProjects() {
        val now = Instant.now()
        crawler.findTemporaryProjects(now)
                .filter { it.ttl.isNegative }
                .forEach { deletionService.deleteProject(it) }

    }

    @GetMapping
    fun list(): List<CrawlService.TemporaryProject> {
        val now = Instant.now()
        return crawler.findTemporaryProjects(now)
    }

}
