package no.skatteetaten.aurora.gorg.controller

import no.skatteetaten.aurora.gorg.service.ApplicationDeploymentService
import no.skatteetaten.aurora.gorg.service.TemporaryApplicationDeployment
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/apps")
class ApplicationDeploymentController(val applicationDeploymentService: ApplicationDeploymentService) {

    @DeleteMapping
    fun deleteApplicationDeployments() {
        applicationDeploymentService.findTemporaryApplicationDeployments(Instant.now())
                .filter { it.ttl.isNegative }
                .forEach { applicationDeploymentService.deleteApplicationDeployment(it) }
    }

    @GetMapping
    fun list(): List<TemporaryApplicationDeployment> =
        applicationDeploymentService.findTemporaryApplicationDeployments(Instant.now())
}
