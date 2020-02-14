package no.skatteetaten.aurora.gorg.controller

import no.skatteetaten.aurora.gorg.service.DeleteService
import no.skatteetaten.aurora.gorg.service.KubernetesService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class CrawlController(
    val kubernetesService: KubernetesService,
    val deleteService: DeleteService
) {
    @GetMapping("/projects")
    fun listProjects() = kubernetesService.findTemporaryProjects()

    @DeleteMapping("/projects")
    fun deleteProjects() = kubernetesService.findTemporaryProjects()
        .filter { it.ttl.isNegative }
        .forEach { deleteService.deleteProject(it) }

    @GetMapping("/buildConfigs")
    fun listBuildConfig() = kubernetesService.findTemporaryBuildConfigs()


/*    @DeleteMapping("/buildConfigs")
    fun deleteBuildConfigs() = kubernetesService.findTemporaryBuildConfigs()
        .filter { it.ttl.isNegative }
        .forEach { deleteService.deleteBuildConfig(it) }

    @GetMapping("/applicationDeployments")
    fun listApplicationDeployments() = kubernetesService.findTemporaryApplicationDeployments()

    @DeleteMapping("/applicationDeployments")
    fun deleteApplicationDeployments() = kubernetesService.findTemporaryApplicationDeployments()
        .filter { it.ttl.isNegative }
        .forEach { deleteService.deleteApplicationDeployment(it) }
*/}
