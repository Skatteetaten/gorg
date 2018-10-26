package no.skatteetaten.aurora.gorg.controller

import no.skatteetaten.aurora.gorg.service.DeleteService
import no.skatteetaten.aurora.gorg.service.OpenShiftService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api")
class CrawlController(
    val openShiftService: OpenShiftService,
    val deleteService: DeleteService
) {

    @GetMapping("/projects")
    fun listProjects() = openShiftService.findTemporaryProjects(Instant.now())

    @DeleteMapping("/projects")
    fun deleteProjects() = openShiftService.findTemporaryProjects(Instant.now())
        .filter { it.ttl.isNegative }
        .forEach { deleteService.deleteProject(it) }

    @GetMapping("/buildConfigs")
    fun listBuildConfig() = openShiftService.findTemporaryBuildConfigs(Instant.now())

    @DeleteMapping("/buildConfigs")
    fun deleteBuildConfigs() = openShiftService.findTemporaryBuildConfigs(Instant.now())
        .filter { it.ttl.isNegative }
        .forEach { deleteService.deleteBuildConfig(it) }

    @GetMapping("/applicationDeployments")
    fun listApplicationDeployments() = openShiftService.findTemporaryApplicationDeployments(Instant.now())

    @DeleteMapping("/applicationDeployments")
    fun deleteApplicationDeployments() = openShiftService.findTemporaryApplicationDeployments(Instant.now())
        .filter { it.ttl.isNegative }
        .forEach { deleteService.deleteApplicationDeployment(it) }
}