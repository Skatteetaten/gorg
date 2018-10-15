package no.skatteetaten.aurora.gorg.controller

import no.skatteetaten.aurora.gorg.service.ApplicationService
import no.skatteetaten.aurora.gorg.service.TemporaryApplication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/apps")
class ApplicationController(val applicationService: ApplicationService) {

    @DeleteMapping
    fun deleteApplications() {
        applicationService.findTemporaryApplications(Instant.now())
                .filter { it.ttl.isNegative }
                .forEach { applicationService.deleteApplication(it) }
    }

    @GetMapping
    fun list(): List<TemporaryApplication> =
        applicationService.findTemporaryApplications(Instant.now())
}
