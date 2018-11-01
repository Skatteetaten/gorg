package no.skatteetaten.aurora.gorg.controller

import no.skatteetaten.aurora.gorg.service.DeleteService
import no.skatteetaten.aurora.gorg.service.OpenShiftService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class CrawlController(
    val openShiftService: OpenShiftService,
    val deleteService: DeleteService
) {

    @GetMapping("/projects")
    fun listProjects() = openShiftService.findTemporaryProjects()

    @DeleteMapping("/projects")
    fun deleteProjects() = openShiftService.findTemporaryProjects()
        .filter { it.ttl.isNegative }
        .forEach { deleteService.deleteProject(it) }

    @GetMapping("/buildConfigs")
    fun listBuildConfig() = openShiftService.findTemporaryBuildConfigs()

    @DeleteMapping("/buildConfigs")
    fun deleteBuildConfigs() = openShiftService.findTemporaryBuildConfigs()
        .filter { it.ttl.isNegative }
        .forEach { deleteService.deleteBuildConfig(it) }

    @GetMapping("/applicationDeployments")
    fun listApplicationDeployments() = openShiftService.findTemporaryApplicationDeployments()

    @DeleteMapping("/applicationDeployments")
    fun deleteApplicationDeployments() = openShiftService.findTemporaryApplicationDeployments()
        .filter { it.ttl.isNegative }
        .forEach { deleteService.deleteApplicationDeployment(it) }
}